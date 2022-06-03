package fr.catcore.fabricatedforge.mixin.forgefml.client.options;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {

    @Shadow public float fov;

    @Shadow public float gamma;

    @Shadow public float musicVolume;

    @Shadow public float soundVolume;

    @Shadow public float sensitivity;

    @Shadow public float chatOpacity;

    @Shadow public int renderDistance;

    @Shadow public int difficultyLevel;

    @Shadow public int guiScale;

    @Shadow public int chatVisibility;

    @Shadow public int particle;

    @Shadow public int maxFramerate;

    @Shadow public boolean fancyGraphics;

    @Shadow private File optionsFile;

    @Shadow public boolean invertYMouse;

    @Shadow public boolean bobView;

    @Shadow public boolean anaglyph3d;

    @Shadow public boolean advancedOpengl;

    @Shadow public boolean ambientOcculsion;

    @Shadow public boolean renderClouds;

    @Shadow public String currentTexturePackName;

    @Shadow public String lastServer;

    @Shadow public String language;

    @Shadow public boolean chatColor;

    @Shadow public boolean chatLink;

    @Shadow public boolean chatLinkPrompt;

    @Shadow public boolean useServerTextures;

    @Shadow public boolean snopperEnabled;

    @Shadow public boolean fullscreen;

    @Shadow public boolean vsync;

    @Shadow public boolean hideServerAddress;

    @Shadow public KeyBinding[] keysAll;

    @Shadow public abstract void onPlayerModelPartChange();

    @Shadow public boolean advancedItemTooltips;

    @Shadow public boolean pauseOnLostFocus;

    @Shadow public boolean field_5053;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void save() {
        if (!FMLClientHandler.instance().isLoading()) {
            try {
                PrintWriter var1 = new PrintWriter(new FileWriter(this.optionsFile));
                var1.println("music:" + this.musicVolume);
                var1.println("sound:" + this.soundVolume);
                var1.println("invertYMouse:" + this.invertYMouse);
                var1.println("mouseSensitivity:" + this.sensitivity);
                var1.println("fov:" + this.fov);
                var1.println("gamma:" + this.gamma);
                var1.println("viewDistance:" + this.renderDistance);
                var1.println("guiScale:" + this.guiScale);
                var1.println("particles:" + this.particle);
                var1.println("bobView:" + this.bobView);
                var1.println("anaglyph3d:" + this.anaglyph3d);
                var1.println("advancedOpengl:" + this.advancedOpengl);
                var1.println("fpsLimit:" + this.maxFramerate);
                var1.println("difficulty:" + this.difficultyLevel);
                var1.println("fancyGraphics:" + this.fancyGraphics);
                var1.println("ao:" + this.ambientOcculsion);
                var1.println("clouds:" + this.renderClouds);
                var1.println("skin:" + this.currentTexturePackName);
                var1.println("lastServer:" + this.lastServer);
                var1.println("lang:" + this.language);
                var1.println("chatVisibility:" + this.chatVisibility);
                var1.println("chatColors:" + this.chatColor);
                var1.println("chatLinks:" + this.chatLink);
                var1.println("chatLinksPrompt:" + this.chatLinkPrompt);
                var1.println("chatOpacity:" + this.chatOpacity);
                var1.println("serverTextures:" + this.useServerTextures);
                var1.println("snooperEnabled:" + this.snopperEnabled);
                var1.println("fullscreen:" + this.fullscreen);
                var1.println("enableVsync:" + this.vsync);
                var1.println("hideServerAddress:" + this.hideServerAddress);
                var1.println("advancedItemTooltips:" + this.advancedItemTooltips);
                var1.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
                var1.println("showCape:" + this.field_5053);

                for(KeyBinding var5 : this.keysAll) {
                    var1.println("key_" + var5.translationKey + ":" + var5.code);
                }

                var1.close();
            } catch (Exception var61) {
                System.out.println("Failed to save options");
                var61.printStackTrace();
            }

            this.onPlayerModelPartChange();
        }
    }
}
