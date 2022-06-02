package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin extends Block {
    public DoorBlockMixin(int id, Material material) {
        super(id, material);
    }

    @Inject(method = "method_421", cancellable = true, at = @At("RETURN"))
    private void forge_method_421(World i, int j, int k, int playerEntity, PlayerEntity l, int f, float g, float h, float par9, CallbackInfoReturnable<Boolean> cir) {
        if (this.material == Material.IRON) {
            cir.setReturnValue(false);
        }
    }
}
