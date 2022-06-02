package net.minecraftforge.event;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class ForgeEventFactory {
    public ForgeEventFactory() {
    }

    public static boolean doPlayerHarvestCheck(PlayerEntity player, Block block, boolean success) {
        HarvestCheck event = new HarvestCheck(player, block, success);
        MinecraftForge.EVENT_BUS.post(event);
        return event.success;
    }

    public static float getBreakSpeed(PlayerEntity player, Block block, int metadata, float original) {
        BreakSpeed event = new BreakSpeed(player, block, metadata, original);
        return MinecraftForge.EVENT_BUS.post(event) ? -1.0F : event.newSpeed;
    }

    public static PlayerInteractEvent onPlayerInteract(PlayerEntity player, Action action, int x, int y, int z, int face) {
        PlayerInteractEvent event = new PlayerInteractEvent(player, action, x, y, z, face);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onPlayerDestroyItem(PlayerEntity player, ItemStack stack) {
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack));
    }
}
