package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event.HasResult;

@Cancelable
@HasResult
public class FillBucketEvent extends PlayerEvent {
    public final ItemStack current;
    public final World world;
    public final BlockHitResult target;
    public ItemStack result;

    public FillBucketEvent(PlayerEntity player, ItemStack current, World world, BlockHitResult target) {
        super(player);
        this.current = current;
        this.world = world;
        this.target = target;
    }
}
