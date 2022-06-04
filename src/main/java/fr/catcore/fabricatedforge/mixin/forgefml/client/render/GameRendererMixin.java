package fr.catcore.fabricatedforge.mixin.forgefml.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_321;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Clipper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow private Minecraft field_1860;

    @Shadow private float field_1831;

    @Shadow private float movementFovMultiplier;

    @Shadow private float lastMovementFovMultiplier;

    @Shadow public int field_1859;

    @Shadow private float field_1825;

    @Shadow private float field_1824;

    @Shadow private float field_1827;

    @Shadow private float field_1826;

    @Shadow private float lastThirdPersonDistance;

    @Shadow private float thirdPersonDistance;

    @Shadow private float field_1816;

    @Shadow private float field_1815;

    @Shadow private float field_1818;

    @Shadow private float field_1817;

    @Shadow private boolean thickFog;

    @Shadow private double zoom;

    @Shadow private boolean lightmapDirty;

    @Shadow public abstract void updateTargetedEntity(float tickDelta);

    @Shadow public static int anaglyphFilter;

    @Shadow protected abstract void updateFog(float tickDelta);

    @Shadow protected abstract void renderFog(int i, float tickDelta);

    @Shadow public abstract void method_1330(double d);

    @Shadow public abstract void method_1322(double d);

    @Shadow protected abstract void renderWeather(float tickDelta);

    @Shadow protected abstract void renderHand(float tickDelta, int anaglyphOffset);

    @Shadow protected abstract void updateLightmap(float tickDelta);

    @Shadow protected abstract void setupCamera(float tickDelta, int anaglyphFilter);

    @Shadow protected abstract void method_4300(WorldRenderer worldRenderer, float f);

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void updateMovementFovMultiplier() {
        if (this.field_1860.field_3806 instanceof ClientPlayerEntity) {
            ClientPlayerEntity var1 = (ClientPlayerEntity)this.field_1860.field_3806;
            this.field_1831 = var1.method_1305();
        } else {
            this.field_1831 = this.field_1860.playerEntity.method_1305();
        }

        this.lastMovementFovMultiplier = this.movementFovMultiplier;
        this.movementFovMultiplier += (this.field_1831 - this.movementFovMultiplier) * 0.5F;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private float getFov(float par1, boolean par2) {
        if (this.field_1859 > 0) {
            return 90.0F;
        } else {
            MobEntity var3 = this.field_1860.field_3806;
            float var4 = 70.0F;
            if (par2) {
                var4 += this.field_1860.options.fov * 40.0F;
                var4 *= this.lastMovementFovMultiplier + (this.movementFovMultiplier - this.lastMovementFovMultiplier) * par1;
            }

            if (var3.method_2600() <= 0) {
                float var5 = (float)var3.field_3300 + par1;
                var4 /= (1.0F - 500.0F / (var5 + 500.0F)) * 2.0F + 1.0F;
            }

            int var6 = class_321.method_805(this.field_1860.world, var3, par1);
            if (var6 != 0 && Block.BLOCKS[var6].material == Material.WATER) {
                var4 = var4 * 60.0F / 70.0F;
            }

            return var4 + this.field_1825 + (this.field_1824 - this.field_1825) * par1;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void transformCamera(float par1) {
        MobEntity var2 = this.field_1860.field_3806;
        float var3 = var2.heightOffset - 1.62F;
        double var4 = var2.prevX + (var2.x - var2.prevX) * (double)par1;
        double var6 = var2.prevY + (var2.y - var2.prevY) * (double)par1 - (double)var3;
        double var8 = var2.prevZ + (var2.z - var2.prevZ) * (double)par1;
        GL11.glRotatef(this.field_1827 + (this.field_1826 - this.field_1827) * par1, 0.0F, 0.0F, 1.0F);
        if (var2.method_2641()) {
            var3 = (float)((double)var3 + 1.0);
            GL11.glTranslatef(0.0F, 0.3F, 0.0F);
            if (!this.field_1860.options.field_955) {
                ForgeHooksClient.orientBedCamera(this.field_1860, var2);
                GL11.glRotatef(var2.prevYaw + (var2.yaw - var2.prevYaw) * par1 + 180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glRotatef(var2.prevPitch + (var2.pitch - var2.prevPitch) * par1, -1.0F, 0.0F, 0.0F);
            }
        } else if (this.field_1860.options.perspective > 0) {
            double var27 = (double)(this.lastThirdPersonDistance + (this.thirdPersonDistance - this.lastThirdPersonDistance) * par1);
            if (this.field_1860.options.field_955) {
                float var28 = this.field_1816 + (this.field_1815 - this.field_1816) * par1;
                float var13 = this.field_1818 + (this.field_1817 - this.field_1818) * par1;
                GL11.glTranslatef(0.0F, 0.0F, (float)(-var27));
                GL11.glRotatef(var13, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(var28, 0.0F, 1.0F, 0.0F);
            } else {
                float var28 = var2.yaw;
                float var13 = var2.pitch;
                if (this.field_1860.options.perspective == 2) {
                    var13 += 180.0F;
                }

                double var14 = (double)(-MathHelper.sin(var28 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI)) * var27;
                double var16 = (double)(MathHelper.cos(var28 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI)) * var27;
                double var18 = (double)(-MathHelper.sin(var13 / 180.0F * (float) Math.PI)) * var27;

                for(int var20 = 0; var20 < 8; ++var20) {
                    float var21 = (float)((var20 & 1) * 2 - 1);
                    float var22 = (float)((var20 >> 1 & 1) * 2 - 1);
                    float var23 = (float)((var20 >> 2 & 1) * 2 - 1);
                    var21 *= 0.1F;
                    var22 *= 0.1F;
                    var23 *= 0.1F;
                    BlockHitResult var24 = this.field_1860
                            .world
                            .rayTrace(
                                    this.field_1860.world.method_4696().getOrCreate(var4 + (double)var21, var6 + (double)var22, var8 + (double)var23),
                                    this.field_1860
                                            .world
                                            .method_4696()
                                            .getOrCreate(var4 - var14 + (double)var21 + (double)var23, var6 - var18 + (double)var22, var8 - var16 + (double)var23)
                            );
                    if (var24 != null) {
                        double var25 = var24.pos.distanceTo(this.field_1860.world.method_4696().getOrCreate(var4, var6, var8));
                        if (var25 < var27) {
                            var27 = var25;
                        }
                    }
                }

                if (this.field_1860.options.perspective == 2) {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(var2.pitch - var13, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(var2.yaw - var28, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.0F, (float)(-var27));
                GL11.glRotatef(var28 - var2.yaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(var13 - var2.pitch, 1.0F, 0.0F, 0.0F);
            }
        } else {
            GL11.glTranslatef(0.0F, 0.0F, -0.1F);
        }

        if (!this.field_1860.options.field_955) {
            GL11.glRotatef(var2.prevPitch + (var2.pitch - var2.prevPitch) * par1, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(var2.prevYaw + (var2.yaw - var2.prevYaw) * par1 + 180.0F, 0.0F, 1.0F, 0.0F);
        }

        GL11.glTranslatef(0.0F, var3, 0.0F);
        var4 = var2.prevX + (var2.x - var2.prevX) * (double)par1;
        var6 = var2.prevY + (var2.y - var2.prevY) * (double)par1 - (double)var3;
        var8 = var2.prevZ + (var2.z - var2.prevZ) * (double)par1;
        this.thickFog = this.field_1860.worldRenderer.method_1364(var4, var6, var8, par1);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void renderWorld(float par1, long par2) {
        this.field_1860.profiler.push("lightTex");
        if (this.lightmapDirty) {
            this.updateLightmap(par1);
        }

        GL11.glEnable(2884);
        GL11.glEnable(2929);
        if (this.field_1860.field_3806 == null) {
            this.field_1860.field_3806 = this.field_1860.playerEntity;
        }

        this.field_1860.profiler.swap("pick");
        this.updateTargetedEntity(par1);
        MobEntity var4 = this.field_1860.field_3806;
        WorldRenderer var5 = this.field_1860.worldRenderer;
        ParticleManager var6 = this.field_1860.particleManager;
        double var7 = var4.prevTickX + (var4.x - var4.prevTickX) * (double)par1;
        double var9 = var4.prevTickY + (var4.y - var4.prevTickY) * (double)par1;
        double var11 = var4.prevTickZ + (var4.z - var4.prevTickZ) * (double)par1;
        this.field_1860.profiler.swap("center");

        for(int var13 = 0; var13 < 2; ++var13) {
            if (this.field_1860.options.anaglyph3d) {
                anaglyphFilter = var13;
                if (anaglyphFilter == 0) {
                    GL11.glColorMask(false, true, true, false);
                } else {
                    GL11.glColorMask(true, false, false, false);
                }
            }

            this.field_1860.profiler.swap("clear");
            GL11.glViewport(0, 0, this.field_1860.width, this.field_1860.height);
            this.updateFog(par1);
            GL11.glClear(16640);
            GL11.glEnable(2884);
            this.field_1860.profiler.swap("camera");
            this.setupCamera(par1, var13);
            class_321.method_804(this.field_1860.playerEntity, this.field_1860.options.perspective == 2);
            this.field_1860.profiler.swap("frustrum");
            Clipper.getInstance();
            if (this.field_1860.options.renderDistance < 2) {
                this.renderFog(-1, par1);
                this.field_1860.profiler.swap("sky");
                var5.method_1365(par1);
            }

            GL11.glEnable(2912);
            this.renderFog(1, par1);
            if (this.field_1860.options.ambientOcculsion) {
                GL11.glShadeModel(7425);
            }

            this.field_1860.profiler.swap("culling");
            CullingCameraView var14 = new CullingCameraView();
            var14.setPos(var7, var9, var11);
            this.field_1860.worldRenderer.method_1373(var14, par1);
            if (var13 == 0) {
                this.field_1860.profiler.swap("updatechunks");

                while(!this.field_1860.worldRenderer.method_1375(var4, false) && par2 != 0L) {
                    long var15 = par2 - System.nanoTime();
                    if (var15 < 0L || var15 > 1000000000L) {
                        break;
                    }
                }
            }

            if (var4.y < 128.0) {
                this.method_4300(var5, par1);
            }

            this.renderFog(0, par1);
            GL11.glEnable(2912);
            GL11.glBindTexture(3553, this.field_1860.field_3813.getTextureFromPath("/terrain.png"));
            DiffuseLighting.disable();
            this.field_1860.profiler.swap("terrain");
            var5.method_1374(var4, 0, (double)par1);
            GL11.glShadeModel(7424);
            if (this.field_1859 == 0) {
                DiffuseLighting.enableNormally();
                this.field_1860.profiler.swap("entities");
                var5.method_1370(var4.method_2663(par1), var14, par1);
                this.method_1330((double)par1);
                this.field_1860.profiler.swap("litParticles");
                var6.method_1299(var4, par1);
                DiffuseLighting.disable();
                this.renderFog(0, par1);
                this.field_1860.profiler.swap("particles");
                var6.method_1296(var4, par1);
                this.method_1322((double)par1);
                if (this.field_1860.result != null && var4.isSubmergedIn(Material.WATER) && var4 instanceof PlayerEntity && !this.field_1860.options.hudHidden) {
                    PlayerEntity var17 = (PlayerEntity)var4;
                    GL11.glDisable(3008);
                    this.field_1860.profiler.swap("outline");
                    if (!ForgeHooksClient.onDrawBlockHighlight(var5, var17, this.field_1860.result, 0, var17.inventory.getMainHandStack(), par1)) {
                        var5.method_1376(var17, this.field_1860.result, 0, var17.inventory.getMainHandStack(), par1);
                        var5.method_1380(var17, this.field_1860.result, 0, var17.inventory.getMainHandStack(), par1);
                    }

                    GL11.glEnable(3008);
                }
            }

            GL11.glDisable(3042);
            GL11.glEnable(2884);
            GL11.glBlendFunc(770, 771);
            GL11.glDepthMask(true);
            this.renderFog(0, par1);
            GL11.glEnable(3042);
            GL11.glDisable(2884);
            GL11.glBindTexture(3553, this.field_1860.field_3813.getTextureFromPath("/terrain.png"));
            if (this.field_1860.options.fancyGraphics) {
                this.field_1860.profiler.swap("water");
                if (this.field_1860.options.ambientOcculsion) {
                    GL11.glShadeModel(7425);
                }

                GL11.glColorMask(false, false, false, false);
                int var18 = var5.method_1374(var4, 1, (double)par1);
                if (this.field_1860.options.anaglyph3d) {
                    if (anaglyphFilter == 0) {
                        GL11.glColorMask(false, true, true, true);
                    } else {
                        GL11.glColorMask(true, false, false, true);
                    }
                } else {
                    GL11.glColorMask(true, true, true, true);
                }

                if (var18 > 0) {
                    var5.method_1366(1, (double)par1);
                }

                GL11.glShadeModel(7424);
            } else {
                this.field_1860.profiler.swap("water");
                var5.method_1374(var4, 1, (double)par1);
            }

            GL11.glDepthMask(true);
            GL11.glEnable(2884);
            GL11.glDisable(3042);
            if (this.zoom == 1.0
                    && var4 instanceof PlayerEntity
                    && !this.field_1860.options.hudHidden
                    && this.field_1860.result != null
                    && !var4.isSubmergedIn(Material.WATER)) {
                PlayerEntity var17 = (PlayerEntity)var4;
                GL11.glDisable(3008);
                this.field_1860.profiler.swap("outline");
                if (!ForgeHooksClient.onDrawBlockHighlight(var5, var17, this.field_1860.result, 0, var17.inventory.getMainHandStack(), par1)) {
                    var5.method_1376(var17, this.field_1860.result, 0, var17.inventory.getMainHandStack(), par1);
                    var5.method_1380(var17, this.field_1860.result, 0, var17.inventory.getMainHandStack(), par1);
                }

                GL11.glEnable(3008);
            }

            this.field_1860.profiler.swap("destroyProgress");
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 1);
            var5.drawBlockDamageTexture(Tessellator.INSTANCE, var4, par1);
            GL11.glDisable(3042);
            this.field_1860.profiler.swap("weather");
            this.renderWeather(par1);
            GL11.glDisable(2912);
            if (var4.y >= 128.0) {
                this.method_4300(var5, par1);
            }

            this.field_1860.profiler.swap("FRenderLast");
            ForgeHooksClient.dispatchRenderLast(var5, par1);
            this.field_1860.profiler.swap("hand");
            if (this.zoom == 1.0) {
                GL11.glClear(256);
                this.renderHand(par1, var13);
            }

            if (!this.field_1860.options.anaglyph3d) {
                this.field_1860.profiler.pop();
                return;
            }
        }

        GL11.glColorMask(true, true, true, false);
        this.field_1860.profiler.pop();
    }
}
