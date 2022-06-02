package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.TorchBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin extends Block {
    @Shadow protected abstract boolean method_490(World world, int i, int j, int k);

    public TorchBlockMixin(int id, Material material) {
        super(id, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private boolean method_489(World par1World, int par2, int par3, int par4) {
        if (par1World.method_3784(par2, par3, par4)) {
            return true;
        } else {
            int var5 = par1World.getBlock(par2, par3, par4);
            return Block.BLOCKS[var5] != null && Block.BLOCKS[var5].canPlaceTorchOnTop(par1World, par2, par3, par4);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_434(World par1World, int par2, int par3, int par4) {
        return par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST, true)
                || par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST, true)
                || par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH, true)
                || par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH, true)
                || this.method_489(par1World, par2, par3 - 1, par4);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_4185(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8) {
        int var9 = par1World.getBlockData(par2, par3, par4);
        if (par5 == 1 && this.method_489(par1World, par2, par3 - 1, par4)) {
            var9 = 5;
        }

        if (par5 == 2 && par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH, true)) {
            var9 = 4;
        }

        if (par5 == 3 && par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH, true)) {
            var9 = 3;
        }

        if (par5 == 4 && par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST, true)) {
            var9 = 2;
        }

        if (par5 == 5 && par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST, true)) {
            var9 = 1;
        }

        par1World.method_3672(par2, par3, par4, var9);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_460(World par1World, int par2, int par3, int par4) {
        if (par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST, true)) {
            par1World.method_3672(par2, par3, par4, 1);
        } else if (par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST, true)) {
            par1World.method_3672(par2, par3, par4, 2);
        } else if (par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH, true)) {
            par1World.method_3672(par2, par3, par4, 3);
        } else if (par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH, true)) {
            par1World.method_3672(par2, par3, par4, 4);
        } else if (this.method_489(par1World, par2, par3 - 1, par4)) {
            par1World.method_3672(par2, par3, par4, 5);
        }

        this.method_490(par1World, par2, par3, par4);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_408(World par1World, int par2, int par3, int par4, int par5) {
        if (this.method_490(par1World, par2, par3, par4)) {
            int var6 = par1World.getBlockData(par2, par3, par4);
            boolean var7 = false;
            if (!par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.EAST, true) && var6 == 1) {
                var7 = true;
            }

            if (!par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.WEST, true) && var6 == 2) {
                var7 = true;
            }

            if (!par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.SOUTH, true) && var6 == 3) {
                var7 = true;
            }

            if (!par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.NORTH, true) && var6 == 4) {
                var7 = true;
            }

            if (!this.method_489(par1World, par2, par3 - 1, par4) && var6 == 5) {
                var7 = true;
            }

            if (var7) {
                this.method_445(par1World, par2, par3, par4, par1World.getBlockData(par2, par3, par4), 0);
                par1World.method_3690(par2, par3, par4, 0);
            }
        }

    }
}
