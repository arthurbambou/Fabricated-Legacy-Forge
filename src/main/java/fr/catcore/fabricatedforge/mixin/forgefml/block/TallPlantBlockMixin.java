package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.FlowerBlock;
import net.minecraft.block.Material;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.Random;

@Mixin(TallPlantBlock.class)
public abstract class TallPlantBlockMixin extends FlowerBlock implements IShearable {
    protected TallPlantBlockMixin(int i, int j, Material material) {
        super(i, j, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int method_398(int par1, Random par2Random, int par3) {
        return -1;
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
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList();
        if (world.random.nextInt(8) != 0) {
            return ret;
        } else {
            ItemStack item = ForgeHooks.getGrassSeed(world);
            if (item != null) {
                ret.add(item);
            }

            return ret;
        }
    }

    @Override
    public boolean isShearable(ItemStack item, World world, int x, int y, int z) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList();
        ret.add(new ItemStack(this, 1, world.getBlockData(x, y, z)));
        return ret;
    }
}
