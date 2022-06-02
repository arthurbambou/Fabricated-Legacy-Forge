package cpw.mods.fml.common.modloader;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IDispenserHandler;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import java.util.EnumSet;
import java.util.Map;

import net.minecraft.TradeEntry;
import net.minecraft.command.Command;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategoryProvider;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;

public class ModLoaderHelper {
    public static IModLoaderSidedHelper sidedHelper;
    private static Map<Integer, ModLoaderGuiHelper> guiHelpers = Maps.newHashMap();
    private static ModLoaderVillageTradeHandler[] tradeHelpers = new ModLoaderVillageTradeHandler[6];

    public ModLoaderHelper() {
    }

    public static void updateStandardTicks(BaseModProxy mod, boolean enable, boolean useClock) {
        ModLoaderModContainer mlmc = (ModLoaderModContainer)Loader.instance().activeModContainer();
        BaseModTicker ticker = mlmc.getGameTickHandler();
        EnumSet<TickType> ticks = ticker.ticks();
        if (enable && !useClock) {
            ticks.add(TickType.RENDER);
        } else {
            ticks.remove(TickType.RENDER);
        }

        if (!enable || !useClock && !FMLCommonHandler.instance().getSide().isServer()) {
            ticks.remove(TickType.CLIENT);
            ticks.remove(TickType.WORLDLOAD);
        } else {
            ticks.add(TickType.CLIENT);
            ticks.add(TickType.WORLDLOAD);
        }

    }

    public static void updateGUITicks(BaseModProxy mod, boolean enable, boolean useClock) {
        ModLoaderModContainer mlmc = (ModLoaderModContainer)Loader.instance().activeModContainer();
        EnumSet<TickType> ticks = mlmc.getGUITickHandler().ticks();
        if (enable && !useClock) {
            ticks.add(TickType.RENDER);
        } else {
            ticks.remove(TickType.RENDER);
        }

        if (enable && useClock) {
            ticks.add(TickType.CLIENT);
            ticks.add(TickType.WORLDLOAD);
        } else {
            ticks.remove(TickType.CLIENT);
            ticks.remove(TickType.WORLDLOAD);
        }

    }

    public static IPacketHandler buildPacketHandlerFor(BaseModProxy mod) {
        return new ModLoaderPacketHandler(mod);
    }

    public static IWorldGenerator buildWorldGenHelper(BaseModProxy mod) {
        return new ModLoaderWorldGenerator(mod);
    }

    public static IFuelHandler buildFuelHelper(BaseModProxy mod) {
        return new ModLoaderFuelHelper(mod);
    }

    public static ICraftingHandler buildCraftingHelper(BaseModProxy mod) {
        return new ModLoaderCraftingHelper(mod);
    }

    public static void finishModLoading(ModLoaderModContainer mc) {
        if (sidedHelper != null) {
            sidedHelper.finishModLoading(mc);
        }

    }

    public static IConnectionHandler buildConnectionHelper(BaseModProxy mod) {
        return new ModLoaderConnectionHandler(mod);
    }

    public static IPickupNotifier buildPickupHelper(BaseModProxy mod) {
        return new ModLoaderPickupNotifier(mod);
    }

    public static void buildGuiHelper(BaseModProxy mod, int id) {
        ModLoaderGuiHelper handler = new ModLoaderGuiHelper(mod, id);
        guiHelpers.put(id, handler);
        NetworkRegistry.instance().registerGuiHandler(mod, handler);
    }

    public static void openGui(int id, PlayerEntity player, ScreenHandler container, int x, int y, int z) {
        ModLoaderGuiHelper helper = (ModLoaderGuiHelper)guiHelpers.get(id);
        helper.injectContainer(container);
        player.openGui(helper.getMod(), id, player.world, x, y, z);
    }

    public static Object getClientSideGui(BaseModProxy mod, PlayerEntity player, int ID, int x, int y, int z) {
        return sidedHelper != null ? sidedHelper.getClientGui(mod, player, ID, x, y, z) : null;
    }

    public static IDispenserHandler buildDispenseHelper(BaseModProxy mod) {
        return new ModLoaderDispenseHelper(mod);
    }

    public static void buildEntityTracker(
            BaseModProxy mod, Class<? extends Entity> entityClass, int entityTypeId, int updateRange, int updateInterval, boolean sendVelocityInfo
    ) {
        EntityRegistration er = EntityRegistry.registerModLoaderEntity(mod, entityClass, entityTypeId, updateRange, updateInterval, sendVelocityInfo);
        er.setCustomSpawning(
                new ModLoaderEntitySpawnCallback(mod, er),
                EnderDragonEntity.class.isAssignableFrom(entityClass) || EntityCategoryProvider.class.isAssignableFrom(entityClass)
        );
    }

    public static void registerTrade(int profession, TradeEntry entry) {
        assert profession < tradeHelpers.length : "The profession is out of bounds";

        if (tradeHelpers[profession] == null) {
            tradeHelpers[profession] = new ModLoaderVillageTradeHandler();
            VillagerRegistry.instance().registerVillageTradeHandler(profession, tradeHelpers[profession]);
        }

        tradeHelpers[profession].addTrade(entry);
    }

    public static void addCommand(Command command) {
        ModLoaderModContainer mlmc = (ModLoaderModContainer)Loader.instance().activeModContainer();
        if (mlmc != null) {
            mlmc.addServerCommand(command);
        }

    }

    public static IChatListener buildChatListener(BaseModProxy mod) {
        return new ModLoaderChatListener(mod);
    }
}
