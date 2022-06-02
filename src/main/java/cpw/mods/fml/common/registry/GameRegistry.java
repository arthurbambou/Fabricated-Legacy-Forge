package cpw.mods.fml.common.registry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.IDispenserHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Mod.Block;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipeRegistry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.level.LevelGeneratorType;

public class GameRegistry {
    private static Multimap<ModContainer, BlockProxy> blockRegistry = ArrayListMultimap.create();
    private static Multimap<ModContainer, ItemProxy> itemRegistry = ArrayListMultimap.create();
    private static Set<IWorldGenerator> worldGenerators = Sets.newHashSet();
    private static List<IFuelHandler> fuelHandlers = Lists.newArrayList();
    private static List<ICraftingHandler> craftingHandlers = Lists.newArrayList();
    private static List<IDispenserHandler> dispenserHandlers = Lists.newArrayList();
    private static List<IPickupNotifier> pickupHandlers = Lists.newArrayList();
    private static List<IPlayerTracker> playerTrackers = Lists.newArrayList();

    public GameRegistry() {
    }

    public static void registerWorldGenerator(IWorldGenerator generator) {
        worldGenerators.add(generator);
    }

    public static void generateWorld(int chunkX, int chunkZ, World world, ChunkProvider chunkGenerator, ChunkProvider chunkProvider) {
        long worldSeed = world.getSeed();
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 3;
        long zSeed = fmlRandom.nextLong() >> 3;
        fmlRandom.setSeed(xSeed * (long)chunkX + zSeed * (long)chunkZ ^ worldSeed);

        for(IWorldGenerator generator : worldGenerators) {
            generator.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }

    }

    public static void registerDispenserHandler(IDispenserHandler handler) {
        dispenserHandlers.add(handler);
    }

    @Deprecated
    public static void registerDispenserHandler(IDispenseHandler handler) {
        registerDispenserHandler(
                new IDispenserHandler() {
                    public int dispense(
                            int x, int y, int z, int xVelocity, int zVelocity, World world, ItemStack item, Random random, double entX, double entY, double entZ
                    ) {
                        return handler.dispense((double)x, (double)y, (double)z, xVelocity, zVelocity, world, item, random, entX, entY, entZ);
                    }
                }
        );
    }

    public static int tryDispense(
            World world, int x, int y, int z, int xVelocity, int zVelocity, ItemStack item, Random random, double entX, double entY, double entZ
    ) {
        for(IDispenserHandler handler : dispenserHandlers) {
            int dispensed = handler.dispense(x, y, z, xVelocity, zVelocity, world, item, random, entX, entY, entZ);
            if (dispensed > -1) {
                return dispensed;
            }
        }

        return -1;
    }

    public static Object buildBlock(ModContainer container, Class<?> type, Block annotation) throws Exception {
        Object o = type.getConstructor(Integer.TYPE).newInstance(findSpareBlockId());
        registerBlock((net.minecraft.block.Block)o);
        return o;
    }

    private static int findSpareBlockId() {
        return BlockTracker.nextBlockId();
    }

    public static void registerBlock(net.minecraft.block.Block block) {
        registerBlock(block, BlockItem.class);
    }

    public static void registerBlock(net.minecraft.block.Block block, Class<? extends BlockItem> itemclass) {
        if (Loader.instance().isInState(LoaderState.CONSTRUCTING)) {
            FMLLog.warning(
                    "The mod %s is attempting to register a block whilst it it being constructed. This is bad modding practice - please use a proper mod lifecycle event.",
                    new Object[]{Loader.instance().activeModContainer()}
            );
        }

        try {
            assert block != null : "registerBlock: block cannot be null";

            assert itemclass != null : "registerBlock: itemclass cannot be null";

            int blockItemId = block.id - 256;
            itemclass.getConstructor(Integer.TYPE).newInstance(blockItemId);
        } catch (Exception var3) {
            FMLLog.log(Level.SEVERE, var3, "Caught an exception during block registration", new Object[0]);
            throw new LoaderException(var3);
        }

        blockRegistry.put(Loader.instance().activeModContainer(), (BlockProxy)block);
    }

    public static void addRecipe(ItemStack output, Object... params) {
        RecipeDispatcher.getInstance().method_3495(output, params);
    }

    public static void addShapelessRecipe(ItemStack output, Object... params) {
        RecipeDispatcher.getInstance().registerShapelessRecipe(output, params);
    }

    public static void addRecipe(RecipeType recipe) {
        RecipeDispatcher.getInstance().getAllRecipes().add(recipe);
    }

    public static void addSmelting(int input, ItemStack output, float xp) {
        SmeltingRecipeRegistry.getInstance().method_3488(input, output, xp);
    }

    public static void registerTileEntity(Class<? extends BlockEntity> tileEntityClass, String id) {
        BlockEntity.registerBlockEntity(tileEntityClass, id);
    }

    public static void addBiome(Biome biome) {
        LevelGeneratorType.DEFAULT.addNewBiome(biome);
    }

    public static void removeBiome(Biome biome) {
        LevelGeneratorType.DEFAULT.removeBiome(biome);
    }

    public static void registerFuelHandler(IFuelHandler handler) {
        fuelHandlers.add(handler);
    }

    public static int getFuelValue(ItemStack itemStack) {
        int fuelValue = 0;

        for(IFuelHandler handler : fuelHandlers) {
            fuelValue = Math.max(fuelValue, handler.getBurnTime(itemStack));
        }

        return fuelValue;
    }

    public static void registerCraftingHandler(ICraftingHandler handler) {
        craftingHandlers.add(handler);
    }

    public static void onItemCrafted(PlayerEntity player, ItemStack item, Inventory craftMatrix) {
        for(ICraftingHandler handler : craftingHandlers) {
            handler.onCrafting(player, item, craftMatrix);
        }

    }

    public static void onItemSmelted(PlayerEntity player, ItemStack item) {
        for(ICraftingHandler handler : craftingHandlers) {
            handler.onSmelting(player, item);
        }

    }

    public static void registerPickupHandler(IPickupNotifier handler) {
        pickupHandlers.add(handler);
    }

    public static void onPickupNotification(PlayerEntity player, ItemEntity item) {
        for(IPickupNotifier notify : pickupHandlers) {
            notify.notifyPickup(item, player);
        }

    }

    public static void registerPlayerTracker(IPlayerTracker tracker) {
        playerTrackers.add(tracker);
    }

    public static void onPlayerLogin(PlayerEntity player) {
        for(IPlayerTracker tracker : playerTrackers) {
            tracker.onPlayerLogin(player);
        }

    }

    public static void onPlayerLogout(PlayerEntity player) {
        for(IPlayerTracker tracker : playerTrackers) {
            tracker.onPlayerLogout(player);
        }

    }

    public static void onPlayerChangedDimension(PlayerEntity player) {
        for(IPlayerTracker tracker : playerTrackers) {
            tracker.onPlayerChangedDimension(player);
        }

    }

    public static void onPlayerRespawn(PlayerEntity player) {
        for(IPlayerTracker tracker : playerTrackers) {
            tracker.onPlayerRespawn(player);
        }

    }
}
