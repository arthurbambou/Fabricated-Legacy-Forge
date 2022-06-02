package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.mixininterface.IBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.Material;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin extends Block {

    public FarmlandBlockMixin(int id, Material material) {
        super(id, material);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private boolean method_310(World par1World, int par2, int par3, int par4) {
        byte var5 = 0;

        for(int var6 = par2 - var5; var6 <= par2 + var5; ++var6) {
            for(int var7 = par4 - var5; var7 <= par4 + var5; ++var7) {
                int var8 = par1World.getBlock(var6, par3 + 1, var7);
                Block plant = BLOCKS[var8];
                if (plant instanceof IPlantable && this.canSustainPlant(par1World, par2, par3, par4, ForgeDirection.UP, (IPlantable)plant)) {
                    return true;
                }
            }
        }

        return false;
    }
}
