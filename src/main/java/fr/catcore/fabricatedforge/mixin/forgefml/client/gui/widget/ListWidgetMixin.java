package fr.catcore.fabricatedforge.mixin.forgefml.client.gui.widget;

import fr.catcore.fabricatedforge.mixininterface.IListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ListWidget.class)
public abstract class ListWidgetMixin implements IListWidget {

    @Shadow protected int lastMouseX;
    @Shadow protected int lastMouseY;

    @Shadow protected abstract void renderBackground();

    @Shadow protected abstract int getEntryCount();

    @Shadow protected abstract int getScrollbarPosition();

    @Shadow private float field_1253;
    @Shadow protected int yStart;
    @Shadow protected int yEnd;
    @Shadow private int field_1247;
    @Shadow private int headerHeight;
    @Shadow private float scrollAmount;
    @Shadow @Final protected int entryHeight;
    @Shadow private int field_1256;
    @Shadow private long time;

    @Shadow protected abstract void method_1057(int i, boolean bl);

    @Shadow protected abstract void clickedHeader(int mouseX, int mouseY);

    @Shadow private float field_1254;

    @Shadow public abstract int getMaxScroll();

    @Shadow protected abstract int getMaxPosition();

    @Shadow protected abstract void capYPosition();

    @Shadow private boolean renderHeader;

    @Shadow protected abstract void renderHeader(int x, int y, Tessellator tessellator);

    @Shadow private boolean field_1258;

    @Shadow protected abstract boolean isEntrySelected(int index);

    @Shadow protected abstract void method_1055(int i, int j, int k, int l, Tessellator tessellator);

    @Shadow private int height;
    @Shadow private int field_1250;
    @Shadow private int xEnd;

    @Shadow protected abstract void renderDecorations(int mouseX, int mouseY);

    @Shadow @Final private Minecraft field_1241;

    @Shadow protected abstract void renderHoleBackground(int top, int bottom, int topAlpha, int bottomAlpha);

