package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CactusBlock.class)
public abstract class CactusBlockMixin extends Block implements IPlantable {

    public CactusBlockMixin(int id, Material material) {
        super(id, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_450(World par1World, int par2, int par3, int par4) {
        if (par1World.getMaterial(par2 - 1, par3, par4).hasCollision()) {
            return false;
        } else if (par1World.getMaterial(par2 + 1, par3, par4).hasCollision()) {
            return false;
        } else if (par1World.getMaterial(par2, par3, par4 - 1).hasCollision()) {
            return false;
        } else if (par1World.getMaterial(par2, par3, par4 + 1).hasCollision()) {
            return false;
        } else {
            int var5 = par1World.getBlock(par2, par3 - 1, par4);
            return BLOCKS[var5] != null && BLOCKS[var5].canSustainPlant(par1World, par2, par3 - 1, par4, ForgeDirection.UP, this);
        }
    }

    @Override
    public EnumPlantType getPlantType(World world, int x, int y, int z) {
        return EnumPlantType.Desert;
    }

    @Override
    public int getPlantID(World world, int x, int y, int z) {
        return this.id;
    }

    @Override
    public int getPlantMetadata(World world, int x, int y, int z) {
        return -1;
    }
}
