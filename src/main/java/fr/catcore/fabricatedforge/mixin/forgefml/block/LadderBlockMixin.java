package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(LadderBlock.class)
public abstract class LadderBlockMixin extends Block {

    public LadderBlockMixin(int id, Material material) {
        super(id, material);
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
        if ((var9 == 0 || par5 == 2) && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH)) {
            var9 = 2;
        }

        if ((var9 == 0 || par5 == 3) && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH)) {
            var9 = 3;
        }

        if ((var9 == 0 || par5 == 4) && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST)) {
            var9 = 4;
        }

        if ((var9 == 0 || par5 == 5) && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST)) {
            var9 = 5;
        }

        par1World.method_3672(par2, par3, par4, var9);
    }

    /**
     * @author Minecraft
     * @reason none
     */
    @Overwrite
    public void method_408(World par1World, int par2, int par3, int par4, int par5) {
        int var6 = par1World.getBlockData(par2, par3, par4);
        boolean var7 = false;
        if (var6 == 2 && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH)) {
            var7 = true;
        }

        if (var6 == 3 && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH)) {
            var7 = true;
        }

        if (var6 == 4 && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST)) {
            var7 = true;
        }

        if (var6 == 5 && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST)) {
            var7 = true;
        }

        if (!var7) {
            this.method_445(par1World, par2, par3, par4, var6, 0);
            par1World.method_3690(par2, par3, par4, 0);
        }

        super.method_408(par1World, par2, par3, par4, par5);
    }

    @Override
    public boolean isLadder(World world, int x, int y, int z) {
        return true;
    }
}