    // Forge Field
    @Unique
    public String BACKGROUND_IMAGE = "/gui/background.png";

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void render(int par1, int par2, float par3) {
        this.lastMouseX = par1;
        this.lastMouseY = par2;
        this.renderBackground();
        int var4 = this.getEntryCount();
        int var5 = this.getScrollbarPosition();
        int var6 = var5 + 6;
        if (Mouse.isButtonDown(0)) {
            if (this.field_1253 == -1.0F) {
                boolean var7 = true;
                if (par2 >= this.yStart && par2 <= this.yEnd) {
                    int var8 = this.field_1247 / 2 - 110;
                    int var9 = this.field_1247 / 2 + 110;
                    int var10 = par2 - this.yStart - this.headerHeight + (int)this.scrollAmount - 4;
                    int var11 = var10 / this.entryHeight;
                    if (par1 >= var8 && par1 <= var9 && var11 >= 0 && var10 >= 0 && var11 < var4) {
                        boolean var12 = var11 == this.field_1256 && Minecraft.getTime() - this.time < 250L;
                        this.method_1057(var11, var12);
                        this.field_1256 = var11;
                        this.time = Minecraft.getTime();
                    } else if (par1 >= var8 && par1 <= var9 && var10 < 0) {
                        this.clickedHeader(par1 - var8, par2 - this.yStart + (int)this.scrollAmount - 4);
                        var7 = false;
                    }

                    if (par1 >= var5 && par1 <= var6) {
                        this.field_1254 = -1.0F;
                        int var19 = this.getMaxScroll();
                        if (var19 < 1) {
                            var19 = 1;
                        }

                        int var13 = (int)((float)((this.yEnd - this.yStart) * (this.yEnd - this.yStart)) / (float)this.getMaxPosition());
                        if (var13 < 32) {
                            var13 = 32;
                        }

                        if (var13 > this.yEnd - this.yStart - 8) {
                            var13 = this.yEnd - this.yStart - 8;
                        }

                        this.field_1254 /= (float)(this.yEnd - this.yStart - var13) / (float)var19;
                    } else {
                        this.field_1254 = 1.0F;
                    }

                    if (var7) {
                        this.field_1253 = (float)par2;
                    } else {
                        this.field_1253 = -2.0F;
                    }
                } else {
                    this.field_1253 = -2.0F;
                }
            } else if (this.field_1253 >= 0.0F) {
                this.scrollAmount -= ((float)par2 - this.field_1253) * this.field_1254;
                this.field_1253 = (float)par2;
            }
        } else {
            while(Mouse.next()) {
                int var16 = Mouse.getEventDWheel();
                if (var16 != 0) {
                    if (var16 > 0) {
                        var16 = -1;
                    } else if (var16 < 0) {
                        var16 = 1;
                    }

                    this.scrollAmount += (float)(var16 * this.entryHeight / 2);
                }
            }

            this.field_1253 = -1.0F;
        }

        this.capYPosition();
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        Tessellator var18 = Tessellator.INSTANCE;
        this.drawContainerBackground(var18);
        int var9 = this.field_1247 / 2 - 92 - 16;
        int var10 = this.yStart + 4 - (int)this.scrollAmount;
        if (this.renderHeader) {
            this.renderHeader(var9, var10, var18);
        }

        for(int var11 = 0; var11 < var4; ++var11) {
            int var19 = var10 + var11 * this.entryHeight + this.headerHeight;
            int var13 = this.entryHeight - 4;
            if (var19 <= this.yEnd && var19 + var13 >= this.yStart) {
                if (this.field_1258 && this.isEntrySelected(var11)) {
                    int var14 = this.field_1247 / 2 - 110;
                    int var15 = this.field_1247 / 2 + 110;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(3553);
                    var18.method_1405();
                    var18.method_1413(8421504);
                    var18.method_1399((double)var14, (double)(var19 + var13 + 2), 0.0, 0.0, 1.0);
                    var18.method_1399((double)var15, (double)(var19 + var13 + 2), 0.0, 1.0, 1.0);
                    var18.method_1399((double)var15, (double)(var19 - 2), 0.0, 1.0, 0.0);
                    var18.method_1399((double)var14, (double)(var19 - 2), 0.0, 0.0, 0.0);
                    var18.method_1413(0);
                    var18.method_1399((double)(var14 + 1), (double)(var19 + var13 + 1), 0.0, 0.0, 1.0);
                    var18.method_1399((double)(var15 - 1), (double)(var19 + var13 + 1), 0.0, 1.0, 1.0);
                    var18.method_1399((double)(var15 - 1), (double)(var19 - 1), 0.0, 1.0, 0.0);
                    var18.method_1399((double)(var14 + 1), (double)(var19 - 1), 0.0, 0.0, 0.0);
                    var18.method_1396();
                    GL11.glEnable(3553);
                }

                this.method_1055(var11, var9, var19, var13, var18);
            }
        }

        GL11.glDisable(2929);
        byte var20 = 4;
        this.renderHoleBackground(0, this.yStart, 255, 255);
        this.renderHoleBackground(this.yEnd, this.height, 255, 255);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        var18.method_1405();
        var18.method_1402(0, 0);
        var18.method_1399((double)this.field_1250, (double)(this.yStart + var20), 0.0, 0.0, 1.0);
        var18.method_1399((double)this.xEnd, (double)(this.yStart + var20), 0.0, 1.0, 1.0);
        var18.method_1402(0, 255);
        var18.method_1399((double)this.xEnd, (double)this.yStart, 0.0, 1.0, 0.0);
        var18.method_1399((double)this.field_1250, (double)this.yStart, 0.0, 0.0, 0.0);
        var18.method_1396();
        var18.method_1405();
        var18.method_1402(0, 255);
        var18.method_1399((double)this.field_1250, (double)this.yEnd, 0.0, 0.0, 1.0);
        var18.method_1399((double)this.xEnd, (double)this.yEnd, 0.0, 1.0, 1.0);
        var18.method_1402(0, 0);
        var18.method_1399((double)this.xEnd, (double)(this.yEnd - var20), 0.0, 1.0, 0.0);
        var18.method_1399((double)this.field_1250, (double)(this.yEnd - var20), 0.0, 0.0, 0.0);
        var18.method_1396();
        int var19 = this.getMaxScroll();
        if (var19 > 0) {
            int var13 = (this.yEnd - this.yStart) * (this.yEnd - this.yStart) / this.getMaxPosition();
            if (var13 < 32) {
                var13 = 32;
            }

            if (var13 > this.yEnd - this.yStart - 8) {
                var13 = this.yEnd - this.yStart - 8;
            }

            int var14 = (int)this.scrollAmount * (this.yEnd - this.yStart - var13) / var19 + this.yStart;
            if (var14 < this.yStart) {
                var14 = this.yStart;
            }

            var18.method_1405();
            var18.method_1402(0, 255);
            var18.method_1399((double)var5, (double)this.yEnd, 0.0, 0.0, 1.0);
            var18.method_1399((double)var6, (double)this.yEnd, 0.0, 1.0, 1.0);
            var18.method_1399((double)var6, (double)this.yStart, 0.0, 1.0, 0.0);
            var18.method_1399((double)var5, (double)this.yStart, 0.0, 0.0, 0.0);
            var18.method_1396();
            var18.method_1405();
            var18.method_1402(8421504, 255);
            var18.method_1399((double)var5, (double)(var14 + var13), 0.0, 0.0, 1.0);
            var18.method_1399((double)var6, (double)(var14 + var13), 0.0, 1.0, 1.0);
            var18.method_1399((double)var6, (double)var14, 0.0, 1.0, 0.0);
            var18.method_1399((double)var5, (double)var14, 0.0, 0.0, 0.0);
            var18.method_1396();
            var18.method_1405();
            var18.method_1402(12632256, 255);
            var18.method_1399((double)var5, (double)(var14 + var13 - 1), 0.0, 0.0, 1.0);
            var18.method_1399((double)(var6 - 1), (double)(var14 + var13 - 1), 0.0, 1.0, 1.0);
            var18.method_1399((double)(var6 - 1), (double)var14, 0.0, 1.0, 0.0);
            var18.method_1399((double)var5, (double)var14, 0.0, 0.0, 0.0);
            var18.method_1396();
        }

        this.renderDecorations(par1, par2);
        GL11.glEnable(3553);
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glDisable(3042);
    }

