package fr.catcore.fabricatedforge.mixin.forgefml.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.class_535;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.List;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin {

    @Shadow public boolean textured;

    @Shadow public int field_1781;

    @Shadow public int field_1782;

    @Shadow public int field_1783;

    @Shadow public boolean[] field_1791;

    @Shadow public List field_1802;

    @Shadow public World field_1779;

    @Shadow public static int field_1780;

    @Shadow private int field_1778;

    @Shadow private int field_1803;

    @Shadow protected abstract void method_1317();

    @Shadow private List field_1777;

    @Shadow public boolean field_1801;

    @Shadow private boolean building;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1307() {
        if (this.textured) {
            this.textured = false;
            int var1 = this.field_1781;
            int var2 = this.field_1782;
            int var3 = this.field_1783;
            int var4 = this.field_1781 + 16;
            int var5 = this.field_1782 + 16;
            int var6 = this.field_1783 + 16;

            for(int var7 = 0; var7 < 2; ++var7) {
                this.field_1791[var7] = true;
            }

            Chunk.field_4724 = false;
            HashSet var21 = new HashSet();
            var21.addAll(this.field_1802);
            this.field_1802.clear();
            byte var8 = 1;
            ChunkCache var9 = new ChunkCache(this.field_1779, var1 - var8, var2 - var8, var3 - var8, var4 + var8, var5 + var8, var6 + var8);
            if (!var9.isEmpty()) {
                ++field_1780;
                class_535 var10 = new class_535(var9);
                this.field_1778 = 0;

                for(int var11 = 0; var11 < 2; ++var11) {
                    boolean var12 = false;
                    boolean var13 = false;
                    boolean var14 = false;

                    for(int var15 = var2; var15 < var5; ++var15) {
                        for(int var16 = var3; var16 < var6; ++var16) {
                            for(int var17 = var1; var17 < var4; ++var17) {
                                int var18 = var9.getBlock(var17, var15, var16);
                                if (var18 > 0) {
                                    if (!var14) {
                                        var14 = true;
                                        GL11.glNewList(this.field_1803 + var11, 4864);
                                        GL11.glPushMatrix();
                                        this.method_1317();
                                        float var19 = 1.000001F;
                                        GL11.glTranslatef(-8.0F, -8.0F, -8.0F);
                                        GL11.glScalef(var19, var19, var19);
                                        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
                                        ForgeHooksClient.beforeRenderPass(var11);
                                        Tessellator.INSTANCE.method_1405();
                                        Tessellator.INSTANCE.method_1406((double)(-this.field_1781), (double)(-this.field_1782), (double)(-this.field_1783));
                                    }

                                    Block var23 = Block.BLOCKS[var18];
                                    if (var23 != null) {
                                        if (var11 == 0 && var23.hasTileEntity(var9.getBlockData(var17, var15, var16))) {
                                            BlockEntity var20 = var9.method_3781(var17, var15, var16);
                                            if (BlockEntityRenderDispatcher.INSTANCE.method_1624(var20)) {
                                                this.field_1802.add(var20);
                                            }
                                        }

                                        int var24 = var23.method_479();
                                        if (var24 > var11) {
                                            var12 = true;
                                        }

                                        if (var23.canRenderInPass(var11)) {
                                            ForgeHooksClient.beforeBlockRender(var23, var10);
                                            var13 |= var10.method_1458(var23, var17, var15, var16);
                                            ForgeHooksClient.afterBlockRender(var23, var10);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (var14) {
                        ForgeHooksClient.afterRenderPass(var11);
                        this.field_1778 += Tessellator.INSTANCE.method_1396();
                        GL11.glPopMatrix();
                        GL11.glEndList();
                        Tessellator.INSTANCE.method_1406(0.0, 0.0, 0.0);
                    } else {
                        var13 = false;
                    }

                    if (var13) {
                        this.field_1791[var11] = false;
                    }

                    if (!var12) {
                        break;
                    }
                }
            }

            HashSet var22 = new HashSet();
            var22.addAll(this.field_1802);
            var22.removeAll(var21);
            this.field_1777.addAll(var22);
            var21.removeAll(this.field_1802);
            this.field_1777.removeAll(var21);
            this.field_1801 = Chunk.field_4724;
            this.building = true;
        }

    }
}
