package fr.catcore.fabricatedforge.mixin.forgefml.client.gui.screen.ingame;

import net.minecraft.client.class_416;
import net.minecraft.client.class_417;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.SurvivalInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Language;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends InventoryScreen {

    @Shadow private static int selectedTab;

    @Shadow private List slots;
    @Shadow private Slot deleteItemSlot;
    @Shadow private static SimpleInventory inventory;
    @Shadow private TextFieldWidget searchField;

    @Shadow protected abstract void search();

    @Shadow private float scrollPosition;
    @Shadow private boolean field_1379;
    @Shadow private boolean field_1378;

    // Forge Fields
    @Unique
    private int tabPage = 0;
    @Unique
    private int maxPages = 0;

    public CreativeInventoryScreenMixin(ScreenHandler screenHandler) {
        super(screenHandler);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void fmlAddChangeButton(CallbackInfo ci) {
        if (this.field_1229.interactionManager.hasCreativeInventory()) {
            int tabCount = ItemGroup.itemGroups.length;
            if (tabCount > 12) {
                this.buttons.add(new ButtonWidget(101, this.x, this.y - 50, 20, 20, "<"));
                this.buttons.add(new ButtonWidget(102, this.x + this.backgroundWidth - 20, this.y - 50, 20, 20, ">"));
                this.tabPage = 0;
                this.maxPages = (tabCount - 12) / 10 + 1;
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void drawForeground(int par1, int par2) {
        ItemGroup var3 = ItemGroup.itemGroups[selectedTab];
        if (var3 != null && var3.hasTooltip()) {
            this.textRenderer.method_4247(var3.getTranslationKey(), 8, 6, 4210752);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void mouseClicked(int par1, int par2, int par3) {
        if (par3 == 0) {
            int var4 = par1 - this.x;
            int var5 = par2 - this.y;

            for(ItemGroup var9 : ItemGroup.itemGroups) {
                if (var9 != null && this.isClickInTab(var9, var4, var5)) {
                    this.setSelectedTab(var9);
                    return;
                }
            }
        }

        super.mouseClicked(par1, par2, par3);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private boolean hasScrollbar() {
        if (ItemGroup.itemGroups[selectedTab] == null) {
            return false;
        } else {
            return selectedTab != ItemGroup.INVENTORY.getIndex()
                    && ItemGroup.itemGroups[selectedTab].hasScrollbar()
                    && ((class_416)this.screenHandler).method_1153();
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void setSelectedTab(ItemGroup par1CreativeTabs) {
        if (par1CreativeTabs != null) {
            int var2 = selectedTab;
            selectedTab = par1CreativeTabs.getIndex();
            class_416 var3 = (class_416)this.screenHandler;
            var3.field_1384.clear();
            par1CreativeTabs.showItems(var3.field_1384);
            if (par1CreativeTabs == ItemGroup.INVENTORY) {
                ScreenHandler var4 = this.field_1229.playerEntity.playerScreenHandler;
                if (this.slots == null) {
                    this.slots = var3.slots;
                }

                var3.slots = new ArrayList();

                for(int var5 = 0; var5 < var4.slots.size(); ++var5) {
                    class_417 var6 = new class_417((CreativeInventoryScreen)(Object) this, (Slot)var4.slots.get(var5), var5);
                    var3.slots.add(var6);
                    if (var5 >= 5 && var5 < 9) {
                        int var7 = var5 - 5;
                        int var8 = var7 / 2;
                        int var9 = var7 % 2;
                        var6.xPosition = 9 + var8 * 54;
                        var6.yPosition = 6 + var9 * 27;
                    } else if (var5 >= 0 && var5 < 5) {
                        var6.yPosition = -2000;
                        var6.xPosition = -2000;
                    } else if (var5 < var4.slots.size()) {
                        int var7 = var5 - 9;
                        int var8 = var7 % 9;
                        int var9 = var7 / 9;
                        var6.xPosition = 9 + var8 * 18;
                        if (var5 >= 36) {
                            var6.yPosition = 112;
                        } else {
                            var6.yPosition = 54 + var9 * 18;
                        }
                    }
                }

                this.deleteItemSlot = new Slot(inventory, 0, 173, 112);
                var3.slots.add(this.deleteItemSlot);
            } else if (var2 == ItemGroup.INVENTORY.getIndex()) {
                var3.slots = this.slots;
                this.slots = null;
            }

            if (this.searchField != null) {
                if (par1CreativeTabs == ItemGroup.SEARCH) {
                    this.searchField.setVisible(true);
                    this.searchField.setFocusUnlocked(false);
                    this.searchField.setFocused(true);
                    this.searchField.setText("");
                    this.search();
                } else {
                    this.searchField.setVisible(false);
                    this.searchField.setFocusUnlocked(true);
                    this.searchField.setFocused(false);
                }
            }

            this.scrollPosition = 0.0F;
            var3.method_1152(0.0F);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void render(int par1, int par2, float par3) {
        boolean var4 = Mouse.isButtonDown(0);
        int var5 = this.x;
        int var6 = this.y;
        int var7 = var5 + 175;
        int var8 = var6 + 18;
        int var9 = var7 + 14;
        int var10 = var8 + 112;
        if (!this.field_1379 && var4 && par1 >= var7 && par2 >= var8 && par1 < var9 && par2 < var10) {
            this.field_1378 = this.hasScrollbar();
        }

        if (!var4) {
            this.field_1378 = false;
        }

        this.field_1379 = var4;
        if (this.field_1378) {
            this.scrollPosition = ((float)(par2 - var8) - 7.5F) / ((float)(var10 - var8) - 15.0F);
            if (this.scrollPosition < 0.0F) {
                this.scrollPosition = 0.0F;
            }

            if (this.scrollPosition > 1.0F) {
                this.scrollPosition = 1.0F;
            }

            ((class_416)this.screenHandler).method_1152(this.scrollPosition);
        }

        super.render(par1, par2, par3);
        ItemGroup[] var11 = ItemGroup.itemGroups;
        int start = this.tabPage * 10;
        int var12 = Math.min(var11.length, (this.tabPage + 1) * 10 + 2);
        if (this.tabPage != 0) {
            start += 2;
        }

        boolean rendered = false;

        for(int var13 = start; var13 < var12; ++var13) {
            ItemGroup var14 = var11[var13];
            if (var14 != null && this.renderTabTooltipIfHovered(var14, par1, par2)) {
                rendered = true;
                break;
            }
        }

        if (!rendered && !this.renderTabTooltipIfHovered(ItemGroup.SEARCH, par1, par2)) {
            this.renderTabTooltipIfHovered(ItemGroup.INVENTORY, par1, par2);
        }

        if (this.deleteItemSlot != null
                && selectedTab == ItemGroup.INVENTORY.getIndex()
                && this.isPointWithinBounds(this.deleteItemSlot.xPosition, this.deleteItemSlot.yPosition, 16, 16, par1, par2)) {
            this.method_1128(Language.getInstance().translate("inventory.binSlot"), par1, par2);
        }

        if (this.maxPages != 0) {
            String page = String.format("%d / %d", this.tabPage + 1, this.maxPages + 1);
            int width = this.textRenderer.getStringWidth(page);
            GL11.glDisable(2896);
            this.zOffset = 300.0F;
            field_1346.zOffset = 300.0F;
            this.textRenderer.method_4247(page, this.x + this.backgroundWidth / 2 - width / 2, this.y - 44, -1);
            this.zOffset = 0.0F;
            field_1346.zOffset = 0.0F;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void drawBackground(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        DiffuseLighting.enable();
        int var4 = this.field_1229.field_3813.getTextureFromPath("/gui/allitems.png");
        ItemGroup var5 = ItemGroup.itemGroups[selectedTab];
        int var6 = this.field_1229.field_3813.getTextureFromPath("/gui/creative_inv/" + var5.getTexture());
        ItemGroup[] var7 = ItemGroup.itemGroups;
        int var8 = var7.length;
        int start = this.tabPage * 10;
        var8 = Math.min(var7.length, (this.tabPage + 1) * 10 + 2);
        if (this.tabPage != 0) {
            start += 2;
        }

        for(int var9 = start; var9 < var8; ++var9) {
            ItemGroup var10 = var7[var9];
            this.field_1229.field_3813.method_1426(var4);
            if (var10 != null && var10.getIndex() != selectedTab) {
                this.renderTabIcon(var10);
            }
        }

        if (this.tabPage != 0) {
            if (var5 != ItemGroup.SEARCH) {
                this.field_1229.field_3813.method_1426(var4);
                this.renderTabIcon(ItemGroup.SEARCH);
            }

            if (var5 != ItemGroup.INVENTORY) {
                this.field_1229.field_3813.method_1426(var4);
                this.renderTabIcon(ItemGroup.INVENTORY);
            }
        }

        this.field_1229.field_3813.method_1426(var6);
        this.drawTexture(this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.searchField.render();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int var11 = this.x + 175;
        var8 = this.y + 18;
        int var14 = var8 + 112;
        this.field_1229.field_3813.method_1426(var4);
        if (var5 != null && var5.getTabPage() == this.tabPage || var5 == ItemGroup.SEARCH || var5 == ItemGroup.INVENTORY) {
            if (var5.hasScrollbar()) {
                this.drawTexture(var11, var8 + (int)((float)(var14 - var8 - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
            }

            this.renderTabIcon(var5);
            if (var5 == ItemGroup.INVENTORY) {
                SurvivalInventoryScreen.method_1159(
                        this.field_1229, this.x + 43, this.y + 45, 20, (float)(this.x + 43 - par2), (float)(this.y + 45 - 30 - par3)
                );
            }

        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected boolean isClickInTab(ItemGroup par1CreativeTabs, int par2, int par3) {
        if (par1CreativeTabs.getTabPage() != this.tabPage && par1CreativeTabs != ItemGroup.SEARCH && par1CreativeTabs != ItemGroup.INVENTORY) {
            return false;
        } else {
            int var4 = par1CreativeTabs.getColumn();
            int var5 = 28 * var4;
            byte var6 = 0;
            if (var4 == 5) {
                var5 = this.backgroundWidth - 28 + 2;
            } else if (var4 > 0) {
                var5 += var4;
            }

            int var7;
            if (par1CreativeTabs.isTopRow()) {
                var7 = var6 - 32;
            } else {
                var7 = var6 + this.backgroundHeight;
            }

            return par2 >= var5 && par2 <= var5 + 28 && par3 >= var7 && par3 <= var7 + 32;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected boolean renderTabTooltipIfHovered(ItemGroup par1CreativeTabs, int par2, int par3) {
        int var4 = par1CreativeTabs.getColumn();
        int var5 = 28 * var4;
        byte var6 = 0;
        if (var4 == 5) {
            var5 = this.backgroundWidth - 28 + 2;
        } else if (var4 > 0) {
            var5 += var4;
        }

        int var7;
        if (par1CreativeTabs.isTopRow()) {
            var7 = var6 - 32;
        } else {
            var7 = var6 + this.backgroundHeight;
        }

        if (this.isPointWithinBounds(var5 + 3, var7 + 3, 23, 27, par2, par3)) {
            this.method_1128(par1CreativeTabs.getTranslationKey(), par2, par3);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void renderTabIcon(ItemGroup par1CreativeTabs) {
        boolean var2 = par1CreativeTabs.getIndex() == selectedTab;
        boolean var3 = par1CreativeTabs.isTopRow();
        int var4 = par1CreativeTabs.getColumn();
        int var5 = var4 * 28;
        int var6 = 0;
        int var7 = this.x + 28 * var4;
        int var8 = this.y;
        byte var9 = 32;
        if (var2) {
            var6 += 32;
        }

        if (var4 == 5) {
            var7 = this.x + this.backgroundWidth - 28;
        } else if (var4 > 0) {
            var7 += var4;
        }

        if (var3) {
            var8 -= 28;
        } else {
            var6 += 64;
            var8 += this.backgroundHeight - 4;
        }

        GL11.glDisable(2896);
        this.drawTexture(var7, var8, var5, var6, 28, var9);
        this.zOffset = 100.0F;
        field_1346.zOffset = 100.0F;
        var7 += 6;
        var8 += 8 + (var3 ? 1 : -1);
        GL11.glEnable(2896);
        GL11.glEnable(32826);
        ItemStack var10 = par1CreativeTabs.getIconItemStack();
        field_1346.method_4336(this.textRenderer, this.field_1229.field_3813, var10, var7, var8);
        field_1346.method_1549(this.textRenderer, this.field_1229.field_3813, var10, var7, var8);
        GL11.glDisable(2896);
        field_1346.zOffset = 0.0F;
        this.zOffset = 0.0F;
    }

    @Inject(method = "buttonClicked", at = @At("RETURN"))
    private void fmlOnButtonClick(ButtonWidget par1GuiButton, CallbackInfo ci) {
        if (par1GuiButton.id == 101) {
            this.tabPage = Math.max(this.tabPage - 1, 0);
        } else if (par1GuiButton.id == 102) {
            this.tabPage = Math.min(this.tabPage + 1, this.maxPages);
        }
    }

    // How?
//    static SimpleInventory method_1149() {
//        return inventory;
//    }
}
