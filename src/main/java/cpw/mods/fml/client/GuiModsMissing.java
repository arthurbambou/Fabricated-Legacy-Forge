package cpw.mods.fml.client;

import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import net.minecraft.client.gui.screen.FatalErrorScreen;

public class GuiModsMissing extends FatalErrorScreen {
    private MissingModsException modsMissing;

    public GuiModsMissing(MissingModsException modsMissing) {
        this.modsMissing = modsMissing;
    }

    public void init() {
        super.init();
    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();
        int offset = Math.max(85 - this.modsMissing.missingMods.size() * 10, 10);
        this.drawCenteredString(this.textRenderer, "Forge Mod Loader has found a problem with your minecraft installation", this.width / 2, offset, 16777215);
        offset += 10;
        this.drawCenteredString(this.textRenderer, "The mods and versions listed below could not be found", this.width / 2, offset, 16777215);
        offset += 5;

        for(ArtifactVersion v : this.modsMissing.missingMods) {
            offset += 10;
            this.drawCenteredString(this.textRenderer, String.format("%s : %s", v.getLabel(), v.getRangeString()), this.width / 2, offset, 15658734);
        }

        offset += 20;
        this.drawCenteredString(this.textRenderer, "The file 'ForgeModLoader-client-0.log' contains more information", this.width / 2, offset, 16777215);
    }
}
