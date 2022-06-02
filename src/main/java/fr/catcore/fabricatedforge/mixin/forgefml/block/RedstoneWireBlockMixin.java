package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.Axis;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static boolean method_373(BlockView par0IBlockAccess, int par1, int par2, int par3, int par4) {
        int var5 = par0IBlockAccess.getBlock(par1, par2, par3);
        if (var5 == Block.field_408.id) {
            return true;
        } else if (var5 == 0) {
            return false;
        } else if (var5 != Block.field_447.id && var5 != Block.field_448.id) {
            return Block.BLOCKS[var5] != null && Block.BLOCKS[var5].canConnectRedstone(par0IBlockAccess, par1, par2, par3, par4);
        } else {
            int var6 = par0IBlockAccess.getBlockData(par1, par2, par3);
            return par4 == (var6 & 3) || par4 == Axis.OPPOSITE[var6 & 3];
        }
    }
}
