package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.Block;
import net.minecraft.block.LogBlock;
import net.minecraft.block.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LogBlock.class)
public class LogBlockMixin extends Block {
    public LogBlockMixin(int id, Material material) {
        super(id, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_411(World par1World, int par2, int par3, int par4, int par5, int par6) {
        byte var7 = 4;
        int var8 = var7 + 1;
        if (par1World.isRegionLoaded(par2 - var8, par3 - var8, par4 - var8, par2 + var8, par3 + var8, par4 + var8)) {
            for(int var9 = -var7; var9 <= var7; ++var9) {
                for(int var10 = -var7; var10 <= var7; ++var10) {
                    for(int var11 = -var7; var11 <= var7; ++var11) {
                        int var12 = par1World.getBlock(par2 + var9, par3 + var10, par4 + var11);
                        if (Block.BLOCKS[var12] != null) {
                            Block.BLOCKS[var12].beginLeavesDecay(par1World, par2 + var9, par3 + var10, par4 + var11);
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean canSustainLeaves(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isWood(World world, int x, int y, int z) {
        return true;
    }
}
