package cpw.mods.fml.client;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.LoaderState.ModState;
import java.util.ArrayList;
import net.minecraft.client.render.Tessellator;

public class GuiSlotModList extends GuiScrollingList {
    private GuiModList parent;
    private ArrayList<ModContainer> mods;

    public GuiSlotModList(GuiModList parent, ArrayList<ModContainer> mods, int listWidth) {
        super(parent.getMinecraftInstance(), listWidth, parent.height, 32, parent.height - 65 + 4, 10, 35);
        this.parent = parent;
        this.mods = mods;
    }

    protected int getSize() {
        return this.mods.size();
    }

    protected void elementClicked(int var1, boolean var2) {
        this.parent.selectModIndex(var1);
    }

    protected boolean isSelected(int var1) {
        return this.parent.modIndexSelected(var1);
    }

    protected void drawBackground() {
        this.parent.renderBackground();
    }

    protected int getContentHeight() {
        return this.getSize() * 35 + 1;
    }

    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5) {
        ModContainer mc = (ModContainer)this.mods.get(listIndex);
        if (Loader.instance().getModState(mc) == ModState.DISABLED) {
            this.parent
                    .getFontRenderer()
                    .method_4247(this.parent.getFontRenderer().trimToWidth(mc.getName(), this.listWidth - 10), this.left + 3, var3 + 2, 16720418);
            this.parent
                    .getFontRenderer()
                    .method_4247(this.parent.getFontRenderer().trimToWidth(mc.getDisplayVersion(), this.listWidth - 10), this.left + 3, var3 + 12, 16720418);
            this.parent
                    .getFontRenderer()
                    .method_4247(this.parent.getFontRenderer().trimToWidth("DISABLED", this.listWidth - 10), this.left + 3, var3 + 22, 16720418);
        } else {
            this.parent
                    .getFontRenderer()
                    .method_4247(this.parent.getFontRenderer().trimToWidth(mc.getName(), this.listWidth - 10), this.left + 3, var3 + 2, 16777215);
            this.parent
                    .getFontRenderer()
                    .method_4247(this.parent.getFontRenderer().trimToWidth(mc.getDisplayVersion(), this.listWidth - 10), this.left + 3, var3 + 12, 13421772);
            this.parent
                    .getFontRenderer()
                    .method_4247(
                            this.parent
                                    .getFontRenderer()
                                    .trimToWidth(mc.getMetadata() != null ? mc.getMetadata().getChildModCountString() : "Metadata not found", this.listWidth - 10),
                            this.left + 3,
                            var3 + 22,
                            13421772
                    );
        }

    }
}
