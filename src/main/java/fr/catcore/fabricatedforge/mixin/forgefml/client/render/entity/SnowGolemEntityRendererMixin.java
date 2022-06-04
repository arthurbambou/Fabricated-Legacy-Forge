package fr.catcore.fabricatedforge.mixin.forgefml.client.render.entity;

import net.minecraft.block.Block;
import net.minecraft.client.class_535;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.SnowGolemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SnowGolemEntityRenderer.class)
public class SnowGolemEntityRendererMixin extends MobEntityRenderer {
    @Shadow private SnowmanEntityModel field_2138;

    public SnowGolemEntityRendererMixin(EntityModel entityModel, float f) {
        super(entityModel, f);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void method_1569(SnowGolemEntity par1EntitySnowman, float par2) {
        super.method_1569(par1EntitySnowman, par2);
        ItemStack var3 = new ItemStack(Block.PUMPKIN, 1);
        if (var3 != null && var3.getItem() instanceof BlockItem) {
            GL11.glPushMatrix();
            this.field_2138.field_1532.method_1195(0.0625F);
            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var3, IItemRenderer.ItemRenderType.EQUIPPED);
            boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, var3, IItemRenderer.ItemRendererHelper.BLOCK_3D);
            if (is3D || class_535.method_1455(Block.BLOCKS[var3.id].getBlockType())) {
                float var4 = 0.625F;
                GL11.glTranslatef(0.0F, -0.34375F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var4, -var4, var4);
            }

            this.dispatcher.field_2099.method_1357(par1EntitySnowman, var3, 0);
            GL11.glPopMatrix();
        }

    }
}
