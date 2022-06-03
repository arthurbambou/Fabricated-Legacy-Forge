package cpw.mods.fml.client;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.WrongMinecraftVersionException;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.catcore.fabricatedforge.forged.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_469;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.MapUpdate_S2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.World;

public class FMLClientHandler implements IFMLSidedHandler {
    private static final FMLClientHandler INSTANCE = new FMLClientHandler();
    private Minecraft client;
    private DummyModContainer optifineContainer;
    private boolean guiLoaded;
    private boolean serverIsRunning;
    private MissingModsException modsMissing;
    private boolean loading;
    private WrongMinecraftVersionException wrongMC;
    private CustomModLoadingErrorDisplayException customError;

    public FMLClientHandler() {
    }

    public void beginMinecraftLoading(Minecraft minecraft) {
        if (minecraft.isDemo()) {
            FMLLog.severe("DEMO MODE DETECTED, FML will not work. Finishing now.", new Object[0]);
            this.haltGame("FML will not run in demo mode", new RuntimeException());
        } else {
            this.loading = true;
            this.client = minecraft;
            ObfuscationReflectionHelper.detectObfuscation(World.class);
            TextureFXManager.instance().setClient(this.client);
            FMLCommonHandler.instance().beginLoading(this);
            new ModLoaderClientHelper(this.client);

            try {
                Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
                String optifineVersion = (String)optifineConfig.getField("VERSION").get(null);
                Map<String, Object> dummyOptifineMeta = ImmutableMap.<String, Object>builder().put("name", "Optifine").put("version", optifineVersion).build();
                ModMetadata optifineMetadata = MetadataCollection.from(this.getClass().getResourceAsStream("optifinemod.info"), "optifine")
                        .getMetadataForId("optifine", dummyOptifineMeta);
                this.optifineContainer = new DummyModContainer(optifineMetadata);
                FMLLog.info("Forge Mod Loader has detected optifine %s, enabling compatibility features", new Object[]{this.optifineContainer.getVersion()});
            } catch (Exception var10) {
                this.optifineContainer = null;
            }

            try {
                Loader.instance().loadMods();
            } catch (WrongMinecraftVersionException var6) {
                this.wrongMC = var6;
            } catch (MissingModsException var7) {
                this.modsMissing = var7;
            } catch (CustomModLoadingErrorDisplayException var8) {
                FMLLog.log(Level.SEVERE, var8, "A custom exception was thrown by a mod, the game will now halt", new Object[0]);
                this.customError = var8;
            } catch (LoaderException var9) {
                this.haltGame("There was a severe problem during mod loading that has caused the game to fail", var9);
                return;
            }

        }
    }

    public void haltGame(String message, Throwable t) {
        this.client.printCrashReport(new CrashReport(message, t));
        throw Throwables.propagate(t);
    }

    public void finishMinecraftLoading() {
        if (this.modsMissing == null && this.wrongMC == null) {
            try {
                Loader.instance().initializeMods();
            } catch (CustomModLoadingErrorDisplayException var2) {
                FMLLog.log(Level.SEVERE, var2, "A custom exception was thrown by a mod, the game will now halt", new Object[0]);
                this.customError = var2;
                return;
            } catch (LoaderException var3) {
                this.haltGame("There was a severe problem during mod loading that has caused the game to fail", var3);
                return;
            }

            LanguageRegistry.reloadLanguageTable();
            RenderingRegistry.instance().loadEntityRenderers(EntityRenderDispatcher.field_2094.renderers);
            this.loading = false;
            KeyBindingRegistry.instance().uploadKeyBindingsToGame(this.client.options);
        }
    }

    public void onInitializationComplete() {
        if (this.wrongMC != null) {
            this.client.openScreen(new GuiWrongMinecraft(this.wrongMC));
        } else if (this.modsMissing != null) {
            this.client.openScreen(new GuiModsMissing(this.modsMissing));
        } else if (this.customError != null) {
            this.client.openScreen(new GuiCustomModLoadingErrorScreen(this.customError));
        } else {
            TextureFXManager.instance().loadTextures(this.client.texturePackManager.getCurrentTexturePack());
        }

    }

    public Minecraft getClient() {
        return this.client;
    }

    public Logger getMinecraftLogger() {
        return null;
    }

    public static FMLClientHandler instance() {
        return INSTANCE;
    }

