package fr.catcore.fabricatedforge.mixin.forgefml.client.gui.screen;

import net.minecraft.advancement.Achievement;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.AchievementsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.AchievementPage;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Mixin(AchievementsScreen.class)
public class AchievementsScreenMixin extends Screen {

    @Shadow protected int field_1308;
    @Shadow protected double field_1312;
    @Shadow protected double field_1314;
    @Shadow protected double field_1313;
    @Shadow protected double field_1315;
    @Shadow @Final private static int field_1318;
    @Shadow @Final private static int field_1319;
    @Shadow @Final private static int field_1320;
    @Shadow @Final private static int field_1321;
    @Shadow protected int field_1309;
    @Shadow private StatHandler handler;

    // Forge Fields
    @Unique
    private int currentPage = -1;
    @Unique
    private OptionButtonWidget button;
    @Unique
    private LinkedList<Achievement> minecraftAchievements = new LinkedList<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fmlCtr(StatHandler par1, CallbackInfo ci) {
        this.minecraftAchievements.clear();

        for(Object achievement : AchievementsAndCriterions.ACHIEVEMENTS) {
            if (!AchievementPage.isAchievementInPages((Achievement)achievement)) {
                this.minecraftAchievements.add((Achievement)achievement);
            }
        }
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void fmlAddPageTitle(CallbackInfo ci) {
        this.buttons
                .add(
                        this.button = new OptionButtonWidget(
                                2, (this.width - this.field_1308) / 2 + 24, this.height / 2 + 74, 125, 20, AchievementPage.getTitle(this.currentPage)
                        )
                );
    }

    @Inject(method = "buttonClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;buttonClicked(Lnet/minecraft/client/gui/widget/ButtonWidget;)V"))
    private void fmlAddPageButton(ButtonWidget par1GuiButton, CallbackInfo ci) {
        if (par1GuiButton.id == 2) {
            ++this.currentPage;
            if (this.currentPage >= AchievementPage.getAchievementPages().size()) {
                this.currentPage = -1;
            }

            this.button.message = AchievementPage.getTitle(this.currentPage);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void method_1095(int par1, int par2, float par3) {
        int var4 = MathHelper.floor(this.field_1312 + (this.field_1314 - this.field_1312) * (double)par3);
        int var5 = MathHelper.floor(this.field_1313 + (this.field_1315 - this.field_1313) * (double)par3);
        if (var4 < field_1318) {
            var4 = field_1318;
        }

        if (var5 < field_1319) {
            var5 = field_1319;
        }

        if (var4 >= field_1320) {
            var4 = field_1320 - 1;
        }

        if (var5 >= field_1321) {
            var5 = field_1321 - 1;
        }

        int var6 = this.field_1229.field_3813.getTextureFromPath("/terrain.png");
        int var7 = this.field_1229.field_3813.getTextureFromPath("/achievement/bg.png");
        int var8 = (this.width - this.field_1308) / 2;
        int var9 = (this.height - this.field_1309) / 2;
        int var10 = var8 + 16;
        int var11 = var9 + 17;
        this.zOffset = 0.0F;
        GL11.glDepthFunc(518);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        GL11.glEnable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        this.field_1229.field_3813.method_1426(var6);
        int var12 = var4 + 288 >> 4;
        int var13 = var5 + 288 >> 4;
        int var14 = (var4 + 288) % 16;
        int var15 = (var5 + 288) % 16;
        Random var21 = new Random();

        for(int var22 = 0; var22 * 16 - var15 < 155; ++var22) {
            float var23 = 0.6F - (float)(var13 + var22) / 25.0F * 0.3F;
            GL11.glColor4f(var23, var23, var23, 1.0F);

            for(int var24 = 0; var24 * 16 - var14 < 224; ++var24) {
                var21.setSeed((long)(1234 + var12 + var24));
                var21.nextInt();
                int var25 = var21.nextInt(1 + var13 + var22) + (var13 + var22) / 2;
                int var26 = Block.SAND_BLOCK.field_439;
                if (var25 > 37 || var13 + var22 == 35) {
                    var26 = Block.BEDROCK.field_439;
                } else if (var25 == 22) {
                    if (var21.nextInt(2) == 0) {
                        var26 = Block.DIAMOND_ORE.field_439;
                    } else {
                        var26 = Block.REDSTONE_ORE.field_439;
                    }
                } else if (var25 == 10) {
                    var26 = Block.IRON_ORE.field_439;
                } else if (var25 == 8) {
                    var26 = Block.COAL_ORE.field_439;
                } else if (var25 > 4) {
                    var26 = Block.STONE_BLOCK.field_439;
                } else if (var25 > 0) {
                    var26 = Block.DIRT.field_439;
                }

                this.drawTexture(var10 + var24 * 16 - var14, var11 + var22 * 16 - var15, var26 % 16 << 4, var26 >> 4 << 4, 16, 16);
            }
        }

        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glDisable(3553);
        List<Achievement> achievementList = (List<Achievement>)(this.currentPage == -1
                ? this.minecraftAchievements
                : AchievementPage.getAchievementPage(this.currentPage).getAchievements());

        for(int var31 = 0; var31 < achievementList.size(); ++var31) {
            Achievement var33 = (Achievement)achievementList.get(var31);
            if (var33.parent != null && achievementList.contains(var33.parent)) {
                int var24 = var33.column * 24 - var4 + 11 + var10;
                int var25 = var33.row * 24 - var5 + 11 + var11;
                int var26 = var33.parent.column * 24 - var4 + 11 + var10;
                int var27 = var33.parent.row * 24 - var5 + 11 + var11;
                boolean var28 = this.handler.method_1728(var33);
                boolean var29 = this.handler.method_1735(var33);
                int var30 = Math.sin((double)(Minecraft.getTime() % 600L) / 600.0 * Math.PI * 2.0) > 0.6 ? 255 : 130;
                int var38 = -16777216;
                if (var28) {
                    var38 = -9408400;
                } else if (var29) {
                    var38 = 65280 + (var30 << 24);
                }

                this.drawHorizontalLine(var24, var26, var25, var38);
                this.drawVerticalLine(var26, var25, var27, var38);
            }
        }

        Achievement var32 = null;
        ItemRenderer var37 = new ItemRenderer();
        DiffuseLighting.enable();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);

        for(int var24 = 0; var24 < achievementList.size(); ++var24) {
            Achievement var35 = (Achievement)achievementList.get(var24);
            int var26 = var35.column * 24 - var4;
            int var27 = var35.row * 24 - var5;
            if (var26 >= -24 && var27 >= -24 && var26 <= 224 && var27 <= 155) {
                if (this.handler.method_1728(var35)) {
                    float var38 = 1.0F;
                    GL11.glColor4f(var38, var38, var38, 1.0F);
                } else if (this.handler.method_1735(var35)) {
                    float var38 = Math.sin((double)(Minecraft.getTime() % 600L) / 600.0 * Math.PI * 2.0) < 0.6 ? 0.6F : 0.8F;
                    GL11.glColor4f(var38, var38, var38, 1.0F);
                } else {
                    float var38 = 0.3F;
                    GL11.glColor4f(var38, var38, var38, 1.0F);
                }

                this.field_1229.field_3813.method_1426(var7);
                int var42 = var10 + var26;
                int var41 = var11 + var27;
                if (var35.isChallenge()) {
                    this.drawTexture(var42 - 2, var41 - 2, 26, 202, 26, 26);
                } else {
                    this.drawTexture(var42 - 2, var41 - 2, 0, 202, 26, 26);
                }

                if (!this.handler.method_1735(var35)) {
                    float var40 = 0.1F;
                    GL11.glColor4f(var40, var40, var40, 1.0F);
                    var37.field_2123 = false;
                }

                GL11.glEnable(2896);
                GL11.glEnable(2884);
                var37.method_4336(this.field_1229.textRenderer, this.field_1229.field_3813, var35.logo, var42 + 3, var41 + 3);
                GL11.glDisable(2896);
                if (!this.handler.method_1735(var35)) {
                    var37.field_2123 = true;
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (par1 >= var10
                        && par2 >= var11
                        && par1 < var10 + 224
                        && par2 < var11 + 155
                        && par1 >= var42
                        && par1 <= var42 + 22
                        && par2 >= var41
                        && par2 <= var41 + 22) {
                    var32 = var35;
                }
            }
        }

        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_1229.field_3813.method_1426(var7);
        this.drawTexture(var8, var9, 0, 0, this.field_1308, this.field_1309);
        GL11.glPopMatrix();
        this.zOffset = 0.0F;
        GL11.glDepthFunc(515);
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        super.render(par1, par2, par3);
        if (var32 != null) {
            String var34 = CommonI18n.translate(var32.getStringId());
            String var36 = var32.getDescription();
            int var26 = par1 + 12;
            int var27 = par2 - 4;
            if (this.handler.method_1735(var32)) {
                int var42 = Math.max(this.textRenderer.getStringWidth(var34), 120);
                int var41 = this.textRenderer.method_963(var36, var42);
                if (this.handler.method_1728(var32)) {
                    var41 += 12;
                }

                this.fillGradient(var26 - 3, var27 - 3, var26 + var42 + 3, var27 + var41 + 3 + 12, -1073741824, -1073741824);
                this.textRenderer.drawTrimmed(var36, var26, var27 + 12, var42, -6250336);
                if (this.handler.method_1728(var32)) {
                    this.textRenderer.method_956(CommonI18n.translate("achievement.taken"), var26, var27 + var41 + 4, -7302913);
                }
            } else {
                int var42 = Math.max(this.textRenderer.getStringWidth(var34), 120);
                String var39 = CommonI18n.translate("achievement.requires", CommonI18n.translate(var32.parent.getStringId()));
                int var30 = this.textRenderer.method_963(var39, var42);
                this.fillGradient(var26 - 3, var27 - 3, var26 + var42 + 3, var27 + var30 + 12 + 3, -1073741824, -1073741824);
                this.textRenderer.drawTrimmed(var39, var26, var27 + 12, var42, -9416624);
            }

            this.textRenderer
                    .method_956(
                            var34, var26, var27, this.handler.method_1735(var32) ? (var32.isChallenge() ? -128 : -1) : (var32.isChallenge() ? -8355776 : -8355712)
                    );
        }

        GL11.glEnable(2929);
        GL11.glEnable(2896);
        DiffuseLighting.disable();
    }
}
