package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event.Result;

@Cancelable
public class PlayerInteractEvent extends PlayerEvent {
    public final PlayerInteractEvent.Action action;
    public final int x;
    public final int y;
    public final int z;
    public final int face;
    public Result useBlock = Result.DEFAULT;
    public Result useItem = Result.DEFAULT;

    public PlayerInteractEvent(PlayerEntity player, PlayerInteractEvent.Action action, int x, int y, int z, int face) {
        super(player);
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
        if (face == -1) {
            this.useBlock = Result.DENY;
        }

    }

    public void setCanceled(boolean cancel) {
        this.useBlock = cancel ? Result.DENY : (this.useBlock == Result.DENY ? Result.DEFAULT : this.useBlock);
        this.useItem = cancel ? Result.DENY : (this.useItem == Result.DENY ? Result.DEFAULT : this.useItem);
    }

    public static enum Action {
        RIGHT_CLICK_AIR,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_BLOCK;

        private Action() {
        }
    }
}
