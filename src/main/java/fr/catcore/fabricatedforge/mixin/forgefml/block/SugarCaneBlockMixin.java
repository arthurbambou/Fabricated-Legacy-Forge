package fr.catcore.fabricatedforge.mixin.forgefml.block;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SugarCaneBlock.class)
public abstract class SugarCaneBlockMixin extends Block implements IPlantable {
    public SugarCaneBlockMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public EnumPlantType getPlantType(World world, int x, int y, int z) {
        return EnumPlantType.Beach;
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
