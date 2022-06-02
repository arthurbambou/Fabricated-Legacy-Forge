package cpw.mods.fml.client;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiScrollingList {
    private final Minecraft client;
    protected final int listWidth;
    protected final int listHeight;
    protected final int top;
    protected final int bottom;
    private final int right;
    protected final int left;
    protected final int slotHeight;
    private int scrollUpActionId;
    private int scrollDownActionId;
    protected int mouseX;
    protected int mouseY;
    private float initialMouseClickY = -2.0F;
    private float scrollFactor;
    private float scrollDistance;
    private int selectedIndex = -1;
    private long lastClickTime = 0L;
    private boolean field_25123_p = true;
    private boolean field_27262_q;
    private int field_27261_r;

    public GuiScrollingList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight) {
        this.client = client;
        this.listWidth = width;
        this.listHeight = height;
        this.top = top;
        this.bottom = bottom;
        this.slotHeight = entryHeight;
        this.left = left;
        this.right = width + this.left;
    }

    public void func_27258_a(boolean p_27258_1_) {
        this.field_25123_p = p_27258_1_;
    }

    protected void func_27259_a(boolean p_27259_1_, int p_27259_2_) {
        this.field_27262_q = p_27259_1_;
        this.field_27261_r = p_27259_2_;
        if (!p_27259_1_) {
            this.field_27261_r = 0;
        }

    }

    protected abstract int getSize();

    protected abstract void elementClicked(int i, boolean bl);

    protected abstract boolean isSelected(int i);

    protected int getContentHeight() {
        return this.getSize() * this.slotHeight + this.field_27261_r;
    }

    protected abstract void drawBackground();

    protected abstract void drawSlot(int i, int j, int k, int l, Tessellator arg);

    protected void func_27260_a(int p_27260_1_, int p_27260_2_, Tessellator p_27260_3_) {
    }

    protected void func_27255_a(int p_27255_1_, int p_27255_2_) {
    }

    protected void func_27257_b(int p_27257_1_, int p_27257_2_) {
    }

    public int func_27256_c(int p_27256_1_, int p_27256_2_) {
        int var3 = this.left + 1;
        int var4 = this.left + this.listWidth - 7;
        int var5 = p_27256_2_ - this.top - this.field_27261_r + (int)this.scrollDistance - 4;
        int var6 = var5 / this.slotHeight;
        return p_27256_1_ >= var3 && p_27256_1_ <= var4 && var6 >= 0 && var5 >= 0 && var6 < this.getSize() ? var6 : -1;
    }

    public void registerScrollButtons(List p_22240_1_, int p_22240_2_, int p_22240_3_) {
        this.scrollUpActionId = p_22240_2_;
        this.scrollDownActionId = p_22240_3_;
    }

    private void applyScrollLimits() {
        int var1 = this.getContentHeight() - (this.bottom - this.top - 4);
        if (var1 < 0) {
            var1 /= 2;
        }

        if (this.scrollDistance < 0.0F) {
            this.scrollDistance = 0.0F;
        }

        if (this.scrollDistance > (float)var1) {
            this.scrollDistance = (float)var1;
        }

    }

    public void actionPerformed(ButtonWidget button) {
        if (button.active) {
            if (button.id == this.scrollUpActionId) {
                this.scrollDistance -= (float)(this.slotHeight * 2 / 3);
                this.initialMouseClickY = -2.0F;
                this.applyScrollLimits();
            } else if (button.id == this.scrollDownActionId) {
                this.scrollDistance += (float)(this.slotHeight * 2 / 3);
                this.initialMouseClickY = -2.0F;
                this.applyScrollLimits();
            }
        }

    }

    public void drawScreen(int mouseX, int mouseY, float p_22243_3_) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.drawBackground();
        int listLength = this.getSize();
        int scrollBarXStart = this.left + this.listWidth - 6;
        int scrollBarXEnd = scrollBarXStart + 6;
        int boxLeft = this.left;
        int boxRight = scrollBarXStart - 1;
        if (Mouse.isButtonDown(0)) {
            if (this.initialMouseClickY == -1.0F) {
                boolean var7 = true;
                if (mouseY >= this.top && mouseY <= this.bottom) {
                    int var10 = mouseY - this.top - this.field_27261_r + (int)this.scrollDistance - 4;
                    int var11 = var10 / this.slotHeight;
                    if (mouseX >= boxLeft && mouseX <= boxRight && var11 >= 0 && var10 >= 0 && var11 < listLength) {
                        boolean var12 = var11 == this.selectedIndex && System.currentTimeMillis() - this.lastClickTime < 250L;
                        this.elementClicked(var11, var12);
                        this.selectedIndex = var11;
                        this.lastClickTime = System.currentTimeMillis();
                    } else if (mouseX >= boxLeft && mouseX <= boxRight && var10 < 0) {
                        this.func_27255_a(mouseX - boxLeft, mouseY - this.top + (int)this.scrollDistance - 4);
                        var7 = false;
                    }

                    if (mouseX >= scrollBarXStart && mouseX <= scrollBarXEnd) {
                        this.scrollFactor = -1.0F;
                        int var19 = this.getContentHeight() - (this.bottom - this.top - 4);
                        if (var19 < 1) {
                            var19 = 1;
                        }

                        int var13 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
                        if (var13 < 32) {
                            var13 = 32;
                        }

                        if (var13 > this.bottom - this.top - 8) {
                            var13 = this.bottom - this.top - 8;
                        }

                        this.scrollFactor /= (float)(this.bottom - this.top - var13) / (float)var19;
                    } else {
                        this.scrollFactor = 1.0F;
                    }

                    if (var7) {
                        this.initialMouseClickY = (float)mouseY;
                    } else {
                        this.initialMouseClickY = -2.0F;
                    }
                } else {
                    this.initialMouseClickY = -2.0F;
                }
            } else if (this.initialMouseClickY >= 0.0F) {
                this.scrollDistance -= ((float)mouseY - this.initialMouseClickY) * this.scrollFactor;
                this.initialMouseClickY = (float)mouseY;
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

                    this.scrollDistance += (float)(var16 * this.slotHeight / 2);
                }
            }

            this.initialMouseClickY = -1.0F;
        }

        this.applyScrollLimits();
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        Tessellator var18 = Tessellator.INSTANCE;
        GL11.glBindTexture(3553, this.client.field_3813.getTextureFromPath("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var17 = 32.0F;
        var18.method_1405();
        var18.method_1413(2105376);
        var18.method_1399(
                (double)this.left, (double)this.bottom, 0.0, (double)((float)this.left / var17), (double)((float)(this.bottom + (int)this.scrollDistance) / var17)
        );
        var18.method_1399(
                (double)this.right,
                (double)this.bottom,
                0.0,
                (double)((float)this.right / var17),
                (double)((float)(this.bottom + (int)this.scrollDistance) / var17)
        );
        var18.method_1399(
                (double)this.right, (double)this.top, 0.0, (double)((float)this.right / var17), (double)((float)(this.top + (int)this.scrollDistance) / var17)
        );
        var18.method_1399(
                (double)this.left, (double)this.top, 0.0, (double)((float)this.left / var17), (double)((float)(this.top + (int)this.scrollDistance) / var17)
        );
        var18.method_1396();
        int var10 = this.top + 4 - (int)this.scrollDistance;
        if (this.field_27262_q) {
            this.func_27260_a(boxRight, var10, var18);
        }

        for(int var11 = 0; var11 < listLength; ++var11) {
            int var19 = var10 + var11 * this.slotHeight + this.field_27261_r;
            int var13 = this.slotHeight - 4;
            if (var19 <= this.bottom && var19 + var13 >= this.top) {
                if (this.field_25123_p && this.isSelected(var11)) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(3553);
                    var18.method_1405();
                    var18.method_1413(8421504);
                    var18.method_1399((double)boxLeft, (double)(var19 + var13 + 2), 0.0, 0.0, 1.0);
                    var18.method_1399((double)boxRight, (double)(var19 + var13 + 2), 0.0, 1.0, 1.0);
                    var18.method_1399((double)boxRight, (double)(var19 - 2), 0.0, 1.0, 0.0);
                    var18.method_1399((double)boxLeft, (double)(var19 - 2), 0.0, 0.0, 0.0);
                    var18.method_1413(0);
                    var18.method_1399((double)(boxLeft + 1), (double)(var19 + var13 + 1), 0.0, 0.0, 1.0);
                    var18.method_1399((double)(boxRight - 1), (double)(var19 + var13 + 1), 0.0, 1.0, 1.0);
                    var18.method_1399((double)(boxRight - 1), (double)(var19 - 1), 0.0, 1.0, 0.0);
                    var18.method_1399((double)(boxLeft + 1), (double)(var19 - 1), 0.0, 0.0, 0.0);
                    var18.method_1396();
                    GL11.glEnable(3553);
                }

                this.drawSlot(var11, boxRight, var19, var13, var18);
            }
        }

        GL11.glDisable(2929);
        byte var20 = 4;
        this.overlayBackground(0, this.top, 255, 255);
        this.overlayBackground(this.bottom, this.listHeight, 255, 255);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        var18.method_1405();
        var18.method_1402(0, 0);
        var18.method_1399((double)this.left, (double)(this.top + var20), 0.0, 0.0, 1.0);
        var18.method_1399((double)this.right, (double)(this.top + var20), 0.0, 1.0, 1.0);
        var18.method_1402(0, 255);
        var18.method_1399((double)this.right, (double)this.top, 0.0, 1.0, 0.0);
        var18.method_1399((double)this.left, (double)this.top, 0.0, 0.0, 0.0);
        var18.method_1396();
        var18.method_1405();
        var18.method_1402(0, 255);
        var18.method_1399((double)this.left, (double)this.bottom, 0.0, 0.0, 1.0);
        var18.method_1399((double)this.right, (double)this.bottom, 0.0, 1.0, 1.0);
        var18.method_1402(0, 0);
        var18.method_1399((double)this.right, (double)(this.bottom - var20), 0.0, 1.0, 0.0);
        var18.method_1399((double)this.left, (double)(this.bottom - var20), 0.0, 0.0, 0.0);
        var18.method_1396();
        int var19 = this.getContentHeight() - (this.bottom - this.top - 4);
        if (var19 > 0) {
            int var13 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            if (var13 < 32) {
                var13 = 32;
            }

            if (var13 > this.bottom - this.top - 8) {
                var13 = this.bottom - this.top - 8;
            }

            int var14 = (int)this.scrollDistance * (this.bottom - this.top - var13) / var19 + this.top;
            if (var14 < this.top) {
                var14 = this.top;
            }

            var18.method_1405();
            var18.method_1402(0, 255);
            var18.method_1399((double)scrollBarXStart, (double)this.bottom, 0.0, 0.0, 1.0);
            var18.method_1399((double)scrollBarXEnd, (double)this.bottom, 0.0, 1.0, 1.0);
            var18.method_1399((double)scrollBarXEnd, (double)this.top, 0.0, 1.0, 0.0);
            var18.method_1399((double)scrollBarXStart, (double)this.top, 0.0, 0.0, 0.0);
            var18.method_1396();
            var18.method_1405();
            var18.method_1402(8421504, 255);
            var18.method_1399((double)scrollBarXStart, (double)(var14 + var13), 0.0, 0.0, 1.0);
            var18.method_1399((double)scrollBarXEnd, (double)(var14 + var13), 0.0, 1.0, 1.0);
            var18.method_1399((double)scrollBarXEnd, (double)var14, 0.0, 1.0, 0.0);
            var18.method_1399((double)scrollBarXStart, (double)var14, 0.0, 0.0, 0.0);
            var18.method_1396();
            var18.method_1405();
            var18.method_1402(12632256, 255);
            var18.method_1399((double)scrollBarXStart, (double)(var14 + var13 - 1), 0.0, 0.0, 1.0);
            var18.method_1399((double)(scrollBarXEnd - 1), (double)(var14 + var13 - 1), 0.0, 1.0, 1.0);
            var18.method_1399((double)(scrollBarXEnd - 1), (double)var14, 0.0, 1.0, 0.0);
            var18.method_1399((double)scrollBarXStart, (double)var14, 0.0, 0.0, 0.0);
            var18.method_1396();
        }

        this.func_27257_b(mouseX, mouseY);
        GL11.glEnable(3553);
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glDisable(3042);
    }

    private void overlayBackground(int p_22239_1_, int p_22239_2_, int p_22239_3_, int p_22239_4_) {
        Tessellator var5 = Tessellator.INSTANCE;
        GL11.glBindTexture(3553, this.client.field_3813.getTextureFromPath("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var6 = 32.0F;
        var5.method_1405();
        var5.method_1402(4210752, p_22239_4_);
        var5.method_1399(0.0, (double)p_22239_2_, 0.0, 0.0, (double)((float)p_22239_2_ / var6));
        var5.method_1399(
                (double)this.listWidth + 30.0, (double)p_22239_2_, 0.0, (double)((float)(this.listWidth + 30) / var6), (double)((float)p_22239_2_ / var6)
        );
        var5.method_1402(4210752, p_22239_3_);
        var5.method_1399(
                (double)this.listWidth + 30.0, (double)p_22239_1_, 0.0, (double)((float)(this.listWidth + 30) / var6), (double)((float)p_22239_1_ / var6)
        );
        var5.method_1399(0.0, (double)p_22239_1_, 0.0, 0.0, (double)((float)p_22239_1_ / var6));
        var5.method_1396();
    }
}
