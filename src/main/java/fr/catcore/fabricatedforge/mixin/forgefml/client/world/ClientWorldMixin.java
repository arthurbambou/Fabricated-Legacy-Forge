package fr.catcore.fabricatedforge.mixin.forgefml.client.world;

import net.minecraft.client.class_469;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World {

    public ClientWorldMixin(SaveHandler saveHandler, String string, Dimension dimension, LevelInfo levelInfo, Profiler profiler) {
        super(saveHandler, string, dimension, levelInfo, profiler);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;method_3578(III)V"))
    private void ctrFix1(class_469 par1NetClientHandler, LevelInfo par2WorldSettings, int par3, int par4, Profiler par5Profiler, CallbackInfo ci) {
        this.isClient = true;
        this.finishSetup();
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void ctrFix2(class_469 par1NetClientHandler, LevelInfo par2WorldSettings, int par3, int par4, Profiler par5Profiler, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(this));
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void tickWeather() {
        super.tickWeather();
    }

    @Override
    public void updateWeatherBody() {
        if (!this.dimension.isNether) {
            if (this.field_23088 > 0) {
                --this.field_23088;
            }

            this.rainGradientPrev = this.rainGradient;
            if (this.levelProperties.isRaining()) {
                this.rainGradient = (float)((double)this.rainGradient + 0.01);
            } else {
                this.rainGradient = (float)((double)this.rainGradient - 0.01);
            }

            if (this.rainGradient < 0.0F) {
                this.rainGradient = 0.0F;
            }

            if (this.rainGradient > 1.0F) {
                this.rainGradient = 1.0F;
            }

            this.thunderGradientPrev = this.thunderGradient;
            if (this.levelProperties.isThundering()) {
                this.thunderGradient = (float)((double)this.thunderGradient + 0.01);
            } else {
                this.thunderGradient = (float)((double)this.thunderGradient - 0.01);
            }

            if (this.thunderGradient < 0.0F) {
                this.thunderGradient = 0.0F;
            }

            if (this.thunderGradient > 1.0F) {
                this.thunderGradient = 1.0F;
            }
        }

    }
}
