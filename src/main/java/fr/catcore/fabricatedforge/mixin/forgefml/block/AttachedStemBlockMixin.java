package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Random;

@Mixin(AttachedStemBlock.class)
public abstract class AttachedStemBlockMixin extends FlowerBlock {

    @Shadow private Block mainBlock;

    protected AttachedStemBlockMixin(int i, int j, Material material) {
        super(i, j, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_436(World par1World, int par2, int par3, int par4, Random par5Random) {
        super.method_436(par1World, par2, par3, par4, par5Random);
        if (par1World.method_3720(par2, par3 + 1, par4) >= 9) {
            float var6 = this.method_386(par1World, par2, par3, par4);
            if (par5Random.nextInt((int)(25.0F / var6) + 1) == 0) {
                int var7 = par1World.getBlockData(par2, par3, par4);
                if (var7 < 7) {
                    par1World.method_3672(par2, par3, par4, ++var7);
                } else {
                    if (par1World.getBlock(par2 - 1, par3, par4) == this.mainBlock.id) {
                        return;
                    }

                    if (par1World.getBlock(par2 + 1, par3, par4) == this.mainBlock.id) {
                        return;
                    }

                    if (par1World.getBlock(par2, par3, par4 - 1) == this.mainBlock.id) {
                        return;
                    }

                    if (par1World.getBlock(par2, par3, par4 + 1) == this.mainBlock.id) {
                        return;
                    }

                    int var8 = par5Random.nextInt(4);
                    int var9 = par2;
                    int var10 = par4;
                    if (var8 == 0) {
                        var9 = par2 - 1;
                    }

                    if (var8 == 1) {
                        ++var9;
                    }

                    if (var8 == 2) {
                        var10 = par4 - 1;
                    }

                    if (var8 == 3) {
                        ++var10;
                    }

                    int var11 = par1World.getBlock(var9, par3 - 1, var10);
                    boolean isSoil = BLOCKS[var11] != null && BLOCKS[var11].canSustainPlant(par1World, var9, par3 - 1, var10, ForgeDirection.UP, this);
                    if (par1World.getBlock(var9, par3, var10) == 0 && (isSoil || var11 == Block.DIRT.id || var11 == Block.GRASS_BLOCK.id)) {
                        par1World.method_3690(var9, par3, var10, this.mainBlock.id);
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
    private float method_386(World par1World, int par2, int par3, int par4) {
        float var5 = 1.0F;
        int var6 = par1World.getBlock(par2, par3, par4 - 1);
        int var7 = par1World.getBlock(par2, par3, par4 + 1);
        int var8 = par1World.getBlock(par2 - 1, par3, par4);
        int var9 = par1World.getBlock(par2 + 1, par3, par4);
        int var10 = par1World.getBlock(par2 - 1, par3, par4 - 1);
        int var11 = par1World.getBlock(par2 + 1, par3, par4 - 1);
        int var12 = par1World.getBlock(par2 + 1, par3, par4 + 1);
        int var13 = par1World.getBlock(par2 - 1, par3, par4 + 1);
        boolean var14 = var8 == this.id || var9 == this.id;
        boolean var15 = var6 == this.id || var7 == this.id;
        boolean var16 = var10 == this.id || var11 == this.id || var12 == this.id || var13 == this.id;

        for(int var17 = par2 - 1; var17 <= par2 + 1; ++var17) {
            for(int var18 = par4 - 1; var18 <= par4 + 1; ++var18) {
                int var19 = par1World.getBlock(var17, par3 - 1, var18);
                float var20 = 0.0F;
                if (BLOCKS[var19] != null && BLOCKS[var19].canSustainPlant(par1World, var17, par3 - 1, var18, ForgeDirection.UP, this)) {
                    var20 = 1.0F;
                    if (BLOCKS[var19].isFertile(par1World, var17, par3 - 1, var18)) {
                        var20 = 3.0F;
                    }
                }

                if (var17 != par2 || var18 != par4) {
                    var20 /= 4.0F;
                }

                var5 += var20;
            }
        }

        if (var16 || var14 && var15) {
            var5 /= 2.0F;
        }

        return var5;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_410(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
        super.method_410(par1World, par2, par3, par4, par5, par6, par7);
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList();

        for(int i = 0; i < 3; ++i) {
            if (world.random.nextInt(15) <= metadata) {
                ret.add(new ItemStack(this.mainBlock == PUMPKIN ? Item.PUMPKIN_SEEDS : Item.MELON_SEEDS));
            }
        }

        return ret;
    }
}
