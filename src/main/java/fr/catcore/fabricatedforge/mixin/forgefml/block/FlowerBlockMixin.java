package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FlowerBlock.class)
public abstract class FlowerBlockMixin extends Block implements IPlantable {

    public FlowerBlockMixin(int id, Material material) {
        super(id, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_434(World par1World, int par2, int par3, int par4) {
        return super.method_434(par1World, par2, par3, par4) && this.method_450(par1World, par2, par3, par4);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_450(World par1World, int par2, int par3, int par4) {
        Block soil = BLOCKS[par1World.getBlock(par2, par3 - 1, par4)];
        return (par1World.method_3718(par2, par3, par4) >= 8 || par1World.method_3716(par2, par3, par4))
                && soil != null
                && soil.canSustainPlant(par1World, par2, par3 - 1, par4, ForgeDirection.UP, this);
    }

    @Override
    public EnumPlantType getPlantType(World world, int x, int y, int z) {
        if (this.id == WHEAT.id) {
            return EnumPlantType.Crop;
        } else if (this.id == DEADBUSH.id) {
            return EnumPlantType.Desert;
        } else if (this.id == LILY_PAD.id) {
            return EnumPlantType.Water;
        } else if (this.id == RED_MUSHROOM.id) {
            return EnumPlantType.Cave;
        } else if (this.id == BROWN_MUSHROOM.id) {
            return EnumPlantType.Cave;
        } else if (this.id == NETHER_WART.id) {
            return EnumPlantType.Nether;
        } else if (this.id == SAPLING.id) {
            return EnumPlantType.Plains;
        } else if (this.id == MELON_STEM.id) {
            return EnumPlantType.Crop;
        } else if (this.id == PUMPKIN_STEM.id) {
            return EnumPlantType.Crop;
        } else {
            return EnumPlantType.Plains;
        }
    }

    @Override
    public int getPlantID(World world, int x, int y, int z) {
        return this.id;
    }

    @Override
    public int getPlantMetadata(World world, int x, int y, int z) {
        return world.getBlockData(x, y, z);
    }
}
