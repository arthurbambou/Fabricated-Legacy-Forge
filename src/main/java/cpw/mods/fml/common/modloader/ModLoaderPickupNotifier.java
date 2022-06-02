package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.IPickupNotifier;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ModLoaderPickupNotifier implements IPickupNotifier {
    private BaseModProxy mod;

    public ModLoaderPickupNotifier(BaseModProxy mod) {
        this.mod = mod;
    }

    public void notifyPickup(ItemEntity item, PlayerEntity player) {
        this.mod.onItemPickup(player, item.field_23087);
    }
}