    public void displayGuiScreen(PlayerEntity player, Screen gui) {
        if (this.client.playerEntity == player && gui != null) {
            this.client.openScreen(gui);
        }

    }

    public void addSpecialModEntries(ArrayList<ModContainer> mods) {
        if (this.optifineContainer != null) {
            mods.add(this.optifineContainer);
        }

    }

    public List<String> getAdditionalBrandingInformation() {
        return this.optifineContainer != null ? Arrays.asList(String.format("Optifine %s", this.optifineContainer.getVersion())) : Collections.emptyList();
    }

    public Side getSide() {
        return Side.CLIENT;
    }

    public boolean hasOptifine() {
        return this.optifineContainer != null;
    }

    public void showGuiScreen(Object clientGuiElement) {
        Screen gui = (Screen)clientGuiElement;
        this.client.openScreen(gui);
    }

    public Entity spawnEntityIntoClientWorld(EntityRegistration er, EntitySpawnPacket packet) {
        ClientWorld wc = this.client.world;
        Class<? extends Entity> cls = er.getEntityClass();

        try {
            Entity entity;
            if (er.hasCustomSpawning()) {
                entity = er.doCustomSpawning(packet);
            } else {
                entity = (Entity)cls.getConstructor(World.class).newInstance(wc);
                entity.id = packet.entityId;
                entity.refreshPositionAndAngles(packet.scaledX, packet.scaledY, packet.scaledZ, packet.scaledYaw, packet.scaledPitch);
                if (entity instanceof MobEntity) {
                    ((MobEntity)entity).field_3315 = packet.scaledHeadYaw;
                }
            }

            entity.trackedX = packet.rawX;
            entity.trackedY = packet.rawY;
            entity.trackedZ = packet.rawZ;
            if (entity instanceof IThrowableEntity) {
                Entity thrower = (Entity)(this.client.playerEntity.id == packet.throwerId ? this.client.playerEntity : wc.getEntityById(packet.throwerId));
                ((IThrowableEntity)entity).setThrower(thrower);
            }

            Entity[] parts = entity.getParts();
            if (parts != null) {
                int i = packet.entityId - entity.id;

                for(int j = 0; j < parts.length; ++j) {
                    parts[j].id += i;
                }
            }

            if (packet.metadata != null) {
                entity.getDataTracker().writeUpdatedEntries(packet.metadata);
            }

            if (packet.throwerId > 0) {
                entity.setVelocityClient(packet.speedScaledX, packet.speedScaledY, packet.speedScaledZ);
            }

            if (entity instanceof IEntityAdditionalSpawnData) {
                ((IEntityAdditionalSpawnData)entity).readSpawnData(packet.dataStream);
            }

            wc.method_1253(packet.entityId, entity);
            return entity;
        } catch (Exception var9) {
            FMLLog.log(Level.SEVERE, var9, "A severe problem occurred during the spawning of an entity", new Object[0]);
            throw Throwables.propagate(var9);
        }
    }

    public void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket packet) {
        Entity ent = this.client.world.getEntityById(packet.entityId);
        if (ent != null) {
            ent.trackedX = packet.serverX;
            ent.trackedY = packet.serverY;
            ent.trackedZ = packet.serverZ;
        } else {
            FMLLog.fine("Attempted to adjust the position of entity %d which is not present on the client", new Object[]{packet.entityId});
        }

    }

    public void beginServerLoading(MinecraftServer server) {
    }

    public void finishServerLoading() {
    }

    public MinecraftServer getServer() {
        return this.client.getServer();
    }

    public void sendPacket(Packet packet) {
        if (this.client.playerEntity != null) {
            this.client.playerEntity.field_1667.sendPacket(packet);
        }

    }

    public void displayMissingMods(ModMissingPacket modMissingPacket) {
        this.client.openScreen(new GuiModsMissingForServer(modMissingPacket));
    }

    public boolean isLoading() {
        return this.loading;
    }

    public void handleTinyPacket(PacketListener handler, MapUpdate_S2CPacket mapData) {
        ((class_469)handler).fmlPacket131Callback(mapData);
    }

    public void setClientCompatibilityLevel(byte compatibilityLevel) {
        ReflectionUtils.class_469_setConnectionCompatibilityLevel(compatibilityLevel);
    }

    public byte getClientCompatibilityLevel() {
        return ReflectionUtils.class_469_getConnectionCompatibilityLevel();
    }
}
