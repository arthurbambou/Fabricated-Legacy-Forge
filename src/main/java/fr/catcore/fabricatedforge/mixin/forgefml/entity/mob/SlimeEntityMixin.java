package fr.catcore.fabricatedforge.mixin.forgefml.entity.mob;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin extends MobEntity {
    @Shadow public abstract int getSize();

    public SlimeEntityMixin(World world) {
        super(world);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean canSpawn() {
        Chunk var1 = this.world.getChunkFromPos(MathHelper.floor(this.x), MathHelper.floor(this.z));
        if (this.world.getLevelProperties().getGeneratorType().handleSlimeSpawnReduction(this.random, this.world)) {
            return false;
        } else {
            if (this.getSize() == 1 || this.world.field_4556 > 0) {
                if (this.world.method_3773(MathHelper.floor(this.x), MathHelper.floor(this.z)) == Biome.SWAMPLAND
                        && this.y > 50.0
                        && this.y < 70.0
                        && this.world.method_3720(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z)) <= this.random.nextInt(8)) {
                    return super.canSpawn();
                }

                if (this.random.nextInt(10) == 0 && var1.getRandom(987234911L).nextInt(10) == 0 && this.y < 40.0) {
                    return super.canSpawn();
                }
            }

            return false;
        }
    }
}
