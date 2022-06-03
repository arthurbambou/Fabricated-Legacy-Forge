package fr.catcore.fabricatedforge.mixin.forgefml.client.particle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fr.catcore.fabricatedforge.mixininterface.IParticleManager;
import net.minecraft.block.Block;
import net.minecraft.client.class_321;
import net.minecraft.client.class_534;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeHooks;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin implements IParticleManager {

    @Shadow private List[] field_1735;
    @Shadow private class_534 field_1736;
    @Shadow protected World world;
    @Shadow private Random random;

    @Shadow public abstract void method_1295(Particle particle);

    // Forge Field
    @Unique
    private Multimap<String, Particle> effectList = ArrayListMultimap.create();

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void tick() {
        for(int var1 = 0; var1 < 4; ++var1) {
            for(int var2 = 0; var2 < this.field_1735[var1].size(); ++var2) {
                Particle var3 = (Particle)this.field_1735[var1].get(var2);
                if (var3 != null) {
                    var3.tick();
                }

                if (var3 == null || var3.removed) {
                    this.field_1735[var1].remove(var2--);
                }
            }
        }

        Iterator<Map.Entry<String, Particle>> itr = this.effectList.entries().iterator();

        while(itr.hasNext()) {
            Particle fx = (Particle)((Map.Entry)itr.next()).getValue();
            fx.tick();
            if (fx.removed) {
                itr.remove();
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1296(Entity par1Entity, float par2) {
        float var3 = class_321.field_890;
        float var4 = class_321.field_892;
        float var5 = class_321.field_893;
        float var6 = class_321.field_894;
        float var7 = class_321.field_891;
        Particle.field_1722 = par1Entity.prevTickX + (par1Entity.x - par1Entity.prevTickX) * (double)par2;
        Particle.field_1723 = par1Entity.prevTickY + (par1Entity.y - par1Entity.prevTickY) * (double)par2;
        Particle.field_1724 = par1Entity.prevTickZ + (par1Entity.z - par1Entity.prevTickZ) * (double)par2;

        for(int var8 = 0; var8 < 3; ++var8) {
            if (!this.field_1735[var8].isEmpty()) {
                int var9 = 0;
                if (var8 == 0) {
                    var9 = this.field_1736.getTextureFromPath("/particles.png");
                }

                if (var8 == 1) {
                    var9 = this.field_1736.getTextureFromPath("/terrain.png");
                }

                if (var8 == 2) {
                    var9 = this.field_1736.getTextureFromPath("/gui/items.png");
                }

                GL11.glBindTexture(3553, var9);
                Tessellator var10 = Tessellator.INSTANCE;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                var10.method_1405();

                for(int var11 = 0; var11 < this.field_1735[var8].size(); ++var11) {
                    Particle var12 = (Particle)this.field_1735[var8].get(var11);
                    if (var12 != null) {
                        var10.method_1411(var12.getLightmapCoordinates(par2));
                        var12.method_1283(var10, par2, var3, var7, var4, var5, var6);
                    }
                }

                var10.method_1396();
            }
        }

        for(String key : this.effectList.keySet()) {
            ForgeHooksClient.bindTexture(key, 0);

            for(Particle entry : this.effectList.get(key)) {
                if (entry != null) {
                    Tessellator tessallator = Tessellator.INSTANCE;
                    tessallator.method_1405();
                    if (entry.method_1284() != 3) {
                        tessallator.method_1411(entry.getLightmapCoordinates(par2));
                        entry.method_1283(tessallator, par2, var3, var7, var4, var5, var6);
                    }

                    tessallator.method_1396();
                }
            }

            ForgeHooksClient.unbindTexture();
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1299(Entity par1Entity, float par2) {
        float var4 = MathHelper.cos(par1Entity.yaw * (float) (Math.PI / 180.0));
        float var5 = MathHelper.sin(par1Entity.yaw * (float) (Math.PI / 180.0));
        float var6 = -var5 * MathHelper.sin(par1Entity.pitch * (float) (Math.PI / 180.0));
        float var7 = var4 * MathHelper.sin(par1Entity.pitch * (float) (Math.PI / 180.0));
        float var8 = MathHelper.cos(par1Entity.pitch * (float) (Math.PI / 180.0));
        byte var9 = 3;
        if (!this.field_1735[var9].isEmpty()) {
            Tessellator var10 = Tessellator.INSTANCE;

            for(int var11 = 0; var11 < this.field_1735[var9].size(); ++var11) {
                Particle var12 = (Particle)this.field_1735[var9].get(var11);
                if (var12 != null) {
                    var10.method_1411(var12.getLightmapCoordinates(par2));
                    var12.method_1283(var10, par2, var4, var8, var5, var6, var7);
                }
            }
        }

    }

    @Inject(method = "setWorld", at = @At("RETURN"))
    private void forgeSetWorld(World par1, CallbackInfo ci) {
        this.effectList.clear();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1294(int par1, int par2, int par3, int par4, int par5) {
        Block var6 = Block.BLOCKS[par4];
        if (var6 != null && !var6.addBlockDestroyEffects(this.world, par1, par2, par3, par5, (ParticleManager)(Object) this)) {
            byte var7 = 4;

            for(int var8 = 0; var8 < var7; ++var8) {
                for(int var9 = 0; var9 < var7; ++var9) {
                    for(int var10 = 0; var10 < var7; ++var10) {
                        double var11 = (double)par1 + ((double)var8 + 0.5) / (double)var7;
                        double var13 = (double)par2 + ((double)var9 + 0.5) / (double)var7;
                        double var15 = (double)par3 + ((double)var10 + 0.5) / (double)var7;
                        int var17 = this.random.nextInt(6);
                        this.addEffect(
                                new BlockDustParticle(
                                        this.world,
                                        var11,
                                        var13,
                                        var15,
                                        var11 - (double)par1 - 0.5,
                                        var13 - (double)par2 - 0.5,
                                        var15 - (double)par3 - 0.5,
                                        var6,
                                        var17,
                                        par5
                                )
                                        .method_1301(par1, par2, par3),
                                var6
                        );
                    }
                }
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1293(int par1, int par2, int par3, int par4) {
        int var5 = this.world.getBlock(par1, par2, par3);
        if (var5 != 0) {
            Block var6 = Block.BLOCKS[var5];
            float var7 = 0.1F;
            double var8 = (double)par1
                    + this.random.nextDouble() * (var6.boundingBoxMaxX - var6.boundingBoxMinX - (double)(var7 * 2.0F))
                    + (double)var7
                    + var6.boundingBoxMinX;
            double var10 = (double)par2
                    + this.random.nextDouble() * (var6.boundingBoxMaxY - var6.boundingBoxMinY - (double)(var7 * 2.0F))
                    + (double)var7
                    + var6.boundingBoxMinY;
            double var12 = (double)par3
                    + this.random.nextDouble() * (var6.boundingBoxMaxZ - var6.boundingBoxMinZ - (double)(var7 * 2.0F))
                    + (double)var7
                    + var6.boundingBoxMinZ;
            if (par4 == 0) {
                var10 = (double)par2 + var6.boundingBoxMinY - (double)var7;
            }

            if (par4 == 1) {
                var10 = (double)par2 + var6.boundingBoxMaxY + (double)var7;
            }

            if (par4 == 2) {
                var12 = (double)par3 + var6.boundingBoxMinZ - (double)var7;
            }

            if (par4 == 3) {
                var12 = (double)par3 + var6.boundingBoxMaxZ + (double)var7;
            }

            if (par4 == 4) {
                var8 = (double)par1 + var6.boundingBoxMinX - (double)var7;
            }

            if (par4 == 5) {
                var8 = (double)par1 + var6.boundingBoxMaxX + (double)var7;
            }

            this.addEffect(
                    new BlockDustParticle(this.world, var8, var10, var12, 0.0, 0.0, 0.0, var6, par4, this.world.getBlockData(par1, par2, par3))
                            .method_1301(par1, par2, par3)
                            .method_1287(0.2F)
                            .method_1289(0.6F),
                    var6
            );
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public String method_1298() {
        int size = 0;

        for(List x : this.field_1735) {
            size += x.size();
        }

        size += this.effectList.size();
        return Integer.toString(size);
    }

    @Override
    public void addEffect(Particle effect, Object obj) {
        if (obj != null && (obj instanceof Block || obj instanceof Item)) {
            if (obj instanceof Item && ((Item)obj).isDefaultTexture()) {
                this.method_1295(effect);
            } else if (obj instanceof Block && ((Block)obj).isDefaultTexture()) {
                this.method_1295(effect);
            } else {
                String texture = "/terrain.png";
                if (effect.method_1284() == 0) {
                    texture = "/particles.png";
                } else if (effect.method_1284() == 2) {
                    texture = "/gui/items.png";
                }

                texture = ForgeHooks.getTexture(texture, obj);
                this.effectList.put(texture, effect);
            }
        } else {
            this.method_1295(effect);
        }
    }

    @Override
    public void addBlockHitEffects(int x, int y, int z, BlockHitResult target) {
        Block block = Block.BLOCKS[this.world.getBlock(x, y, z)];
        if (block != null && !block.addBlockHitEffects(this.world, target, (ParticleManager)(Object) this)) {
            this.method_1293(x, y, z, target.side);
        }

    }
}
