package fr.catcore.fabricatedforge.mixin.forgefml.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(OcelotEntity.class)
public abstract class OcelotEntityMixin extends TameableEntity {
    public OcelotEntityMixin(World world) {
        super(world);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean canSpawn() {
        if (this.world.random.nextInt(3) == 0) {
            return false;
        } else {
            if (this.world.hasEntityIn(this.boundingBox)
                    && this.world.doesBoxCollide(this, this.boundingBox).isEmpty()
                    && !this.world.containsFluid(this.boundingBox)) {
                int var1 = MathHelper.floor(this.x);
                int var2 = MathHelper.floor(this.boundingBox.minY);
                int var3 = MathHelper.floor(this.z);
                if (var2 < 63) {
                    return false;
                }

                int var4 = this.world.getBlock(var1, var2 - 1, var3);
                Block block = Block.BLOCKS[var4];
                if (var4 == Block.GRASS_BLOCK.id || block != null && block.isLeaves(this.world, var1, var2 - 1, var3)) {
                    return true;
                }
            }

            return false;
        }
    }
}
