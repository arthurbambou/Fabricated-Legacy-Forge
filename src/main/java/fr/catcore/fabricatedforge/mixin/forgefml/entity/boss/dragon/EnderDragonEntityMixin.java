package fr.catcore.fabricatedforge.mixin.forgefml.entity.boss.dragon;

import net.minecraft.block.Block;
import net.minecraft.entity.MultipartEntityProvider;
import net.minecraft.entity.boss.BossBarProvider;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin extends MobEntity implements BossBarProvider, MultipartEntityProvider {

    public EnderDragonEntityMixin(World world) {
        super(world);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private boolean destroyBlocks(Box par1AxisAlignedBB) {
        int var2 = MathHelper.floor(par1AxisAlignedBB.minX);
        int var3 = MathHelper.floor(par1AxisAlignedBB.minY);
        int var4 = MathHelper.floor(par1AxisAlignedBB.minZ);
        int var5 = MathHelper.floor(par1AxisAlignedBB.maxX);
        int var6 = MathHelper.floor(par1AxisAlignedBB.maxY);
        int var7 = MathHelper.floor(par1AxisAlignedBB.maxZ);
        boolean var8 = false;
        boolean var9 = false;

        for(int var10 = var2; var10 <= var5; ++var10) {
            for(int var11 = var3; var11 <= var6; ++var11) {
                for(int var12 = var4; var12 <= var7; ++var12) {
                    int var13 = this.world.getBlock(var10, var11, var12);
                    Block block = Block.BLOCKS[var13];
                    if (block != null) {
                        if (block.canDragonDestroy(this.world, var10, var11, var12)) {
                            var9 = true;
                            this.world.method_3690(var10, var11, var12, 0);
                        } else {
                            var8 = true;
                        }
                    }
                }
            }
        }

        if (var9) {
            double var16 = par1AxisAlignedBB.minX + (par1AxisAlignedBB.maxX - par1AxisAlignedBB.minX) * (double)this.random.nextFloat();
            double var17 = par1AxisAlignedBB.minY + (par1AxisAlignedBB.maxY - par1AxisAlignedBB.minY) * (double)this.random.nextFloat();
            double var14 = par1AxisAlignedBB.minZ + (par1AxisAlignedBB.maxZ - par1AxisAlignedBB.minZ) * (double)this.random.nextFloat();
            this.world.method_3621("largeexplode", var16, var17, var14, 0.0, 0.0, 0.0);
        }

        return var8;
    }
}
