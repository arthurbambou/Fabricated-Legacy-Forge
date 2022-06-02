package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractButtonBlock.class)
public abstract class AbstractButtonBlockMixin extends Block {

    public AbstractButtonBlockMixin(int id, Material material) {
        super(id, material);
    }

    @Shadow protected abstract boolean method_285(World world, int i, int j, int k);

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_428(World par1World, int par2, int par3, int par4, int par5) {
        ForgeDirection dir = ForgeDirection.getOrientation(par5);
        return dir == ForgeDirection.NORTH && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH)
                || dir == ForgeDirection.SOUTH && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH)
                || dir == ForgeDirection.WEST && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST)
                || dir == ForgeDirection.EAST && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_434(World par1World, int par2, int par3, int par4) {
        return par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST)
                || par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST)
                || par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH)
                || par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_4185(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8) {
        int var9 = par1World.getBlockData(par2, par3, par4);
        int var10 = var9 & 8;
        var9 &= 7;
        ForgeDirection dir = ForgeDirection.getOrientation(par5);
        if (dir == ForgeDirection.NORTH && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH)) {
            var9 = 4;
        } else if (dir == ForgeDirection.SOUTH && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH)) {
            var9 = 3;
        } else if (dir == ForgeDirection.WEST && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST)) {
            var9 = 2;
        } else if (dir == ForgeDirection.EAST && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST)) {
            var9 = 1;
        } else {
            var9 = this.method_284(par1World, par2, par3, par4);
        }

        par1World.method_3672(par2, par3, par4, var9 + var10);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private int method_284(World par1World, int par2, int par3, int par4) {
        if (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST)) {
            return 1;
        } else if (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST)) {
            return 2;
        } else if (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH)) {
            return 3;
        } else {
            return par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH) ? 4 : 1;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_408(World par1World, int par2, int par3, int par4, int par5) {
        if (this.method_285(par1World, par2, par3, par4)) {
            int var6 = par1World.getBlockData(par2, par3, par4) & 7;
            boolean var7 = false;
            if (!par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST) && var6 == 1) {
                var7 = true;
            }

            if (!par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST) && var6 == 2) {
                var7 = true;
            }

            if (!par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH) && var6 == 3) {
                var7 = true;
            }

            if (!par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH) && var6 == 4) {
                var7 = true;
            }

            if (var7) {
                this.method_445(par1World, par2, par3, par4, par1World.getBlockData(par2, par3, par4), 0);
                par1World.method_3690(par2, par3, par4, 0);
            }
        }

    }
}
