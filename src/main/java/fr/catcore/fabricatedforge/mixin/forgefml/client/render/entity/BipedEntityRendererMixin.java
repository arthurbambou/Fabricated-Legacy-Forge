package fr.catcore.fabricatedforge.mixin.forgefml.client.render.entity;

import net.minecraft.block.Block;
import net.minecraft.client.class_535;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BipedEntityRenderer.class)
public abstract class BipedEntityRendererMixin extends MobEntityRenderer {

    @Shadow protected BiPedModel bipedModel;

    @Shadow protected BiPedModel field_5194;

    @Shadow public static String[] field_5195;

    @Shadow protected BiPedModel field_5193;

    @Shadow protected abstract void method_4331();

    public BipedEntityRendererMixin(EntityModel entityModel, float f) {
        super(entityModel, f);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected int method_1564(MobEntity par1EntityLiving, int par2, float par3) {
        ItemStack var4 = par1EntityLiving.method_4485(3 - par2);
        if (var4 != null) {
            Item var5 = var4.getItem();
            if (var5 instanceof ArmorItem) {
                ArmorItem var6 = (ArmorItem)var5;
                this.method_1529(ForgeHooksClient.getArmorTexture(var4, "/armor/" + field_5195[var6.materialId] + "_" + (par2 == 2 ? 2 : 1) + ".png"));
                BiPedModel var7 = par2 == 2 ? this.field_5194 : this.field_5193;
                var7.head.visible = par2 == 0;
                var7.hat.visible = par2 == 0;
                var7.body.visible = par2 == 1 || par2 == 2;
                var7.field_1476.visible = par2 == 1;
                var7.field_1477.visible = par2 == 1;
                var7.field_1478.visible = par2 == 2 || par2 == 3;
                var7.field_1479.visible = par2 == 2 || par2 == 3;
                this.method_1556(var7);
                if (var7 != null) {
                    var7.field_1490 = this.field_2130.field_1490;
                }

                if (var7 != null) {
                    var7.field_1491 = this.field_2130.field_1491;
                }

                if (var7 != null) {
                    var7.field_1493 = this.field_2130.field_1493;
                }

                float var8 = 1.0F;
                if (var6.method_4602() == ArmorMaterial.CLOTH) {
                    int var9 = var6.getColor(var4);
                    float var10 = (float)(var9 >> 16 & 0xFF) / 255.0F;
                    float var11 = (float)(var9 >> 8 & 0xFF) / 255.0F;
                    float var12 = (float)(var9 & 0xFF) / 255.0F;
                    GL11.glColor3f(var8 * var10, var8 * var11, var8 * var12);
                    if (var4.hasEnchantments()) {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(var8, var8, var8);
                if (var4.hasEnchantments()) {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void method_1569(MobEntity par1EntityLiving, float par2) {
        float var3 = 1.0F;
        GL11.glColor3f(var3, var3, var3);
        super.method_1569(par1EntityLiving, par2);
        ItemStack var4 = par1EntityLiving.method_2640();
        ItemStack var5 = par1EntityLiving.method_4485(3);
        if (var5 != null) {
            GL11.glPushMatrix();
            this.bipedModel.head.method_1195(0.0625F);
            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var5, IItemRenderer.ItemRenderType.EQUIPPED);
            boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, var5, IItemRenderer.ItemRendererHelper.BLOCK_3D);
            if (!(var5.getItem() instanceof BlockItem)) {
                if (var5.getItem().id == Item.SKULL.id) {
                    float var6 = 1.0625F;
                    GL11.glScalef(var6, -var6, -var6);
                    String var7 = "";
                    if (var5.hasNbt() && var5.getNbt().contains("SkullOwner")) {
                        var7 = var5.getNbt().getString("SkullOwner");
                    }

                    SkullBlockEntityRenderer.instance.method_4363(-0.5F, 0.0F, -0.5F, 1, 180.0F, var5.getMeta(), var7);
                }
            } else {
                if (is3D || class_535.method_1455(Block.BLOCKS[var5.id].getBlockType())) {
                    float var6 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var6, -var6, -var6);
                }

                this.dispatcher.field_2099.method_1357(par1EntityLiving, var5, 0);
            }

            GL11.glPopMatrix();
        }

        if (var4 != null) {
            GL11.glPushMatrix();
            if (this.field_2130.field_1493) {
                float var6 = 0.5F;
                GL11.glTranslatef(0.0F, 0.625F, 0.0F);
                GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
                GL11.glScalef(var6, var6, var6);
            }

            this.bipedModel.field_1476.method_1195(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var4, IItemRenderer.ItemRenderType.EQUIPPED);
            boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, var4, IItemRenderer.ItemRendererHelper.BLOCK_3D);
            if (!(var4.getItem() instanceof BlockItem) || !is3D && !class_535.method_1455(Block.BLOCKS[var4.id].getBlockType())) {
                if (var4.id == Item.field_4349.id) {
                    float var6 = 0.625F;
                    GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                    GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var6, -var6, var6);
                    GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                } else if (Item.ITEMS[var4.id].isHandheld()) {
                    float var6 = 0.625F;
                    if (Item.ITEMS[var4.id].shouldRotate()) {
                        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                    }

                    this.method_4331();
                    GL11.glScalef(var6, -var6, var6);
                    GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    float var6 = 0.375F;
                    GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                    GL11.glScalef(var6, var6, var6);
                    GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                }
            } else {
                float var6 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                var6 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
            }

            this.dispatcher.field_2099.method_1357(par1EntityLiving, var4, 0);
            if (var4.getItem().method_3397()) {
                for(int x = 1; x < var4.getItem().getRenderPasses(var4.getMeta()); ++x) {
                    this.dispatcher.field_2099.method_1357(par1EntityLiving, var4, x);
                }
            }

            GL11.glPopMatrix();
        }

    }
}
