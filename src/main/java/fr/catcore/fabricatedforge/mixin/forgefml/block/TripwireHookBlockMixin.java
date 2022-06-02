package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.util.math.Axis;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TripwireHookBlock.class)
public abstract class TripwireHookBlockMixin extends Block {

    @Shadow protected abstract boolean method_499(World world, int i, int j, int k);

    @Shadow protected abstract void method_498(World world, int i, int j, int k, int l);

    @Shadow protected abstract void method_497(World world, int i, int j, int k, boolean bl, boolean bl2, boolean bl3, boolean bl4);

    public TripwireHookBlockMixin(int id, Material material) {
        super(id, material);
    }

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
        return par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.SOUTH)
                || par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.NORTH)
                || par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.EAST)
                || par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.WEST);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_4185(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8) {
        byte var9 = 0;
        if (par5 == 2 && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.WEST, true)) {
            var9 = 2;
        }

        if (par5 == 3 && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.EAST, true)) {
            var9 = 0;
        }

        if (par5 == 4 && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.NORTH, true)) {
            var9 = 1;
        }

        if (par5 == 5 && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.SOUTH, true)) {
            var9 = 3;
        }

        this.method_496(par1World, par2, par3, par4, this.id, var9, false, -1, 0);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_408(World par1World, int par2, int par3, int par4, int par5) {
        if (par5 != this.id && this.method_499(par1World, par2, par3, par4)) {
            int var6 = par1World.getBlockData(par2, par3, par4);
            int var7 = var6 & 3;
            boolean var8 = false;
            if (!par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.SOUTH) && var7 == 3) {
                var8 = true;
            }

            if (!par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.NORTH) && var7 == 1) {
                var8 = true;
            }

            if (!par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.EAST) && var7 == 0) {
                var8 = true;
            }

            if (!par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.WEST) && var7 == 2) {
                var8 = true;
            }

            if (var8) {
                this.method_445(par1World, par2, par3, par4, var6, 0);
                par1World.method_3690(par2, par3, par4, 0);
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_496(World par1World, int par2, int par3, int par4, int par5, int par6, boolean par7, int par8, int par9) {
        int var10 = par6 & 3;
        boolean var11 = (par6 & 4) == 4;
        boolean var12 = (par6 & 8) == 8;
        boolean var13 = par5 == Block.TRIPWIRE_HOOK.id;
        boolean var14 = false;
        boolean var15 = !par1World.isBlockSolidOnSide(par2, par3 - 1, par4, ForgeDirection.UP);
        int var16 = Axis.OFFSET_X[var10];
        int var17 = Axis.OFFSET_Z[var10];
        int var18 = 0;
        int[] var19 = new int[42];

        for(int var20 = 1; var20 < 42; ++var20) {
            int var21 = par2 + var16 * var20;
            int var22 = par4 + var17 * var20;
            int var23 = par1World.getBlock(var21, par3, var22);
            if (var23 == Block.TRIPWIRE_HOOK.id) {
                int var24 = par1World.getBlockData(var21, par3, var22);
                if ((var24 & 3) == Axis.OPPOSITE[var10]) {
                    var18 = var20;
                }
                break;
            }

            if (var23 != Block.TRIPWIRE.id && var20 != par8) {
                var19[var20] = -1;
                var13 = false;
            } else {
                int var24 = var20 == par8 ? par9 : par1World.getBlockData(var21, par3, var22);
                boolean var25 = (var24 & 8) != 8;
                boolean var26 = (var24 & 1) == 1;
                boolean var27 = (var24 & 2) == 2;
                var13 &= var27 == var15;
                var14 |= var25 && var26;
                var19[var20] = var24;
                if (var20 == par8) {
                    par1World.method_3599(par2, par3, par4, par5, this.method_473());
                    var13 &= var25;
                }
            }
        }

        var13 &= var18 > 1;
        var14 &= var13;
        int var33 = (var13 ? 4 : 0) | (var14 ? 8 : 0);
        par6 = var10 | var33;
        if (var18 > 0) {
            int var21 = par2 + var16 * var18;
            int var22 = par4 + var17 * var18;
            int var23 = Axis.OPPOSITE[var10];
            par1World.method_3672(var21, par3, var22, var23 | var33);
            this.method_498(par1World, var21, par3, var22, var23);
            this.method_497(par1World, var21, par3, var22, var13, var14, var11, var12);
        }

        this.method_497(par1World, par2, par3, par4, var13, var14, var11, var12);
        if (par5 > 0) {
            par1World.method_3672(par2, par3, par4, par6);
            if (par7) {
                this.method_498(par1World, par2, par3, par4, var10);
            }
        }

        if (var11 != var13) {
            for(int var21 = 1; var21 < var18; ++var21) {
                int var22 = par2 + var16 * var21;
                int var23 = par4 + var17 * var21;
                int var24 = var19[var21];
                if (var24 >= 0) {
                    if (var13) {
                        var24 |= 4;
                    } else {
                        var24 &= -5;
                    }

                    par1World.method_3672(var22, par3, var23, var24);
                }
            }
        }

    }
}
