package net.minecraftforge.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.OpenScreen_S2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecIBXM;

public class ModCompatibilityClient {
    public static SoundLoader audioModSoundPoolCave;
    private static int isMLMPInstalled = -1;

    public ModCompatibilityClient() {
    }

    private static Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception var4) {
            try {
                return Class.forName("net.minecraft.src." + name);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public static void audioModLoad(SoundSystem mngr) {
        audioModSoundPoolCave = new SoundLoader();
        audioModLoadModAudio("resources/mod/sound", mngr.soundsLoader);
        audioModLoadModAudio("resources/mod/streaming", mngr.bgmusicLoader);
        audioModLoadModAudio("resources/mod/music", mngr.musicLoader);
        audioModLoadModAudio("resources/mod/cavemusic", audioModSoundPoolCave);
        if (SoundSystem.MUSIC_INTERVAL == 12000) {
            SoundSystem.MUSIC_INTERVAL = 6000;
        }

    }

    private static void audioModLoadModAudio(String path, SoundLoader pool) {
        File folder = new File(Minecraft.getGameFolder(), path);

        try {
            audioModWalkFolder(folder, folder, pool);
        } catch (IOException var4) {
            FMLLog.log(Level.FINE, var4, "Loading Mod audio failed for folder: %s", new Object[]{path});
            var4.printStackTrace();
        }

    }

    private static void audioModWalkFolder(File base, File folder, SoundLoader pool) throws IOException {
        if (folder.exists() || folder.mkdirs()) {
            for(File file : folder.listFiles()) {
                if (!file.getName().startsWith(".")) {
                    if (file.isDirectory()) {
                        audioModWalkFolder(base, file, pool);
                    } else if (file.isFile()) {
                        String subpath = file.getPath().substring(base.getPath().length() + 1).replace('\\', '/');
                        pool.getSound(subpath, file);
                    }
                }
            }
        }

    }

    public static void audioModAddCodecs() {
        SoundSystemConfig.setCodec("xm", CodecIBXM.class);
        SoundSystemConfig.setCodec("s3m", CodecIBXM.class);
        SoundSystemConfig.setCodec("mod", CodecIBXM.class);
    }

    public static Sound audioModPickBackgroundMusic(SoundSystem soundManager, Sound current) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        if (mc != null && mc.world != null && audioModSoundPoolCave != null) {
            Entity ent = mc.field_3806;
            int x = MathHelper.fastFloor(ent.x);
            int y = MathHelper.fastFloor(ent.y);
            int z = MathHelper.fastFloor(ent.z);
            return mc.world.method_3716(x, y, z) ? current : audioModSoundPoolCave.getSoundRandom();
        } else {
            return current;
        }
    }

    public static boolean isMLMPInstalled() {
        if (isMLMPInstalled == -1) {
            isMLMPInstalled = getClass("ModLoaderMp") != null ? 1 : 0;
        }

        return isMLMPInstalled == 1;
    }

    public static Object mlmpVehicleSpawn(int type, World world, double x, double y, double z, Entity thrower, Object currentEntity) throws Exception {
        Class mlmp = getClass("ModLoaderMp");
        if (isMLMPInstalled() && mlmp != null) {
            Object entry = mlmp.getDeclaredMethod("handleNetClientHandlerEntities", Integer.TYPE).invoke(null, type);
            if (entry == null) {
                return currentEntity;
            } else {
                Class entityClass = (Class)entry.getClass().getDeclaredField("entityClass").get(entry);
                Object ret = (Entity)entityClass.getConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE).newInstance(world, x, y, z);
                if (entry.getClass().getDeclaredField("entityHasOwner").getBoolean(entry)) {
                    Field owner = entityClass.getField("owner");
                    if (!Entity.class.isAssignableFrom(owner.getType())) {
                        throw new Exception(String.format("Entity's owner field must be of type Entity, but it is of type %s.", owner.getType()));
                    }

                    if (thrower == null) {
                        System.out.println("Received spawn packet for entity with owner, but owner was not found.");
                        FMLLog.fine("Received spawn packet for entity with owner, but owner was not found.", new Object[0]);
                    } else {
                        if (!owner.getType().isAssignableFrom(thrower.getClass())) {
                            throw new Exception(
                                    String.format("Tried to assign an entity of type %s to entity owner, which is of type %s.", thrower.getClass(), owner.getType())
                            );
                        }

                        owner.set(ret, thrower);
                    }
                }

                return ret;
            }
        } else {
            return currentEntity;
        }
    }

    public static void mlmpOpenWindow(OpenScreen_S2CPacket pkt) {
        Class mlmp = getClass("ModLoaderMp");
        if (isMLMPInstalled() && mlmp != null) {
            try {
                mlmp.getDeclaredMethod("handleGUI", OpenScreen_S2CPacket.class).invoke(null, pkt);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

        }
    }
}
