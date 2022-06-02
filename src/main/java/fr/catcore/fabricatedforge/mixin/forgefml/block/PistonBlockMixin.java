package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.PistonBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin extends Block {

    @Shadow
    public static boolean method_558(int i) {
        return false;
    }

    public PistonBlockMixin(int id, Material material) {
        super(id, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private static boolean method_554(int par0, World par1World, int par2, int par3, int par4, boolean par5) {
        if (par0 == Block.OBSIDIAN.id) {
            return false;
        } else {
            if (par0 != Block.field_359.id && par0 != Block.field_355.id) {
                if (Block.BLOCKS[par0].method_471(par1World, par2, par3, par4) == -1.0F) {
                    return false;
                }

                if (Block.BLOCKS[par0].getPistonInteractionType() == 2) {
                    return false;
                }

                if (!par5 && Block.BLOCKS[par0].getPistonInteractionType() == 1) {
                    return false;
                }
            } else if (method_558(par1World.getBlockData(par2, par3, par4))) {
                return false;
            }

            return !par1World.hasBlockEntity(par2, par3, par4);
        }
    }
}
