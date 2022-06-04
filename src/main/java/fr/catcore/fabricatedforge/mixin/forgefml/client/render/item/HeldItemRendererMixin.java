package fr.catcore.fabricatedforge.mixin.forgefml.client.render.item;

import com.mojang.blaze3d.platform.GLX;
import cpw.mods.fml.client.TextureFXManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_535;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.class_481;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow private Minecraft field_1876;

    @Shadow private class_535 field_1880;

    @Shadow private float lastEquipProgress;

    @Shadow private float equipProgress;

    @Shadow private ItemStack mainHand;

    @Final
    @Shadow
    public MapRenderer field_1881;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1357(MobEntity par1EntityLiving, ItemStack par2ItemStack, int par3) {
        GL11.glPushMatrix();
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(par2ItemStack, IItemRenderer.ItemRenderType.EQUIPPED);
        if (customRenderer != null) {
            GL11.glBindTexture(3553, this.field_1876.field_3813.getTextureFromPath(par2ItemStack.getItem().getTextureFile()));
            ForgeHooksClient.renderEquippedItem(customRenderer, this.field_1880, par1EntityLiving, par2ItemStack);
        } else if (par2ItemStack.getItem() instanceof BlockItem && class_535.method_1455(Block.BLOCKS[par2ItemStack.id].getBlockType())) {
            GL11.glBindTexture(3553, this.field_1876.field_3813.getTextureFromPath(par2ItemStack.getItem().getTextureFile()));
            this.field_1880.method_1447(Block.BLOCKS[par2ItemStack.id], par2ItemStack.getMeta(), 1.0F);
        } else {
            GL11.glBindTexture(3553, this.field_1876.field_3813.getTextureFromPath(par2ItemStack.getItem().getTextureFile()));
            Tessellator var5 = Tessellator.INSTANCE;
            int var6 = par1EntityLiving.method_2630(par2ItemStack, par3);
            float var7 = ((float)(var6 % 16 * 16) + 0.0F) / 256.0F;
            float var8 = ((float)(var6 % 16 * 16) + 15.99F) / 256.0F;
            float var9 = ((float)(var6 / 16 * 16) + 0.0F) / 256.0F;
            float var10 = ((float)(var6 / 16 * 16) + 15.99F) / 256.0F;
            float var11 = 0.0F;
            float var12 = 0.3F;
            GL11.glEnable(32826);
            GL11.glTranslatef(-var11, -var12, 0.0F);
            float var13 = 1.5F;
            GL11.glScalef(var13, var13, var13);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            this.method_4306(var5, var8, var9, var7, var10);
            if (par2ItemStack != null && par2ItemStack.hasEnchantmentGlint() && par3 == 0) {
                GL11.glDepthFunc(514);
                GL11.glDisable(2896);
                this.field_1876.field_3813.method_1426(this.field_1876.field_3813.getTextureFromPath("%blur%/misc/glint.png"));
                GL11.glEnable(3042);
                GL11.glBlendFunc(768, 1);
                float var14 = 0.76F;
                GL11.glColor4f(0.5F * var14, 0.25F * var14, 0.8F * var14, 1.0F);
                GL11.glMatrixMode(5890);
                GL11.glPushMatrix();
                float var15 = 0.125F;
                GL11.glScalef(var15, var15, var15);
                float var16 = (float)(Minecraft.getTime() % 3000L) / 3000.0F * 8.0F;
                GL11.glTranslatef(var16, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                this.method_4306(var5, 0.0F, 0.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(var15, var15, var15);
                var16 = (float)(Minecraft.getTime() % 4873L) / 4873.0F * 8.0F;
                GL11.glTranslatef(-var16, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                this.method_4306(var5, 0.0F, 0.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(5888);
                GL11.glDisable(3042);
                GL11.glEnable(2896);
                GL11.glDepthFunc(515);
            }

            GL11.glDisable(32826);
        }

        GL11.glPopMatrix();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void method_4306(Tessellator par1Tessellator, float par2, float par3, float par4, float par5) {
        float var6 = 1.0F;
        float var7 = 0.0625F;
        par1Tessellator.method_1405();
        par1Tessellator.method_1407(0.0F, 0.0F, 1.0F);
        par1Tessellator.method_1399(0.0, 0.0, 0.0, (double)par2, (double)par5);
        par1Tessellator.method_1399((double)var6, 0.0, 0.0, (double)par4, (double)par5);
        par1Tessellator.method_1399((double)var6, 1.0, 0.0, (double)par4, (double)par3);
        par1Tessellator.method_1399(0.0, 1.0, 0.0, (double)par2, (double)par3);
        par1Tessellator.method_1396();
        par1Tessellator.method_1405();
        par1Tessellator.method_1407(0.0F, 0.0F, -1.0F);
        par1Tessellator.method_1399(0.0, 1.0, (double)(0.0F - var7), (double)par2, (double)par3);
        par1Tessellator.method_1399((double)var6, 1.0, (double)(0.0F - var7), (double)par4, (double)par3);
        par1Tessellator.method_1399((double)var6, 0.0, (double)(0.0F - var7), (double)par4, (double)par5);
        par1Tessellator.method_1399(0.0, 0.0, (double)(0.0F - var7), (double)par2, (double)par5);
        par1Tessellator.method_1396();
        par1Tessellator.method_1405();
        par1Tessellator.method_1407(-1.0F, 0.0F, 0.0F);
        int tileSize = TextureFXManager.instance().getTextureDimensions(GL11.glGetInteger(32873)).width / 16;
        float tx = 1.0F / (float)(32 * tileSize);
        float tz = 1.0F / (float)tileSize;

        for(int var8 = 0; var8 < tileSize; ++var8) {
            float var9 = (float)var8 / (float)tileSize;
            float var10 = par2 + (par4 - par2) * var9 - tx;
            float var11 = var6 * var9;
            par1Tessellator.method_1399((double)var11, 0.0, (double)(0.0F - var7), (double)var10, (double)par5);
            par1Tessellator.method_1399((double)var11, 0.0, 0.0, (double)var10, (double)par5);
            par1Tessellator.method_1399((double)var11, 1.0, 0.0, (double)var10, (double)par3);
            par1Tessellator.method_1399((double)var11, 1.0, (double)(0.0F - var7), (double)var10, (double)par3);
        }

        par1Tessellator.method_1396();
        par1Tessellator.method_1405();
        par1Tessellator.method_1407(1.0F, 0.0F, 0.0F);

        for(int var15 = 0; var15 < tileSize; ++var15) {
            float var9 = (float)var15 / (float)tileSize;
            float var10 = par2 + (par4 - par2) * var9 - tx;
            float var11 = var6 * var9 + tz;
            par1Tessellator.method_1399((double)var11, 1.0, (double)(0.0F - var7), (double)var10, (double)par3);
            par1Tessellator.method_1399((double)var11, 1.0, 0.0, (double)var10, (double)par3);
            par1Tessellator.method_1399((double)var11, 0.0, 0.0, (double)var10, (double)par5);
            par1Tessellator.method_1399((double)var11, 0.0, (double)(0.0F - var7), (double)var10, (double)par5);
        }

        par1Tessellator.method_1396();
        par1Tessellator.method_1405();
        par1Tessellator.method_1407(0.0F, 1.0F, 0.0F);

        for(int var16 = 0; var16 < tileSize; ++var16) {
            float var9 = (float)var16 / (float)tileSize;
            float var10 = par5 + (par3 - par5) * var9 - tx;
            float var11 = var6 * var9 + tz;
            par1Tessellator.method_1399(0.0, (double)var11, 0.0, (double)par2, (double)var10);
            par1Tessellator.method_1399((double)var6, (double)var11, 0.0, (double)par4, (double)var10);
            par1Tessellator.method_1399((double)var6, (double)var11, (double)(0.0F - var7), (double)par4, (double)var10);
            par1Tessellator.method_1399(0.0, (double)var11, (double)(0.0F - var7), (double)par2, (double)var10);
        }

        par1Tessellator.method_1396();
        par1Tessellator.method_1405();
        par1Tessellator.method_1407(0.0F, -1.0F, 0.0F);

        for(int var17 = 0; var17 < tileSize; ++var17) {
            float var9 = (float)var17 / (float)tileSize;
            float var10 = par5 + (par3 - par5) * var9 - tx;
            float var11 = var6 * var9;
            par1Tessellator.method_1399((double)var6, (double)var11, 0.0, (double)par4, (double)var10);
            par1Tessellator.method_1399(0.0, (double)var11, 0.0, (double)par2, (double)var10);
            par1Tessellator.method_1399(0.0, (double)var11, (double)(0.0F - var7), (double)par2, (double)var10);
            par1Tessellator.method_1399((double)var6, (double)var11, (double)(0.0F - var7), (double)par4, (double)var10);
        }

        par1Tessellator.method_1396();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void renderArmHoldingItem(float par1) {
        float var2 = this.lastEquipProgress + (this.equipProgress - this.lastEquipProgress) * par1;
        class_481 var3 = this.field_1876.playerEntity;
        float var4 = var3.prevPitch + (var3.pitch - var3.prevPitch) * par1;
        GL11.glPushMatrix();
        GL11.glRotatef(var4, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(var3.prevYaw + (var3.yaw - var3.prevYaw) * par1, 0.0F, 1.0F, 0.0F);
        DiffuseLighting.enableNormally();
        GL11.glPopMatrix();
        if (var3 instanceof ClientPlayerEntity) {
            float var6 = var3.lastRenderPitch + (var3.renderPitch - var3.lastRenderPitch) * par1;
            float var7 = var3.lastRenderYaw + (var3.renderYaw - var3.lastRenderYaw) * par1;
            GL11.glRotatef((var3.pitch - var6) * 0.1F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef((var3.yaw - var7) * 0.1F, 0.0F, 1.0F, 0.0F);
        }

        ItemStack var17 = this.mainHand;
        float var6 = this.field_1876.world.method_3780(MathHelper.floor(var3.x), MathHelper.floor(var3.y), MathHelper.floor(var3.z));
        var6 = 1.0F;
        int var18 = this.field_1876.world.method_3778(MathHelper.floor(var3.x), MathHelper.floor(var3.y), MathHelper.floor(var3.z), 0);
        int var8 = var18 % 65536;
        int var9 = var18 / 65536;
        GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, (float)var8 / 1.0F, (float)var9 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (var17 != null) {
            var18 = Item.ITEMS[var17.id].getDisplayColor(var17, 0);
            float var20 = (float)(var18 >> 16 & 0xFF) / 255.0F;
            float var21 = (float)(var18 >> 8 & 0xFF) / 255.0F;
            float var10 = (float)(var18 & 0xFF) / 255.0F;
            GL11.glColor4f(var6 * var20, var6 * var21, var6 * var10, 1.0F);
        } else {
            GL11.glColor4f(var6, var6, var6, 1.0F);
        }

        if (var17 != null && var17.getItem() instanceof FilledMapItem) {
            IItemRenderer custom = MinecraftForgeClient.getItemRenderer(var17, IItemRenderer.ItemRenderType.FIRST_PERSON_MAP);
            GL11.glPushMatrix();
            float var7 = 0.8F;
            float var20 = var3.method_2661(par1);
            float var21 = MathHelper.sin(var20 * (float) Math.PI);
            float var10 = MathHelper.sin(MathHelper.sqrt(var20) * (float) Math.PI);
            GL11.glTranslatef(-var10 * 0.4F, MathHelper.sin(MathHelper.sqrt(var20) * (float) Math.PI * 2.0F) * 0.2F, -var21 * 0.2F);
            var20 = 1.0F - var4 / 45.0F + 0.1F;
            if (var20 < 0.0F) {
                var20 = 0.0F;
            }

            if (var20 > 1.0F) {
                var20 = 1.0F;
            }

            var20 = -MathHelper.cos(var20 * (float) Math.PI) * 0.5F + 0.5F;
            GL11.glTranslatef(0.0F, 0.0F * var7 - (1.0F - var2) * 1.2F - var20 * 0.5F + 0.04F, -0.9F * var7);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(var20 * -85.0F, 0.0F, 0.0F, 1.0F);
            GL11.glEnable(32826);
            GL11.glBindTexture(3553, this.field_1876.field_3813.method_1423(this.field_1876.playerEntity.skinUrl, this.field_1876.playerEntity.method_2473()));

            for(int var29 = 0; var29 < 2; ++var29) {
                int var22 = var29 * 2 - 1;
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.0F, -0.6F, 1.1F * (float)var22);
                GL11.glRotatef((float)(-45 * var22), 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef((float)(-65 * var22), 0.0F, 1.0F, 0.0F);
                EntityRenderer var24 = EntityRenderDispatcher.field_2094.getRenderer(this.field_1876.playerEntity);
                PlayerEntityRenderer var26 = (PlayerEntityRenderer)var24;
                float var13 = 1.0F;
                GL11.glScalef(var13, var13, var13);
                var26.method_4339(this.field_1876.playerEntity);
                GL11.glPopMatrix();
            }

            var21 = var3.method_2661(par1);
            var10 = MathHelper.sin(var21 * var21 * (float) Math.PI);
            float var11 = MathHelper.sin(MathHelper.sqrt(var21) * (float) Math.PI);
            GL11.glRotatef(-var10 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-var11 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-var11 * 80.0F, 1.0F, 0.0F, 0.0F);
            float var12 = 0.38F;
            GL11.glScalef(var12, var12, var12);
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
            float var13 = 0.015625F;
            GL11.glScalef(var13, var13, var13);
            this.field_1876.field_3813.method_1426(this.field_1876.field_3813.getTextureFromPath("/misc/mapbg.png"));
            Tessellator var28 = Tessellator.INSTANCE;
            GL11.glNormal3f(0.0F, 0.0F, -1.0F);
            var28.method_1405();
            byte var27 = 7;
            var28.method_1399((double)(0 - var27), (double)(128 + var27), 0.0, 0.0, 1.0);
            var28.method_1399((double)(128 + var27), (double)(128 + var27), 0.0, 1.0, 1.0);
            var28.method_1399((double)(128 + var27), (double)(0 - var27), 0.0, 1.0, 0.0);
            var28.method_1399((double)(0 - var27), (double)(0 - var27), 0.0, 0.0, 0.0);
            var28.method_1396();
            MapState var16 = ((FilledMapItem)var17.getItem()).getMapState(var17, this.field_1876.world);
            if (custom == null) {
                if (var16 != null) {
                    this.field_1881.method_1023(this.field_1876.playerEntity, this.field_1876.field_3813, var16);
                }
            } else {
                custom.renderItem(IItemRenderer.ItemRenderType.FIRST_PERSON_MAP, var17, new Object[]{this.field_1876.playerEntity, this.field_1876.field_3813, var16});
            }

            GL11.glPopMatrix();
        } else if (var17 != null) {
            GL11.glPushMatrix();
            float var7 = 0.8F;
            if (var3.getItemUseTicks() > 0) {
                UseAction var19 = var17.getUseAction();
                if (var19 == UseAction.EAT || var19 == UseAction.DRINK) {
                    float var21 = (float)var3.getItemUseTicks() - par1 + 1.0F;
                    float var10 = 1.0F - var21 / (float)var17.getMaxUseTime();
                    float var11 = 1.0F - var10;
                    var11 = var11 * var11 * var11;
                    var11 = var11 * var11 * var11;
                    var11 = var11 * var11 * var11;
                    float var12 = 1.0F - var11;
                    GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(var21 / 4.0F * (float) Math.PI) * 0.1F) * (float)((double)var10 > 0.2 ? 1 : 0), 0.0F);
                    GL11.glTranslatef(var12 * 0.6F, -var12 * 0.5F, 0.0F);
                    GL11.glRotatef(var12 * 90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(var12 * 10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(var12 * 30.0F, 0.0F, 0.0F, 1.0F);
                }
            } else {
                float var20 = var3.method_2661(par1);
                float var21 = MathHelper.sin(var20 * (float) Math.PI);
                float var10 = MathHelper.sin(MathHelper.sqrt(var20) * (float) Math.PI);
                GL11.glTranslatef(-var10 * 0.4F, MathHelper.sin(MathHelper.sqrt(var20) * (float) Math.PI * 2.0F) * 0.2F, -var21 * 0.2F);
            }

            GL11.glTranslatef(0.7F * var7, -0.65F * var7 - (1.0F - var2) * 0.6F, -0.9F * var7);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(32826);
            float var20 = var3.method_2661(par1);
            float var21 = MathHelper.sin(var20 * var20 * (float) Math.PI);
            float var10 = MathHelper.sin(MathHelper.sqrt(var20) * (float) Math.PI);
            GL11.glRotatef(-var21 * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-var10 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-var10 * 80.0F, 1.0F, 0.0F, 0.0F);
            float var11 = 0.4F;
            GL11.glScalef(var11, var11, var11);
            if (var3.getItemUseTicks() > 0) {
                UseAction var23 = var17.getUseAction();
                if (var23 == UseAction.BLOCK) {
                    GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
                    GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
                } else if (var23 == UseAction.BOW) {
                    GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
                    float var13 = (float)var17.getMaxUseTime() - ((float)var3.getItemUseTicks() - par1 + 1.0F);
                    float var14 = var13 / 20.0F;
                    var14 = (var14 * var14 + var14 * 2.0F) / 3.0F;
                    if (var14 > 1.0F) {
                        var14 = 1.0F;
                    }

                    if (var14 > 0.1F) {
                        GL11.glTranslatef(0.0F, MathHelper.sin((var13 - 0.1F) * 1.3F) * 0.01F * (var14 - 0.1F), 0.0F);
                    }

                    GL11.glTranslatef(0.0F, 0.0F, var14 * 0.1F);
                    GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                    float var15 = 1.0F + var14 * 0.2F;
                    GL11.glScalef(1.0F, 1.0F, var15);
                    GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                    GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
                }
            }

            if (var17.getItem().shouldRotate()) {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (var17.getItem().method_3397()) {
                this.method_1357(var3, var17, 0);

                for(int x = 1; x < var17.getItem().getRenderPasses(var17.getMeta()); ++x) {
                    int var25 = Item.ITEMS[var17.id].getDisplayColor(var17, x);
                    float var13 = (float)(var25 >> 16 & 0xFF) / 255.0F;
                    float var14 = (float)(var25 >> 8 & 0xFF) / 255.0F;
                    float var15 = (float)(var25 & 0xFF) / 255.0F;
                    GL11.glColor4f(var6 * var13, var6 * var14, var6 * var15, 1.0F);
                    this.method_1357(var3, var17, x);
                }
            } else {
                this.method_1357(var3, var17, 0);
            }

            GL11.glPopMatrix();
        } else if (!var3.isInvisible()) {
            GL11.glPushMatrix();
            float var7 = 0.8F;
            float var20 = var3.method_2661(par1);
            float var21 = MathHelper.sin(var20 * (float) Math.PI);
            float var10 = MathHelper.sin(MathHelper.sqrt(var20) * (float) Math.PI);
            GL11.glTranslatef(-var10 * 0.3F, MathHelper.sin(MathHelper.sqrt(var20) * (float) Math.PI * 2.0F) * 0.4F, -var21 * 0.4F);
            GL11.glTranslatef(0.8F * var7, -0.75F * var7 - (1.0F - var2) * 0.6F, -0.9F * var7);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glEnable(32826);
            var20 = var3.method_2661(par1);
            var21 = MathHelper.sin(var20 * var20 * (float) Math.PI);
            var10 = MathHelper.sin(MathHelper.sqrt(var20) * (float) Math.PI);
            GL11.glRotatef(var10 * 70.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-var21 * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glBindTexture(3553, this.field_1876.field_3813.method_1423(this.field_1876.playerEntity.skinUrl, this.field_1876.playerEntity.method_2473()));
            GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
            GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(5.6F, 0.0F, 0.0F);
            EntityRenderer var24 = EntityRenderDispatcher.field_2094.getRenderer(this.field_1876.playerEntity);
            PlayerEntityRenderer var26 = (PlayerEntityRenderer)var24;
            float var13 = 1.0F;
            GL11.glScalef(var13, var13, var13);
            var26.method_4339(this.field_1876.playerEntity);
            GL11.glPopMatrix();
        }

        GL11.glDisable(32826);
        DiffuseLighting.disable();
    }
}
