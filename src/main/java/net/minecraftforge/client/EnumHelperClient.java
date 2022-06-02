package net.minecraftforge.client;

import net.minecraft.client.options.GameOption;
import net.minecraft.client.util.OperatingSystem;
import net.minecraft.util.Rarity;
import net.minecraft.world.GameMode;
import net.minecraftforge.common.EnumHelper;

public class EnumHelperClient extends EnumHelper {
    private static Class[][] clentTypes = new Class[][]{
            {GameMode.class, Integer.TYPE, String.class},
            {GameOption.class, String.class, Boolean.TYPE, Boolean.TYPE},
            {OperatingSystem.class},
            {Rarity.class, Integer.TYPE, String.class}
    };

    public EnumHelperClient() {
    }

    public static GameMode addGameType(String name, int id, String displayName) {
        return addEnum(GameMode.class, name, id, displayName);
    }

    public static GameOption addOptions(String name, String langName, boolean isSlider, boolean isToggle) {
        return addEnum(GameOption.class, name, langName, isSlider, isToggle);
    }

    public static OperatingSystem addOS2(String name) {
        return addEnum(OperatingSystem.class, name);
    }

    public static Rarity addRarity(String name, int color, String displayName) {
        return addEnum(Rarity.class, name, color, displayName);
    }

    public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName, Object... paramValues) {
        return (T)addEnum(clentTypes, enumType, enumName, paramValues);
    }
}
