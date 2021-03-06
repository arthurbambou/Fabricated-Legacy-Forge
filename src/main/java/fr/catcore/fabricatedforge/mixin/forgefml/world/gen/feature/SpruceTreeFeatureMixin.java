package fr.catcore.fabricatedforge.mixin.forgefml.world.gen.feature;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SpruceTreeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(SpruceTreeFeature.class)
public abstract class SpruceTreeFeatureMixin extends Feature {

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_4028(World par1World, Random par2Random, int par3, int par4, int par5) {
        int var6 = par2Random.nextInt(4) + 6;
        int var7 = 1 + par2Random.nextInt(2);
        int var8 = var6 - var7;
        int var9 = 2 + par2Random.nextInt(2);
        boolean var10 = true;
        if (par4 >= 1 && par4 + var6 + 1 <= 256) {
            int var11;
            int var13;
            int var15;
            int var21;
            int var17;
            for(var11 = par4; var11 <= par4 + 1 + var6 && var10; ++var11) {
                boolean var12 = true;
                if (var11 - par4 < var7) {
                    var21 = 0;
                } else {
                    var21 = var9;
                }

                for(var13 = par3 - var21; var13 <= par3 + var21 && var10; ++var13) {
                    for(var17 = par5 - var21; var17 <= par5 + var21 && var10; ++var17) {
                        if (var11 >= 0 && var11 < 256) {
                            var15 = par1World.getBlock(var13, var11, var17);
                            Block block = Block.BLOCKS[var15];
                            if (var15 != 0 && block != null && !((IBlock)block).isLeaves(par1World, var13, var11, var17)) {
                                var10 = false;
                            }
                        } else {
                            var10 = false;
                        }
                    }
                }
            }

            if (!var10) {
                return false;
            } else {
                var11 = par1World.getBlock(par3, par4 - 1, par5);
                if ((var11 == Block.GRASS_BLOCK.id || var11 == Block.DIRT.id) && par4 < 256 - var6 - 1) {
                    this.method_4026(par1World, par3, par4 - 1, par5, Block.DIRT.id);
                    var21 = par2Random.nextInt(2);
                    var13 = 1;
                    byte var22 = 0;

                    int var16;
                    for(var15 = 0; var15 <= var8; ++var15) {
                        var16 = par4 + var6 - var15;

                        for(var17 = par3 - var21; var17 <= par3 + var21; ++var17) {
                            int var18 = var17 - par3;

                            for(int var19 = par5 - var21; var19 <= par5 + var21; ++var19) {
                                int var20 = var19 - par5;
                                Block block = Block.BLOCKS[par1World.getBlock(var17, var16, var19)];
                                if ((Math.abs(var18) != var21 || Math.abs(var20) != var21 || var21 <= 0) && (block == null || ((IBlock)block).canBeReplacedByLeaves(par1World, var17, var16, var19))) {
                                    this.method_4027(par1World, var17, var16, var19, Block.LEAVES.id, 1);
                                }
                            }
                        }

                        if (var21 >= var13) {
                            var21 = var22;
                            var22 = 1;
                            ++var13;
                            if (var13 > var9) {
                                var13 = var9;
                            }
                        } else {
                            ++var21;
                        }
                    }

                    var15 = par2Random.nextInt(3);

                    for(var16 = 0; var16 < var6 - var15; ++var16) {
                        var17 = par1World.getBlock(par3, par4 + var16, par5);
                        Block block = Block.BLOCKS[var17];
                        if (var17 == 0 || block == null || ((IBlock)block).isLeaves(par1World, par3, par4 + var16, par5)) {
                            this.method_4027(par1World, par3, par4 + var16, par5, Block.LOG.id, 1);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
