package fr.catcore.fabricatedforge.mixin.forgefml.client.render.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_535;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PistonBlockEntityRenderer.class)
public abstract class PistonBlockEntityRendererMixin extends BlockEntityRenderer {

    @Shadow private class_535 field_2185;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1631(PistonBlockEntity par1TileEntityPiston, double par2, double par4, double par6, float par8) {
        Block var9 = Block.BLOCKS[par1TileEntityPiston.method_569()];
        if (var9 != null && par1TileEntityPiston.getAmountExtended(par8) < 1.0F) {
            Tessellator var10 = Tessellator.INSTANCE;
            this.method_1633("/terrain.png");
            DiffuseLighting.disable();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(2884);
            if (Minecraft.isAmbientOcclusionEnabled()) {
                GL11.glShadeModel(7425);
            } else {
                GL11.glShadeModel(7424);
            }

            ForgeHooksClient.beforeBlockRender(var9, this.field_2185);
            var10.method_1405();
            var10.method_1406(
                    (double)((float)par2 - (float)par1TileEntityPiston.x + par1TileEntityPiston.method_573(par8)),
                    (double)((float)par4 - (float)par1TileEntityPiston.y + par1TileEntityPiston.method_575(par8)),
                    (double)((float)par6 - (float)par1TileEntityPiston.z + par1TileEntityPiston.method_576(par8))
            );
            var10.method_1403(1, 1, 1);
            if (var9 == Block.PISTON_HEAD && par1TileEntityPiston.getAmountExtended(par8) < 0.5F) {
                this.field_2185.method_1452(var9, par1TileEntityPiston.x, par1TileEntityPiston.y, par1TileEntityPiston.z, false);
            } else if (par1TileEntityPiston.isSource() && !par1TileEntityPiston.isExtending()) {
                Block.PISTON_HEAD.method_563(((PistonBlock)var9).method_562());
                this.field_2185
                        .method_1452(
                                Block.PISTON_HEAD,
                                par1TileEntityPiston.x,
                                par1TileEntityPiston.y,
                                par1TileEntityPiston.z,
                                par1TileEntityPiston.getAmountExtended(par8) < 0.5F
                        );
                Block.PISTON_HEAD.method_565();
                var10.method_1406(
                        (double)((float)par2 - (float)par1TileEntityPiston.x),
                        (double)((float)par4 - (float)par1TileEntityPiston.y),
                        (double)((float)par6 - (float)par1TileEntityPiston.z)
                );
                this.field_2185.method_1466(var9, par1TileEntityPiston.x, par1TileEntityPiston.y, par1TileEntityPiston.z);
            } else {
                this.field_2185.method_1449(var9, par1TileEntityPiston.x, par1TileEntityPiston.y, par1TileEntityPiston.z);
            }

            var10.method_1406(0.0, 0.0, 0.0);
            var10.method_1396();
            ForgeHooksClient.afterBlockRender(var9, this.field_2185);
            DiffuseLighting.enableNormally();
        }

    }
}
