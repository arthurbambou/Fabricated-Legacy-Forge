package fr.catcore.fabricatedforge.mixin.forgefml.block;

import fr.catcore.fabricatedforge.forged.ReflectionUtils;
import fr.catcore.fabricatedforge.mixininterface.IRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.RailBlock;
import net.minecraft.block.class_174;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import org.spongepowered.asm.mixin.*;

@Mixin(RailBlock.class)
public abstract class RailBlockMixin extends Block implements IRailBlock {

    @Shadow @Final private boolean field_304;

    @Shadow protected abstract boolean method_351(World world, int i, int j, int k, int l, boolean bl, int m);

    @Shadow protected abstract void method_352(World world, int i, int j, int k, boolean bl);

    public RailBlockMixin(int id, Material material) {
        super(id, material);
    }

    // Forge Field
    @Unique
    private int renderType = 9;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static final boolean method_355(World par0World, int par1, int par2, int par3) {
        int var4 = par0World.getBlock(par1, par2, par3);
        return method_354(var4);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static final boolean method_354(int par0) {
        return Block.BLOCKS[par0] instanceof RailBlock;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int getBlockType() {
        return this.renderType;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_434(World par1World, int par2, int par3, int par4) {
        return par1World.isBlockSolidOnSide(par2, par3 - 1, par4, ForgeDirection.UP);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_408(World par1World, int par2, int par3, int par4, int par5) {
        if (!par1World.isClient) {
            int var6 = par1World.getBlockData(par2, par3, par4);
            int var7 = var6;
            if (this.field_304) {
                var7 = var6 & 7;
            }

            boolean var8 = false;
            if (!par1World.isBlockSolidOnSide(par2, par3 - 1, par4, ForgeDirection.UP)) {
                var8 = true;
            }

            if (var7 == 2 && !par1World.isBlockSolidOnSide(par2 + 1, par3, par4, ForgeDirection.UP)) {
                var8 = true;
            }

            if (var7 == 3 && !par1World.isBlockSolidOnSide(par2 - 1, par3, par4, ForgeDirection.UP)) {
                var8 = true;
            }

            if (var7 == 4 && !par1World.isBlockSolidOnSide(par2, par3, par4 - 1, ForgeDirection.UP)) {
                var8 = true;
            }

            if (var7 == 5 && !par1World.isBlockSolidOnSide(par2, par3, par4 + 1, ForgeDirection.UP)) {
                var8 = true;
            }

            if (var8) {
                this.method_445(par1World, par2, par3, par4, par1World.getBlockData(par2, par3, par4), 0);
                par1World.method_3690(par2, par3, par4, 0);
            } else if (this.id == Block.POWERED_RAIL.id) {
                boolean var9 = par1World.method_3739(par2, par3, par4);
                var9 = var9 || this.method_351(par1World, par2, par3, par4, var6, true, 0) || this.method_351(par1World, par2, par3, par4, var6, false, 0);
                boolean var10 = false;
                if (var9 && (var6 & 8) == 0) {
                    par1World.method_3672(par2, par3, par4, var7 | 8);
                    var10 = true;
                } else if (!var9 && (var6 & 8) != 0) {
                    par1World.method_3672(par2, par3, par4, var7);
                    var10 = true;
                }

                if (var10) {
                    par1World.method_3710(par2, par3 - 1, par4, this.id);
                    if (var7 == 2 || var7 == 3 || var7 == 4 || var7 == 5) {
                        par1World.method_3710(par2, par3 + 1, par4, this.id);
                    }
                }
            } else if (par5 > 0
                    && Block.BLOCKS[par5].emitsRedstonePower()
                    && !this.field_304
                    && ReflectionUtils.class_174_method_360(new class_174((RailBlock)(Object) this, par1World, par2, par3, par4)) == 3) {
                this.method_352(par1World, par2, par3, par4, false);
            }
        }

    }

    @Override
    public void setRenderType(int value) {
        this.renderType = value;
    }

    @Override
    public boolean isFlexibleRail(World world, int y, int x, int z) {
        return !this.field_304;
    }

    @Override
    public boolean canMakeSlopes(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public int getBasicRailMetadata(BlockView world, AbstractMinecartEntity cart, int x, int y, int z) {
        int meta = world.getBlockData(x, y, z);
        if (this.field_304) {
            meta &= 7;
        }

        return meta;
    }

    @Override
    public float getRailMaxSpeed(World world, AbstractMinecartEntity cart, int y, int x, int z) {
        return 0.4F;
    }

    @Override
    public void onMinecartPass(World world, AbstractMinecartEntity cart, int y, int x, int z) {
    }

    @Override
    public boolean hasPowerBit(World world, int x, int y, int z) {
        return this.field_304;
    }

    @Override
    public boolean getField_304() {
        return this.field_304;
    }
}
