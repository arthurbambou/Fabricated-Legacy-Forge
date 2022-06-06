package fr.catcore.fabricatedforge.forged;

import fr.catcore.fabricatedforge.mixin.forgefml.block.class_174Accessor;
import fr.catcore.fabricatedforge.mixininterface.IRailBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.class_174;
import net.minecraftforge.common.IMinecartCollisionHandler;

public class ReflectionUtils {
//    public static final Biome[] LevelGeneratorType_base11Biomes = new Biome[]{Biome.DESERT, Biome.FOREST, Biome.EXTREME_HILLS, Biome.SWAMPLAND, Biome.PLAINS, Biome.TAIGA};
//    public static final Biome[] LevelGeneratorType_base12Biomes = ObjectArrays.concat(LevelGeneratorType_base11Biomes, Biome.JUNGLE);

    public static int[] Block_blockFireSpreadSpeed = new int[4096];

    public static int[] Block_blockFlammability = new int[4096];

    public static void Block_setBurnProperties(int id, int encouragement, int flammability) {
        Block_blockFireSpreadSpeed[id] = encouragement;
        Block_blockFlammability[id] = flammability;
    }

    public static int class_174_method_360(class_174 par0RailLogic) {
        return ((class_174Accessor)par0RailLogic).method_363_invoker();
    }

    public static boolean RailBlock_method_350(RailBlock par0BlockRail) {
        return ((IRailBlock)par0BlockRail).getField_304();
    }

    public static boolean TrapdoorBlock_disableValidation = false;

    public static double World_MAX_ENTITY_RADIUS = 2.0;

    public static float AbstractMinecartEntity_defaultMaxSpeedRail = 0.4F;
    public static float AbstractMinecartEntity_defaultMaxSpeedGround = 0.4F;
    public static float AbstractMinecartEntity_defaultMaxSpeedAirLateral = 0.4F;
    public static float AbstractMinecartEntity_defaultMaxSpeedAirVertical = -1.0F;
    public static double AbstractMinecartEntity_defaultDragRidden = 0.997F;
    public static double AbstractMinecartEntity_defaultDragEmpty = 0.96F;
    public static double AbstractMinecartEntity_defaultDragAir = 0.95F;
    private static IMinecartCollisionHandler AbstractMinecartEntity_collisionHandler = null;

    public static IMinecartCollisionHandler AbstractMinecartEntity_getCollisionHandler() {
        return AbstractMinecartEntity_collisionHandler;
    }

    public static void AbstractMinecartEntity_setCollisionHandler(IMinecartCollisionHandler handler) {
        AbstractMinecartEntity_collisionHandler = handler;
    }
}
