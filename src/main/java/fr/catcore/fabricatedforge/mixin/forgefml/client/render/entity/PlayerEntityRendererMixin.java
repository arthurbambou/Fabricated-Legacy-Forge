package fr.catcore.fabricatedforge.mixin.forgefml.client.render.entity;

import net.minecraft.block.Block;
import net.minecraft.client.class_535;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin extends MobEntityRenderer {

    @Shadow public static String[] field_2136;

    @Shadow private BiPedModel field_2135;

    @Shadow private BiPedModel field_2134;

    @Shadow private BiPedModel field_2133;

    public PlayerEntityRendererMixin(EntityModel entityModel, float f) {
        super(entityModel, f);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected int method_1564(PlayerEntity par1EntityPlayer, int par2, float par3) {
        ItemStack var4 = par1EntityPlayer.inventory.getArmor(3 - par2);
        if (var4 != null) {
            Item var5 = var4.getItem();
            if (var5 instanceof ArmorItem) {
                ArmorItem var6 = (ArmorItem)var5;
                this.method_1529(ForgeHooksClient.getArmorTexture(var4, "/armor/" + field_2136[var6.materialId] + "_" + (par2 == 2 ? 2 : 1) + ".png"));
                BiPedModel var7 = par2 == 2 ? this.field_2135 : this.field_2134;
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
    protected void method_4337(PlayerEntity par1EntityPlayer, int par2, float par3) {
        ItemStack var4 = par1EntityPlayer.inventory.getArmor(3 - par2);
        if (var4 != null) {
            Item var5 = var4.getItem();
            if (var5 instanceof ArmorItem) {
                ArmorItem var6 = (ArmorItem)var5;
                this.method_1529(ForgeHooksClient.getArmorTexture(var4, "/armor/" + field_2136[var6.materialId] + "_" + (par2 == 2 ? 2 : 1) + "_b.png"));
                float var7 = 1.0F;
                GL11.glColor3f(var7, var7, var7);
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void method_1569(PlayerEntity par1EntityPlayer, float par2) {
        float var3 = 1.0F;
        GL11.glColor3f(var3, var3, var3);
        super.method_1569(par1EntityPlayer, par2);
        ItemStack var4 = par1EntityPlayer.inventory.getArmor(3);
        if (var4 != null) {
            GL11.glPushMatrix();
            this.field_2133.head.method_1195(0.0625F);
            if (var4 != null && var4.getItem() instanceof BlockItem) {
                IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var4, IItemRenderer.ItemRenderType.EQUIPPED);
                boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, var4, IItemRenderer.ItemRendererHelper.BLOCK_3D);
                if (is3D || class_535.method_1455(Block.BLOCKS[var4.id].getBlockType())) {
                    float var5 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var5, -var5, -var5);
                }

                this.dispatcher.field_2099.method_1357(par1EntityPlayer, var4, 0);
            } else if (var4.getItem().id == Item.SKULL.id) {
                float var5 = 1.0625F;
                GL11.glScalef(var5, -var5, -var5);
                String var6 = "";
                if (var4.hasNbt() && var4.getNbt().contains("SkullOwner")) {
                    var6 = var4.getNbt().getString("SkullOwner");
                }

                SkullBlockEntityRenderer.instance.method_4363(-0.5F, 0.0F, -0.5F, 1, 180.0F, var4.getMeta(), var6);
            }

            GL11.glPopMatrix();
        }

        if (par1EntityPlayer.username.equals("deadmau5") && this.method_1530(par1EntityPlayer.skinUrl, (String)null)) {
            for(int var20 = 0; var20 < 2; ++var20) {
                float var25 = par1EntityPlayer.prevYaw
                        + (par1EntityPlayer.yaw - par1EntityPlayer.prevYaw) * par2
                        - (par1EntityPlayer.field_3314 + (par1EntityPlayer.field_3313 - par1EntityPlayer.field_3314) * par2);
                float var7 = par1EntityPlayer.prevPitch + (par1EntityPlayer.pitch - par1EntityPlayer.prevPitch) * par2;
                GL11.glPushMatrix();
                GL11.glRotatef(var25, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(var7, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.375F * (float)(var20 * 2 - 1), 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.375F, 0.0F);
                GL11.glRotatef(-var7, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-var25, 0.0F, 1.0F, 0.0F);
                float var8 = 1.3333334F;
                GL11.glScalef(var8, var8, var8);
                this.field_2133.method_1170(0.0625F);
                GL11.glPopMatrix();
            }
        }

        if (this.method_1530(par1EntityPlayer.field_4008, (String)null) && !par1EntityPlayer.isInvisible() && !par1EntityPlayer.method_4577()) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.125F);
            double var22 = par1EntityPlayer.capeX
                    + (par1EntityPlayer.prevCapeX - par1EntityPlayer.capeX) * (double)par2
                    - (par1EntityPlayer.prevX + (par1EntityPlayer.x - par1EntityPlayer.prevX) * (double)par2);
            double var24 = par1EntityPlayer.capeY
                    + (par1EntityPlayer.prevCapeY - par1EntityPlayer.capeY) * (double)par2
                    - (par1EntityPlayer.prevY + (par1EntityPlayer.y - par1EntityPlayer.prevY) * (double)par2);
            double var9 = par1EntityPlayer.capeZ
                    + (par1EntityPlayer.prevCapeZ - par1EntityPlayer.capeZ) * (double)par2
                    - (par1EntityPlayer.prevZ + (par1EntityPlayer.z - par1EntityPlayer.prevZ) * (double)par2);
            float var11 = par1EntityPlayer.field_3314 + (par1EntityPlayer.field_3313 - par1EntityPlayer.field_3314) * par2;
            double var12 = (double)MathHelper.sin(var11 * (float) Math.PI / 180.0F);
            double var14 = (double)(-MathHelper.cos(var11 * (float) Math.PI / 180.0F));
            float var16 = (float)var24 * 10.0F;
            if (var16 < -6.0F) {
                var16 = -6.0F;
            }

            if (var16 > 32.0F) {
                var16 = 32.0F;
            }

            float var17 = (float)(var22 * var12 + var9 * var14) * 100.0F;
            float var18 = (float)(var22 * var14 - var9 * var12) * 100.0F;
            if (var17 < 0.0F) {
                var17 = 0.0F;
            }

            float var19 = par1EntityPlayer.prevStrideDistance + (par1EntityPlayer.strideDistance - par1EntityPlayer.prevStrideDistance) * par2;
            var16 += MathHelper.sin(
                    (par1EntityPlayer.prevHorizontalSpeed + (par1EntityPlayer.horizontalSpeed - par1EntityPlayer.prevHorizontalSpeed) * par2) * 6.0F
            )
                    * 32.0F
                    * var19;
            if (par1EntityPlayer.isSneaking()) {
                var16 += 25.0F;
            }

            GL11.glRotatef(6.0F + var17 / 2.0F + var16, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(var18 / 2.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-var18 / 2.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            this.field_2133.method_1171(0.0625F);
            GL11.glPopMatrix();
        }

        ItemStack var21 = par1EntityPlayer.inventory.getMainHandStack();
        if (var21 != null) {
            GL11.glPushMatrix();
            this.field_2133.field_1476.method_1195(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            if (par1EntityPlayer.fishHook != null) {
                var21 = new ItemStack(Item.STICK);
            }

            UseAction var23 = null;
            if (par1EntityPlayer.getItemUseTicks() > 0) {
                var23 = var21.getUseAction();
            }

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var21, IItemRenderer.ItemRenderType.EQUIPPED);
            boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, var21, IItemRenderer.ItemRendererHelper.BLOCK_3D);
            if (!(var21.getItem() instanceof BlockItem) || !is3D && !class_535.method_1455(Block.BLOCKS[var21.id].getBlockType())) {
                if (var21.id == Item.field_4349.id) {
                    float var7 = 0.625F;
                    GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
                    GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var7, -var7, var7);
                    GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                } else if (Item.ITEMS[var21.id].isHandheld()) {
                    float var7 = 0.625F;
                    if (Item.ITEMS[var21.id].shouldRotate()) {
                        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                    }

                    if (par1EntityPlayer.getItemUseTicks() > 0 && var23 == UseAction.BLOCK) {
                        GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                    }

                    GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
                    GL11.glScalef(var7, -var7, var7);
                    GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                } else {
                    float var7 = 0.375F;
                    GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                    GL11.glScalef(var7, var7, var7);
                    GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                }
            } else {
                float var7 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                var7 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var7, -var7, var7);
            }

            if (var21.getItem().method_3397()) {
                for(int var27 = 0; var27 < var21.getItem().getRenderPasses(var21.getMeta()); ++var27) {
                    int var26 = var21.getItem().getDisplayColor(var21, var27);
                    float var28 = (float)(var26 >> 16 & 0xFF) / 255.0F;
                    float var10 = (float)(var26 >> 8 & 0xFF) / 255.0F;
                    float var11 = (float)(var26 & 0xFF) / 255.0F;
                    GL11.glColor4f(var28, var10, var11, 1.0F);
                    this.dispatcher.field_2099.method_1357(par1EntityPlayer, var21, var27);
                }
            } else {
                this.dispatcher.field_2099.method_1357(par1EntityPlayer, var21, 0);
            }

            GL11.glPopMatrix();
        }

    }
}
