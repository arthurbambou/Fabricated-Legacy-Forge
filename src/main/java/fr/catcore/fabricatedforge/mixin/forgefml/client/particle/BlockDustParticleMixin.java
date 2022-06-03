package fr.catcore.fabricatedforge.mixin.forgefml.client.particle;

import net.minecraft.block.Block;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockDustParticle.class)
public class BlockDustParticleMixin extends Particle {

    @Shadow private Block field_1752;
    // Forge Field
    @Unique
    private int side;

    public BlockDustParticleMixin(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ctrSetSide(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, Block par14Block, int par15, int par16, CallbackInfo ci) {
        this.setMiscTexture(par14Block.method_396(par15, par16));
        this.side = par15;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public BlockDustParticle method_1301(int par1, int par2, int par3) {
        if (this.field_1752 == Block.GRASS_BLOCK && this.side != 1) {
            return (BlockDustParticle)(Object) this;
        } else {
            int var4 = this.field_1752.method_438(this.world, par1, par2, par3);
            this.red *= (float)(var4 >> 16 & 0xFF) / 255.0F;
            this.green *= (float)(var4 >> 8 & 0xFF) / 255.0F;
            this.blue *= (float)(var4 & 0xFF) / 255.0F;
            return (BlockDustParticle)(Object) this;
        }
    }
}
