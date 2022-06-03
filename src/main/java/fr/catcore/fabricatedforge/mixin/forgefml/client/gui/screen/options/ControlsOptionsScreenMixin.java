package fr.catcore.fabricatedforge.mixin.forgefml.client.gui.screen.options;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.Language;
import net.minecraftforge.client.GuiControlsScrollPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlsOptionsScreenMixin extends Screen {

    @Shadow private GameOptions options;

    @Shadow protected String title;
    @Shadow private Screen parent;

    @Shadow protected abstract int method_912();

    // Forge Field
    @Unique
    private GuiControlsScrollPanel scrollPane;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void init() {
        this.scrollPane = new GuiControlsScrollPanel((ControlsOptionsScreen)(Object) this, this.options, this.field_1229);
        Language var1 = Language.getInstance();
        int var2 = this.method_912();
        this.buttons.add(new ButtonWidget(200, this.width / 2 - 100, this.height - 28, var1.translate("gui.done")));
        this.scrollPane.method_1059(this.buttons, 7, 8);
        this.title = var1.translate("controls.title");
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void buttonClicked(ButtonWidget par1GuiButton) {
        if (par1GuiButton.id == 200) {
            this.field_1229.openScreen(this.parent);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void keyPressed(char par1, int par2) {
        if (this.scrollPane.keyTyped(par1, par2)) {
            super.keyPressed(par1, par2);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void render(int par1, int par2, float par3) {
        this.renderBackground();
        this.scrollPane.render(par1, par2, par3);
        this.drawCenteredString(this.textRenderer, this.title, this.width / 2, 4, 16777215);
        super.render(par1, par2, par3);
    }
}
