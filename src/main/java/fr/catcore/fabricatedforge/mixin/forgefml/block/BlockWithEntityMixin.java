package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import fr.catcore.fabricatedforge.mixininterface.IBlockWithEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockWithEntity.class)
public abstract class BlockWithEntityMixin extends Block implements IBlockWithEntity {

    @Shadow public abstract BlockEntity method_309(World world);

    public BlockWithEntityMixin(int id, Material material) {
        super(id, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_460(World par1World, int par2, int par3, int par4) {
        super.method_460(par1World, par2, par3, par4);
        par1World.method_3603(par2, par3, par4, this.createTileEntity(par1World, par1World.getBlockData(par2, par3, par4)));
    }

    @Override
    public BlockEntity createNewTileEntity(World world, int metadata) {
        return this.method_309(world);
    }
}
