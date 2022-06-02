package cpw.mods.fml.client;

import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.util.Language;

public class GuiModsMissingForServer extends Screen {
    private ModMissingPacket modsMissing;

    public GuiModsMissingForServer(ModMissingPacket modsMissing) {
        this.modsMissing = modsMissing;
    }

    public void init() {
        Language translations = Language.getInstance();
        this.buttons.add(new OptionButtonWidget(1, this.width / 2 - 75, this.height - 38, translations.translate("gui.done")));
    }

    protected void buttonClicked(ButtonWidget par1GuiButton) {
        if (par1GuiButton.active && par1GuiButton.id == 1) {
            FMLClientHandler.instance().getClient().openScreen(null);
        }

    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();
        int offset = Math.max(85 - this.modsMissing.getModList().size() * 10, 10);
        this.drawCenteredString(this.textRenderer, "Forge Mod Loader could not connect to this server", this.width / 2, offset, 16777215);
        offset += 10;
        this.drawCenteredString(this.textRenderer, "The mods and versions listed below could not be found", this.width / 2, offset, 16777215);
        offset += 10;
        this.drawCenteredString(this.textRenderer, "They are required to play on this server", this.width / 2, offset, 16777215);
        offset += 5;

        for(ArtifactVersion v : this.modsMissing.getModList()) {
            offset += 10;
            this.drawCenteredString(this.textRenderer, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.width / 2, offset, 15658734);
        }

        super.render(par1, par2, par3);
    }
}
