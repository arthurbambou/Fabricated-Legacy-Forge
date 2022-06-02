package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import net.minecraft.block.CropBlock;
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

@Mixin(CropBlock.class)
public abstract class CropBlockMixin extends FlowerBlock {

    @Shadow protected abstract int method_4164();

    protected CropBlockMixin(int i, int j, Material material) {
        super(i, j, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private float method_294(World par1World, int par2, int par3, int par4) {
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
        super.method_410(par1World, par2, par3, par4, par5, par6, 0);
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList();
        if (metadata == 7) {
            int count = this.quantityDropped(metadata, fortune, world.random);

            for(int i = 0; i < count; ++i) {
                int id = this.method_398(metadata, world.random, 0);
                if (id > 0) {
                    ret.add(new ItemStack(id, 1, this.method_431(metadata)));
                }
            }
        }

        if (metadata >= 7) {
            for(int n = 0; n < 3 + fortune; ++n) {
                if (world.random.nextInt(15) <= metadata) {
                    ret.add(new ItemStack(this.method_4164(), 1, 0));
                }
            }
        }

        return ret;
    }
}
