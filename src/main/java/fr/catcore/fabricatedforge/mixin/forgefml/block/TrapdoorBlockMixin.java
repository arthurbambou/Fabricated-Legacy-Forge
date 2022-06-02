package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.forged.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TrapdoorBlock.class)
public abstract class TrapdoorBlockMixin extends Block {

    @Shadow public abstract void method_491(World world, int i, int j, int k, boolean bl);

    public TrapdoorBlockMixin(int id, Material material) {
        super(id, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_408(World par1World, int par2, int par3, int par4, int par5) {
        if (!par1World.isClient) {
            int var6 = par1World.getBlockData(par2, par3, par4);
            int var7 = par2;
            int var8 = par4;
            if ((var6 & 3) == 0) {
                var8 = par4 + 1;
            }

            if ((var6 & 3) == 1) {
                --var8;
            }

            if ((var6 & 3) == 2) {
                var7 = par2 + 1;
            }

            if ((var6 & 3) == 3) {
                --var7;
            }

            if (!method_494(par1World.getBlock(var7, par3, var8))
                    && !par1World.isBlockSolidOnSide(var7, par3, var8, ForgeDirection.getOrientation((var6 & 3) + 2))) {
                par1World.method_3690(par2, par3, par4, 0);
                this.method_445(par1World, par2, par3, par4, var6, 0);
            }

            boolean var9 = par1World.method_3739(par2, par3, par4);
            if (var9 || par5 > 0 && Block.BLOCKS[par5].emitsRedstonePower() || par5 == 0) {
                this.method_491(par1World, par2, par3, par4, var9);
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_428(World par1World, int par2, int par3, int par4, int par5) {
        if (ReflectionUtils.TrapdoorBlock_disableValidation) {
            return true;
        } else if (par5 == 0) {
            return false;
        } else if (par5 == 1) {
            return false;
        } else {
            if (par5 == 2) {
                ++par4;
            }

            if (par5 == 3) {
                --par4;
            }

            if (par5 == 4) {
                ++par2;
            }

            if (par5 == 5) {
                --par2;
            }

            return method_494(par1World.getBlock(par2, par3, par4)) || par1World.isBlockSolidOnSide(par2, par3, par4, ForgeDirection.UP);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private static boolean method_494(int par0) {
        if (ReflectionUtils.TrapdoorBlock_disableValidation) {
            return true;
        } else if (par0 <= 0) {
            return false;
        } else {
            Block var1 = Block.BLOCKS[par0];
            return var1 != null && var1.material.isOpaque() && var1.renderAsNormalBlock() || var1 == Block.GLOWSTONE;
        }
    }
}
