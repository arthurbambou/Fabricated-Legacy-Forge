package cpw.mods.fml.common.registry;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedBytes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnEntry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.biome.Biome;

public class EntityRegistry {
    private static final EntityRegistry INSTANCE = new EntityRegistry();
    private BitSet availableIndicies;
    private ListMultimap<ModContainer, EntityRegistry.EntityRegistration> entityRegistrations = ArrayListMultimap.create();
    private Map<String, ModContainer> entityNames = Maps.newHashMap();
    private BiMap<Class<? extends Entity>, EntityRegistry.EntityRegistration> entityClassRegistrations = HashBiMap.create();

    public static EntityRegistry instance() {
        return INSTANCE;
    }

    private EntityRegistry() {
        this.availableIndicies = new BitSet(256);
        this.availableIndicies.set(1, 255);

        for(Object id : EntityType.ID_CLASS_MAP.keySet()) {
            this.availableIndicies.clear((Integer)id);
        }

    }

    public static void registerModEntity(
            Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates
    ) {
        instance().doModEntityRegistration(entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
    }

    private void doModEntityRegistration(
            Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates
    ) {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        EntityRegistry.EntityRegistration er = new EntityRegistry.EntityRegistration(
                mc, entityClass, entityName, id, trackingRange, updateFrequency, sendsVelocityUpdates
        );

        try {
            this.entityClassRegistrations.put(entityClass, er);
            this.entityNames.put(entityName, mc);
            if (!EntityType.CLASS_NAME_MAP.containsKey(entityClass)) {
                String entityModName = String.format("%s.%s", mc.getModId(), entityName);
                EntityType.CLASS_NAME_MAP.put(entityClass, entityModName);
                EntityType.NAME_CLASS_MAP.put(entityModName, entityClass);
                FMLLog.finest("Automatically registered mod %s entity %s as %s", new Object[]{mc.getModId(), entityName, entityModName});
            } else {
                FMLLog.fine("Skipping automatic mod %s entity registration for already registered class %s", new Object[]{mc.getModId(), entityClass.getName()});
            }
        } catch (IllegalArgumentException var11) {
            FMLLog.log(
                    Level.WARNING,
                    var11,
                    "The mod %s tried to register the entity (name,class) (%s,%s) one or both of which are already registered",
                    new Object[]{mc.getModId(), entityName, entityClass.getName()}
            );
            return;
        }

        this.entityRegistrations.put(mc, er);
    }

    public static void registerGlobalEntityID(Class<? extends Entity> entityClass, String entityName, int id) {
        if (EntityType.CLASS_NAME_MAP.containsKey(entityClass)) {
            ModContainer activeModContainer = Loader.instance().activeModContainer();
            String modId = "unknown";
            if (activeModContainer != null) {
                modId = activeModContainer.getModId();
            } else {
                FMLLog.severe(
                        "There is a rogue mod failing to register entities from outside the context of mod loading. This is incredibly dangerous and should be stopped.",
                        new Object[0]
                );
            }

            FMLLog.warning(
                    "The mod %s tried to register the entity class %s which was already registered - if you wish to override default naming for FML mod entities, register it here first",
                    new Object[]{modId, entityClass}
            );
        } else {
            id = instance().validateAndClaimId(id);
            EntityType.registerEntity(entityClass, entityName, id);
        }
    }

    private int validateAndClaimId(int id) {
        int realId = id;
        if (id < -128) {
            FMLLog.warning(
                    "Compensating for modloader out of range compensation by mod : entityId %d for mod %s is now %d",
                    new Object[]{id, Loader.instance().activeModContainer().getModId(), id}
            );
            realId = id + 3000;
        }

        if (realId < 0) {
            realId += 127;
        }

        try {
            UnsignedBytes.checkedCast((long)realId);
        } catch (IllegalArgumentException var4) {
            FMLLog.log(
                    Level.SEVERE,
                    "The entity ID %d for mod %s is not an unsigned byte and may not work",
                    new Object[]{id, Loader.instance().activeModContainer().getModId()}
            );
        }

        if (!this.availableIndicies.get(realId)) {
            FMLLog.severe(
                    "The mod %s has attempted to register an entity ID %d which is already reserved. This could cause severe problems",
                    new Object[]{Loader.instance().activeModContainer().getModId(), id}
            );
        }

        this.availableIndicies.clear(realId);
        return realId;
    }

    public static void registerGlobalEntityID(Class<? extends Entity> entityClass, String entityName, int id, int backgroundEggColour, int foregroundEggColour) {
        if (EntityType.CLASS_NAME_MAP.containsKey(entityClass)) {
            ModContainer activeModContainer = Loader.instance().activeModContainer();
            String modId = "unknown";
            if (activeModContainer != null) {
                modId = activeModContainer.getModId();
            } else {
                FMLLog.severe(
                        "There is a rogue mod failing to register entities from outside the context of mod loading. This is incredibly dangerous and should be stopped.",
                        new Object[0]
                );
            }

            FMLLog.warning(
                    "The mod %s tried to register the entity class %s which was already registered - if you wish to override default naming for FML mod entities, register it here first",
                    new Object[]{modId, entityClass}
            );
        } else {
            instance().validateAndClaimId(id);
            EntityType.registerEntity(entityClass, entityName, id, backgroundEggColour, foregroundEggColour);
        }
    }

    public static void addSpawn(Class<? extends MobEntity> entityClass, int weightedProb, int min, int max, EntityCategory typeOfCreature, Biome... biomes) {
        for(Biome biome : biomes) {
            List<SpawnEntry> spawns = biome.getSpawnEntries(typeOfCreature);

            for(SpawnEntry entry : spawns) {
                if (entry.type == entityClass) {
                    entry.weight = weightedProb;
                    entry.minGroupSize = min;
                    entry.maxGroupSize = max;
                    break;
                }
            }

            spawns.add(new SpawnEntry(entityClass, weightedProb, min, max));
        }

    }

    public static void addSpawn(String entityName, int weightedProb, int min, int max, EntityCategory spawnList, Biome... biomes) {
        Class<? extends Entity> entityClazz = (Class)EntityType.NAME_CLASS_MAP.get(entityName);
        if (MobEntity.class.isAssignableFrom(entityClazz)) {
            addSpawn((Class<? extends MobEntity>) entityClazz, weightedProb, min, max, spawnList, biomes);
        }

    }

    public static void removeSpawn(Class<? extends MobEntity> entityClass, EntityCategory typeOfCreature, Biome... biomes) {
        for(Biome biome : biomes) {
            Iterator<SpawnEntry> spawns = biome.getSpawnEntries(typeOfCreature).iterator();

            while(spawns.hasNext()) {
                SpawnEntry entry = (SpawnEntry)spawns.next();
                if (entry.type == entityClass) {
                    spawns.remove();
                }
            }
        }

    }

    public static void removeSpawn(String entityName, EntityCategory spawnList, Biome... biomes) {
        Class<? extends Entity> entityClazz = (Class)EntityType.NAME_CLASS_MAP.get(entityName);
        if (MobEntity.class.isAssignableFrom(entityClazz)) {
            removeSpawn((Class<? extends MobEntity>) entityClazz, spawnList, biomes);
        }

    }

    public static int findGlobalUniqueEntityId() {
        int res = instance().availableIndicies.nextSetBit(0);
        if (res < 0) {
            throw new RuntimeException("No more entity indicies left");
        } else {
            return res;
        }
    }

    public EntityRegistry.EntityRegistration lookupModSpawn(Class<? extends Entity> clazz, boolean keepLooking) {
        Class<?> localClazz = clazz;

        do {
            EntityRegistry.EntityRegistration er = (EntityRegistry.EntityRegistration)this.entityClassRegistrations.get(localClazz);
            if (er != null) {
                return er;
            }

            localClazz = localClazz.getSuperclass();
            keepLooking = !Object.class.equals(localClazz);
        } while(keepLooking);

        return null;
    }

    public EntityRegistry.EntityRegistration lookupModSpawn(ModContainer mc, int modEntityId) {
        for(EntityRegistry.EntityRegistration er : this.entityRegistrations.get(mc)) {
            if (er.getModEntityId() == modEntityId) {
                return er;
            }
        }

        return null;
    }

    public boolean tryTrackingEntity(EntityTracker entityTracker, Entity entity) {
        EntityRegistry.EntityRegistration er = this.lookupModSpawn(entity.getClass(), true);
        if (er != null) {
            entityTracker.startTracking(entity, er.getTrackingRange(), er.getUpdateFrequency(), er.sendsVelocityUpdates());
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    public static EntityRegistry.EntityRegistration registerModLoaderEntity(
            Object mod, Class<? extends Entity> entityClass, int entityTypeId, int updateRange, int updateInterval, boolean sendVelocityInfo
    ) {
        String entityName = (String)EntityType.CLASS_NAME_MAP.get(entityClass);
        if (entityName == null) {
            throw new IllegalArgumentException(
                    String.format(
                            "The ModLoader mod %s has tried to register an entity tracker for a non-existent entity type %s",
                            Loader.instance().activeModContainer().getModId(),
                            entityClass.getCanonicalName()
                    )
            );
        } else {
            instance().doModEntityRegistration(entityClass, entityName, entityTypeId, mod, updateRange, updateInterval, sendVelocityInfo);
            return (EntityRegistry.EntityRegistration)instance().entityClassRegistrations.get(entityClass);
        }
    }

    public class EntityRegistration {
        private Class<? extends Entity> entityClass;
        private ModContainer container;
        private String entityName;
        private int modId;
        private int trackingRange;
        private int updateFrequency;
        private boolean sendsVelocityUpdates;
        private Function<EntitySpawnPacket, Entity> customSpawnCallback;
        private boolean usesVanillaSpawning;

        public EntityRegistration(
                ModContainer mc,
                Class<? extends Entity> entityClass,
                String entityName,
                int id,
                int trackingRange,
                int updateFrequency,
                boolean sendsVelocityUpdates
        ) {
            this.container = mc;
            this.entityClass = entityClass;
            this.entityName = entityName;
            this.modId = id;
            this.trackingRange = trackingRange;
            this.updateFrequency = updateFrequency;
            this.sendsVelocityUpdates = sendsVelocityUpdates;
        }

        public Class<? extends Entity> getEntityClass() {
            return this.entityClass;
        }

        public ModContainer getContainer() {
            return this.container;
        }

        public String getEntityName() {
            return this.entityName;
        }

        public int getModEntityId() {
            return this.modId;
        }

        public int getTrackingRange() {
            return this.trackingRange;
        }

        public int getUpdateFrequency() {
            return this.updateFrequency;
        }

        public boolean sendsVelocityUpdates() {
            return this.sendsVelocityUpdates;
        }

        public boolean usesVanillaSpawning() {
            return this.usesVanillaSpawning;
        }

        public boolean hasCustomSpawning() {
            return this.customSpawnCallback != null;
        }

        public Entity doCustomSpawning(EntitySpawnPacket packet) throws Exception {
            return (Entity)this.customSpawnCallback.apply(packet);
        }

        public void setCustomSpawning(Function<EntitySpawnPacket, Entity> callable, boolean usesVanillaSpawning) {
            this.customSpawnCallback = callable;
            this.usesVanillaSpawning = usesVanillaSpawning;
        }
    }
}
