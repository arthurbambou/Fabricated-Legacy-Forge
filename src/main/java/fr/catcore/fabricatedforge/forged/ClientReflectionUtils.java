package fr.catcore.fabricatedforge.forged;

import cpw.mods.fml.common.FMLLog;

import java.util.logging.Logger;

public class ClientReflectionUtils {
    private static byte class_469_connectionCompatibilityLevel;

    public static void class_469_setConnectionCompatibilityLevel(byte connectionCompatibilityLevel) {
        ClientReflectionUtils.class_469_connectionCompatibilityLevel = connectionCompatibilityLevel;
    }

    public static byte class_469_getConnectionCompatibilityLevel() {
        return class_469_connectionCompatibilityLevel;
    }

    public static Logger class_534_log = FMLLog.getLogger();

    public static boolean Tessellator_renderingWorldRenderer = false;

    public static int SoundSystem_MUSIC_INTERVAL = 12000;
}
