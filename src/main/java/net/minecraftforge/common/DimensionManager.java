package net.minecraftforge.common;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MultiServerWorld;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.WorldSaveException;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class DimensionManager {
    private static Hashtable<Integer, Class<? extends Dimension>> providers = new Hashtable();
    private static Hashtable<Integer, Boolean> spawnSettings = new Hashtable();
    private static Hashtable<Integer, ServerWorld> worlds = new Hashtable();
    private static boolean hasInit = false;
    private static Hashtable<Integer, Integer> dimensions = new Hashtable();
    private static Map<World, ListMultimap<ChunkPos, String>> persistentChunkStore = Maps.newHashMap();
    private static ArrayList<Integer> unloadQueue = new ArrayList();
    private static BitSet dimensionMap = new BitSet(1024);

    public DimensionManager() {
    }

    public static boolean registerProviderType(int id, Class<? extends Dimension> provider, boolean keepLoaded) {
        if (providers.containsValue(id)) {
            return false;
        } else {
            providers.put(id, provider);
            spawnSettings.put(id, keepLoaded);
            return true;
        }
    }

    public static void init() {
        if (!hasInit) {
            registerProviderType(0, OverworldDimension.class, true);
            registerProviderType(-1, TheNetherDimension.class, true);
            registerProviderType(1, TheEndDimension.class, false);
            registerDimension(0, 0);
            registerDimension(-1, -1);
            registerDimension(1, 1);
        }
    }

    public static void registerDimension(int id, int providerType) {
        if (!providers.containsKey(providerType)) {
            throw new IllegalArgumentException(String.format("Failed to register dimension for id %d, provider type %d does not exist", id, providerType));
        } else if (dimensions.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Failed to register dimension for id %d, One is already registered", id));
        } else {
            dimensions.put(id, providerType);
            if (id >= 0) {
                dimensionMap.set(id);
            }

        }
    }

    public static void unregisterDimension(int id) {
        if (!dimensions.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Failed to unregister dimension for id %d; No provider registered", id));
        } else {
            dimensions.remove(id);
        }
    }

    public static int getProviderType(int dim) {
        if (!dimensions.containsKey(dim)) {
            throw new IllegalArgumentException(String.format("Could not get provider type for dimension %d, does not exist", dim));
        } else {
            return dimensions.get(dim);
        }
    }

    public static Dimension getProvider(int dim) {
        return getWorld(dim).dimension;
    }

    public static Integer[] getIDs() {
        return (Integer[])worlds.keySet().toArray(new Integer[worlds.size()]);
    }

    public static void setWorld(int id, ServerWorld world) {
        if (world != null) {
            worlds.put(id, world);
            MinecraftServer.getServer().worldTickTimes.put(id, new long[100]);
            FMLLog.info("Loading dimension %d (%s) (%s)", new Object[]{id, world.getLevelProperties().getLevelName(), world.getServer()});
        } else {
            worlds.remove(id);
            MinecraftServer.getServer().worldTickTimes.remove(id);
            FMLLog.info("Unloading dimension %d", new Object[]{id});
        }

        ArrayList<ServerWorld> tmp = new ArrayList();
        if (worlds.get(0) != null) {
            tmp.add(worlds.get(0));
        }

        if (worlds.get(-1) != null) {
            tmp.add(worlds.get(-1));
        }

        if (worlds.get(1) != null) {
            tmp.add(worlds.get(1));
        }

        for(Entry<Integer, ServerWorld> entry : worlds.entrySet()) {
            int dim = entry.getKey();
            if (dim < -1 || dim > 1) {
                tmp.add(entry.getValue());
            }
        }

        MinecraftServer.getServer().worlds = (ServerWorld[])tmp.toArray(new ServerWorld[0]);
    }

    public static void initDimension(int dim) {
        ServerWorld overworld = getWorld(0);
        if (overworld == null) {
            throw new RuntimeException("Cannot Hotload Dim: Overworld is not Loaded!");
        } else {
            try {
                getProviderType(dim);
            } catch (Exception var6) {
                System.err.println("Cannot Hotload Dim: " + var6.getMessage());
                return;
            }

            MinecraftServer mcServer = overworld.getServer();
            SaveHandler savehandler = overworld.getSaveHandler();
            LevelInfo worldSettings = new LevelInfo(overworld.getLevelProperties());
            ServerWorld world = (ServerWorld)(dim == 0
                    ? overworld
                    : new MultiServerWorld(mcServer, savehandler, overworld.getLevelProperties().getLevelName(), dim, worldSettings, overworld, mcServer.profiler));
            world.addListener(new ServerWorldManager(mcServer, world));
            MinecraftForge.EVENT_BUS.post(new Load(world));
            if (!mcServer.isSinglePlayer()) {
                world.getLevelProperties().method_207(mcServer.method_3026());
            }

            mcServer.method_3016(mcServer.method_3029());
        }
    }

    public static ServerWorld getWorld(int id) {
        return (ServerWorld)worlds.get(id);
    }

    public static ServerWorld[] getWorlds() {
        return (ServerWorld[])worlds.values().toArray(new ServerWorld[0]);
    }

    public static boolean shouldLoadSpawn(int dim) {
        int id = getProviderType(dim);
        return spawnSettings.contains(id) && spawnSettings.get(id);
    }

    public static Integer[] getStaticDimensionIDs() {
        return (Integer[])dimensions.keySet().toArray(new Integer[dimensions.keySet().size()]);
    }

    public static Dimension createProviderFor(int dim) {
        try {
            if (dimensions.containsKey(dim)) {
                Dimension provider = (Dimension)((Class)providers.get(getProviderType(dim))).newInstance();
                provider.setDimension(dim);
                return provider;
            } else {
                throw new RuntimeException(String.format("No WorldProvider bound for dimension %d", dim));
            }
        } catch (Exception var2) {
            FMLCommonHandler.instance()
                    .getFMLLogger()
                    .log(
                            Level.SEVERE,
                            String.format(
                                    "An error occured trying to create an instance of WorldProvider %d (%s)",
                                    dim,
                                    ((Class)providers.get(getProviderType(dim))).getSimpleName()
                            ),
                            var2
                    );
            throw new RuntimeException(var2);
        }
    }

    public static void unloadWorld(int id) {
        unloadQueue.add(id);
    }

    public static void unloadWorlds(Hashtable<Integer, long[]> worldTickTimes) {
        for(int id : unloadQueue) {
            try {
                ((ServerWorld)worlds.get(id)).method_2138(true, null);
            } catch (WorldSaveException var4) {
                var4.printStackTrace();
            }

            MinecraftForge.EVENT_BUS.post(new Unload((World)worlds.get(id)));
            ((ServerWorld)worlds.get(id)).close();
            setWorld(id, null);
        }

        unloadQueue.clear();
    }

    public static int getNextFreeDimId() {
        int next = 0;

        while(true) {
            next = dimensionMap.nextClearBit(next);
            if (!dimensions.containsKey(next)) {
                return next;
            }

            dimensionMap.set(next);
        }
    }

    public static NbtCompound saveDimensionDataMap() {
        int[] data = new int[(dimensionMap.length() + 32 - 1) / 32];
        NbtCompound dimMap = new NbtCompound();

        for(int i = 0; i < data.length; ++i) {
            int val = 0;

            for(int j = 0; j < 32; ++j) {
                val |= dimensionMap.get(i * 32 + j) ? 1 << j : 0;
            }

            data[i] = val;
        }

        dimMap.putIntArray("DimensionArray", data);
        return dimMap;
    }

    public static void loadDimensionDataMap(NbtCompound compoundTag) {
        if (compoundTag == null) {
            dimensionMap.clear();

            for(Integer id : dimensions.keySet()) {
                if (id >= 0) {
                    dimensionMap.set(id);
                }
            }
        } else {
            int[] intArray = compoundTag.getIntArray("DimensionArray");

            for(int i = 0; i < intArray.length; ++i) {
                for(int j = 0; j < 32; ++j) {
                    dimensionMap.set(i * 32 + j, (intArray[i] & 1 << j) != 0);
                }
            }
        }

    }

    static {
        init();
    }
}
