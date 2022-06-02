package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.Block;
import net.minecraft.block.GrassBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(GrassBlock.class)
public class GrassBlockMixin {

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_436(World par1World, int par2, int par3, int par4, Random par5Random) {
        if (!par1World.isClient) {
            if (par1World.method_3720(par2, par3 + 1, par4) < 4 && par1World.method_3651(par2, par3 + 1, par4) > 2) {
                par1World.method_3690(par2, par3, par4, Block.DIRT.id);
            } else if (par1World.method_3720(par2, par3 + 1, par4) >= 9) {
                for(int var6 = 0; var6 < 4; ++var6) {
                    int var7 = par2 + par5Random.nextInt(3) - 1;
                    int var8 = par3 + par5Random.nextInt(5) - 3;
                    int var9 = par4 + par5Random.nextInt(3) - 1;
                    int var10 = par1World.getBlock(var7, var8 + 1, var9);
                    if (par1World.getBlock(var7, var8, var9) == Block.DIRT.id
                            && par1World.method_3720(var7, var8 + 1, var9) >= 4
                            && par1World.method_3651(var7, var8 + 1, var9) <= 2) {
                        par1World.method_3690(var7, var8, var9, Block.GRASS_BLOCK.id);
                    }
                }
            }
        }

    }
}
