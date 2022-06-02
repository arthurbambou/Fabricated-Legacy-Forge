package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import net.minecraft.block.BaseLeavesBlock;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Random;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends BaseLeavesBlock implements IShearable {

    @Shadow
    int[] field_287;

    @Shadow protected abstract void method_326(World world, int i, int j, int k);

    protected LeavesBlockMixin(int i, int j, Material material, boolean bl) {
        super(i, j, material, bl);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_411(World par1World, int par2, int par3, int par4, int par5, int par6) {
        byte var7 = 1;
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

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_436(World par1World, int par2, int par3, int par4, Random par5Random) {
        if (!par1World.isClient) {
            int var6 = par1World.getBlockData(par2, par3, par4);
            if ((var6 & 8) != 0 && (var6 & 4) == 0) {
                byte var7 = 4;
                int var8 = var7 + 1;
                byte var9 = 32;
                int var10 = var9 * var9;
                int var11 = var9 / 2;
                if (this.field_287 == null) {
                    this.field_287 = new int[var9 * var9 * var9];
                }

                if (par1World.isRegionLoaded(par2 - var8, par3 - var8, par4 - var8, par2 + var8, par3 + var8, par4 + var8)) {
                    for(int var12 = -var7; var12 <= var7; ++var12) {
                        for(int var13 = -var7; var13 <= var7; ++var13) {
                            for(int var14 = -var7; var14 <= var7; ++var14) {
                                int var15 = par1World.getBlock(par2 + var12, par3 + var13, par4 + var14);
                                Block block = Block.BLOCKS[var15];
                                if (block != null && block.canSustainLeaves(par1World, par2 + var12, par3 + var13, par4 + var14)) {
                                    this.field_287[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = 0;
                                } else if (block != null && block.isLeaves(par1World, par2 + var12, par3 + var13, par4 + var14)) {
                                    this.field_287[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -2;
                                } else {
                                    this.field_287[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -1;
                                }
                            }
                        }
                    }

                    for(int var17 = 1; var17 <= 4; ++var17) {
                        for(int var13 = -var7; var13 <= var7; ++var13) {
                            for(int var14 = -var7; var14 <= var7; ++var14) {
                                for(int var15 = -var7; var15 <= var7; ++var15) {
                                    if (this.field_287[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11] == var17 - 1) {
                                        if (this.field_287[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2) {
                                            this.field_287[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var17;
                                        }

                                        if (this.field_287[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2) {
                                            this.field_287[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var17;
                                        }

                                        if (this.field_287[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] == -2) {
                                            this.field_287[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] = var17;
                                        }

                                        if (this.field_287[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] == -2) {
                                            this.field_287[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] = var17;
                                        }

                                        if (this.field_287[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] == -2) {
                                            this.field_287[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] = var17;
                                        }

                                        if (this.field_287[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] == -2) {
                                            this.field_287[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] = var17;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int var12 = this.field_287[var11 * var10 + var11 * var9 + var11];
                if (var12 >= 0) {
                    par1World.method_3682(par2, par3, par4, var6 & -9);
                } else {
                    this.method_326(par1World, par2, par3, par4);
                }
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_424(World par1World, PlayerEntity par2EntityPlayer, int par3, int par4, int par5, int par6) {
        super.method_424(par1World, par2EntityPlayer, par3, par4, par5, par6);
    }

    @Override
    public boolean isShearable(ItemStack item, World world, int x, int y, int z) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList();
        ret.add(new ItemStack(this, 1, world.getBlockData(x, y, z) & 3));
        return ret;
    }

    @Override
    public void beginLeavesDecay(World world, int x, int y, int z) {
        world.method_3682(x, y, z, world.getBlockData(x, y, z) | 8);
    }

    @Override
    public boolean isLeaves(World world, int x, int y, int z) {
        return true;
    }
}
