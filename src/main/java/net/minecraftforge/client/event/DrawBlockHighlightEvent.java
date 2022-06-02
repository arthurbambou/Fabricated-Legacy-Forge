package net.minecraftforge.client.event;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class DrawBlockHighlightEvent extends Event {
    public final WorldRenderer context;
    public final PlayerEntity player;
    public final BlockHitResult target;
    public final int subID;
    public final ItemStack currentItem;
    public final float partialTicks;

    public DrawBlockHighlightEvent(WorldRenderer context, PlayerEntity player, BlockHitResult target, int subID, ItemStack currentItem, float partialTicks) {
        this.context = context;
        this.player = player;
        this.target = target;
        this.subID = subID;
        this.currentItem = currentItem;
        this.partialTicks = partialTicks;
    }
}
