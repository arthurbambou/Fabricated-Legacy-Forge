package fr.catcore.fabricatedforge.mixin.forgefml.client.gui.screen.world;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.world.level.LevelGeneratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin extends Screen {
    @Shadow private int generatorType;

    @Inject(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;setGameModeWithString(Ljava/lang/String;)Lnet/minecraft/world/GameMode;"))
    private void FMLOnGUICreateWorldPress(ButtonWidget par1, CallbackInfo ci) {
        LevelGeneratorType.TYPES[this.generatorType].onGUICreateWorldPress();
    }
}