    @ModifyArg(method = "renderHoleBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/class_534;getTextureFromPath(Ljava/lang/String;)I"))
    private String setBackgroundImageInFunction(String path) {
        return this.BACKGROUND_IMAGE;
    }

    @Override
    public void drawContainerBackground(Tessellator tess) {
        GL11.glBindTexture(3553, this.field_1241.field_3813.getTextureFromPath(this.BACKGROUND_IMAGE));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float height = 32.0F;
        tess.method_1405();
        tess.method_1413(2105376);
        tess.method_1399(
                (double)this.field_1250,
                (double)this.yEnd,
                0.0,
                (double)((float)this.field_1250 / height),
                (double)((float)(this.yEnd + (int)this.scrollAmount) / height)
        );
        tess.method_1399(
                (double)this.xEnd, (double)this.yEnd, 0.0, (double)((float)this.xEnd / height), (double)((float)(this.yEnd + (int)this.scrollAmount) / height)
        );
        tess.method_1399(
                (double)this.xEnd, (double)this.yStart, 0.0, (double)((float)this.xEnd / height), (double)((float)(this.yStart + (int)this.scrollAmount) / height)
        );
        tess.method_1399(
                (double)this.field_1250,
                (double)this.yStart,
                0.0,
                (double)((float)this.field_1250 / height),
                (double)((float)(this.yStart + (int)this.scrollAmount) / height)
        );
        tess.method_1396();
    }
}
