package cpw.mods.fml.client;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.WrongMinecraftVersionException;
import net.minecraft.client.gui.screen.FatalErrorScreen;

public class GuiWrongMinecraft extends FatalErrorScreen {
    private WrongMinecraftVersionException wrongMC;

    public GuiWrongMinecraft(WrongMinecraftVersionException wrongMC) {
        this.wrongMC = wrongMC;
    }

    public void init() {
        super.init();
    }

    public void render(int par1, int par2, float par3) {
        this.renderBackground();
        int offset = 75;
        this.drawCenteredString(this.textRenderer, "Forge Mod Loader has found a problem with your minecraft installation", this.width / 2, offset, 16777215);
        offset += 10;
        this.drawCenteredString(
                this.textRenderer,
                String.format("The mod listed below does not want to run in Minecraft version %s", Loader.instance().getMinecraftModContainer().getVersion()),
                this.width / 2,
                offset,
                16777215
        );
        offset += 5;
        offset += 10;
        this.drawCenteredString(
                this.textRenderer,
                String.format(
                        "%s (%s) wants Minecraft %s", this.wrongMC.mod.getName(), this.wrongMC.mod.getModId(), this.wrongMC.mod.acceptableMinecraftVersionRange()
                ),
                this.width / 2,
                offset,
                15658734
        );
        offset += 20;
        this.drawCenteredString(this.textRenderer, "The file 'ForgeModLoader-client-0.log' contains more information", this.width / 2, offset, 16777215);
    }
}
