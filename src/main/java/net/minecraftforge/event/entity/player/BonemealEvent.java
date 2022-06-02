package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event.HasResult;

@Cancelable
@HasResult
public class BonemealEvent extends PlayerEvent {
    public final World world;
    public final int ID;
    public final int X;
    public final int Y;
    public final int Z;

    public BonemealEvent(PlayerEntity player, World world, int id, int x, int y, int z) {
        super(player);
        this.world = world;
        this.ID = id;
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
}
