package fr.catcore.fabricatedforge.mixin.forgefml.entity.mob;

import fr.catcore.fabricatedforge.mixininterface.IMobEntity;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdate_S2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Iterator;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends Entity implements IMobEntity {

    @Shadow public int field_3334;

    @Shadow public float field_3313;

    @Shadow protected float field_3318;

    @Shadow protected float field_3317;

    @Shadow public float field_3293;

    @Shadow private BodyControl bodyControl;

    @Shadow public float field_3314;

    @Shadow public float field_3316;

    @Shadow public float field_3315;

    @Shadow protected float field_3319;

    @Shadow protected int field_3346;

    @Shadow protected int field_3294;

    @Shadow public abstract boolean method_2581(StatusEffect statusEffect);

    @Shadow public float field_3309;

    @Shadow public int field_3310;

    @Shadow protected int field_3345;

    @Shadow public int field_3295;

    @Shadow public int field_3297;

    @Shadow public int field_3298;

    @Shadow public float field_3299;

    @Shadow public abstract void method_2650(MobEntity mobEntity);

    @Shadow protected int field_3332;

    @Shadow protected PlayerEntity field_3331;

    @Shadow public abstract void method_2584(Entity entity, int i, double d, double e);

    @Shadow protected abstract String method_2605();

    @Shadow protected abstract float method_2602();

    @Shadow protected abstract float method_2665();

    @Shadow protected abstract String method_2604();

    @Shadow protected abstract int method_2626(DamageSource damageSource, int i);

    @Shadow protected abstract int method_2648(DamageSource damageSource, int i);

    @Shadow protected int field_3287;

    @Shadow protected boolean field_3304;

    @Shadow public abstract boolean method_2662();

    @Shadow protected abstract void method_2587(boolean bl, int i);

    @Shadow protected abstract void method_2672(int i);

    @Shadow public abstract StatusEffectInstance method_2627(StatusEffect statusEffect);

    @Shadow protected HashMap field_3335;

    @Shadow protected abstract void method_2649(StatusEffectInstance statusEffectInstance);

    @Shadow
    float field_3344;

    @Shadow public abstract ItemStack method_4484(int i);

    @Shadow private ItemStack[] field_5336;

    @Shadow public int field_23096;

    @Shadow public abstract void method_2651();

    @Shadow protected abstract boolean method_2608();

    @Shadow protected abstract void method_4472(boolean bl, int i);

    public MobEntityMixin(World world) {
        super(world);
    }

    @Inject(method = {"method_2629", "method_2650"}, at = @At("RETURN"))
    private void FMLOnLivingSetAttackTarget(MobEntity par1EntityLiving, CallbackInfo ci) {
        ForgeHooks.onLivingSetAttackTarget((MobEntity)(Object) this, par1EntityLiving);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void tick() {
        if (!ForgeHooks.onLivingUpdate((MobEntity)(Object) this)) {
            super.tick();
            if (!this.world.isClient) {
                for(int var1 = 0; var1 < 5; ++var1) {
                    ItemStack var2 = this.method_4484(var1);
                    if (!ItemStack.equalsAll(var2, this.field_5336[var1])) {
                        ((ServerWorld)this.world)
                                .getEntityTracker()
                                .sendToOtherTrackingEntities(this, new EntityEquipmentUpdate_S2CPacket(this.id, var1, var2));
                        this.field_5336[var1] = var2 == null ? null : var2.copy();
                    }
                }
            }

            if (this.field_23096 > 0) {
                if (this.field_3334 <= 0) {
                    this.field_3334 = 60;
                }

                --this.field_3334;
                if (this.field_3334 <= 0) {
                    --this.field_23096;
                }
            }

            this.method_2651();
            double var12 = this.x - this.prevX;
            double var3 = this.z - this.prevZ;
            float var5 = (float)(var12 * var12 + var3 * var3);
            float var6 = this.field_3313;
            float var7 = 0.0F;
            this.field_3317 = this.field_3318;
            float var8 = 0.0F;
            if (var5 > 0.0025000002F) {
                var8 = 1.0F;
                var7 = (float)Math.sqrt((double)var5) * 3.0F;
                var6 = (float)Math.atan2(var3, var12) * 180.0F / (float) Math.PI - 90.0F;
            }

            if (this.field_3293 > 0.0F) {
                var6 = this.yaw;
            }

            if (!this.onGround) {
                var8 = 0.0F;
            }

            this.field_3318 += (var8 - this.field_3318) * 0.3F;
            this.world.profiler.push("headTurn");
            if (this.method_2608()) {
                this.bodyControl.tick();
            } else {
                float var9 = MathHelper.wrapDegrees(var6 - this.field_3313);
                this.field_3313 += var9 * 0.3F;
                float var10 = MathHelper.wrapDegrees(this.yaw - this.field_3313);
                boolean var11 = var10 < -90.0F || var10 >= 90.0F;
                if (var10 < -75.0F) {
                    var10 = -75.0F;
                }

                if (var10 >= 75.0F) {
                    var10 = 75.0F;
                }

                this.field_3313 = this.yaw - var10;
                if (var10 * var10 > 2500.0F) {
                    this.field_3313 += var10 * 0.2F;
                }

                if (var11) {
                    var7 *= -1.0F;
                }
            }

            this.world.profiler.pop();
            this.world.profiler.push("rangeChecks");

            while(this.yaw - this.prevYaw < -180.0F) {
                this.prevYaw -= 360.0F;
            }

            while(this.yaw - this.prevYaw >= 180.0F) {
                this.prevYaw += 360.0F;
            }

            while(this.field_3313 - this.field_3314 < -180.0F) {
                this.field_3314 -= 360.0F;
            }

            while(this.field_3313 - this.field_3314 >= 180.0F) {
                this.field_3314 += 360.0F;
            }

            while(this.pitch - this.prevPitch < -180.0F) {
                this.prevPitch -= 360.0F;
            }

            while(this.pitch - this.prevPitch >= 180.0F) {
                this.prevPitch += 360.0F;
            }

            while(this.field_3315 - this.field_3316 < -180.0F) {
                this.field_3316 -= 360.0F;
            }

            while(this.field_3315 - this.field_3316 >= 180.0F) {
                this.field_3316 += 360.0F;
            }

            this.world.profiler.pop();
            this.field_3319 += var7;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean damage(DamageSource par1DamageSource, int par2) {
        if (ForgeHooks.onLivingAttack((MobEntity)(Object) this, par1DamageSource, par2)) {
            return false;
        } else if (this.world.isClient) {
            return false;
        } else {
            this.field_3346 = 0;
            if (this.field_3294 <= 0) {
                return false;
            } else if (par1DamageSource.isFire() && this.method_2581(StatusEffect.FIRE_RESISTANCE)) {
                return false;
            } else {
                if ((par1DamageSource == DamageSource.ANVIL || par1DamageSource == DamageSource.FALLING_BLOCK) && this.method_4484(4) != null) {
                    par2 = (int)((float)par2 * 0.55F);
                }

                this.field_3309 = 1.5F;
                boolean var3 = true;
                if ((float)this.field_3223 > (float)this.field_3310 / 2.0F) {
                    if (par2 <= this.field_3345) {
                        return false;
                    }

                    this.method_2653(par1DamageSource, par2 - this.field_3345);
                    this.field_3345 = par2;
                    var3 = false;
                } else {
                    this.field_3345 = par2;
                    this.field_3295 = this.field_3294;
                    this.field_3223 = this.field_3310;
                    this.method_2653(par1DamageSource, par2);
                    this.field_3297 = this.field_3298 = 10;
                }

                this.field_3299 = 0.0F;
                Entity var4 = par1DamageSource.getAttacker();
                if (var4 != null) {
                    if (var4 instanceof MobEntity) {
                        this.method_2650((MobEntity)var4);
                    }

                    if (var4 instanceof PlayerEntity) {
                        this.field_3332 = 60;
                        this.field_3331 = (PlayerEntity)var4;
                    } else if (var4 instanceof WolfEntity) {
                        WolfEntity var5 = (WolfEntity)var4;
                        if (var5.isTamed()) {
                            this.field_3332 = 60;
                            this.field_3331 = null;
                        }
                    }
                }

                if (var3) {
                    this.world.sendEntityStatus(this, (byte)2);
                    if (par1DamageSource != DamageSource.DROWN && par1DamageSource != DamageSource.EXPLOSION) {
                        this.scheduleVelocityUpdate();
                    }

                    if (var4 != null) {
                        double var9 = var4.x - this.x;

                        double var7;
                        for(var7 = var4.z - this.z; var9 * var9 + var7 * var7 < 1.0E-4; var7 = (Math.random() - Math.random()) * 0.01) {
                            var9 = (Math.random() - Math.random()) * 0.01;
                        }

                        this.field_3299 = (float)(Math.atan2(var7, var9) * 180.0 / Math.PI) - this.yaw;
                        this.method_2584(var4, par2, var9, var7);
                    } else {
                        this.field_3299 = (float)((int)(Math.random() * 2.0) * 180);
                    }
                }

                if (this.field_3294 <= 0) {
                    if (var3) {
                        this.world.playSound(this, this.method_2605(), this.method_2602(), this.method_2665());
                    }

                    this.dropInventory(par1DamageSource);
                } else if (var3) {
                    this.world.playSound(this, this.method_2604(), this.method_2602(), this.method_2665());
                }

                return true;
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void method_2653(DamageSource par1DamageSource, int par2) {
        par2 = ForgeHooks.onLivingHurt((MobEntity)(Object) this, par1DamageSource, par2);
        if (par2 > 0) {
            par2 = this.method_2626(par1DamageSource, par2);
            par2 = this.method_2648(par1DamageSource, par2);
            this.field_3294 -= par2;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void dropInventory(DamageSource par1DamageSource) {
        if (!ForgeHooks.onLivingDeath((MobEntity)(Object) this, par1DamageSource)) {
            Entity var2 = par1DamageSource.getAttacker();
            if (this.field_3287 >= 0 && var2 != null) {
                var2.updateKilledAdvancementCriterion(this, this.field_3287);
            }

            if (var2 != null) {
                var2.method_2501((MobEntity)(Object) this);
            }

            this.field_3304 = true;
            if (!this.world.isClient) {
                int var3 = 0;
                if (var2 instanceof PlayerEntity) {
                    var3 = EnchantmentHelper.method_4655((MobEntity)var2);
                }

                this.captureDrops(true);
                this.getCapturedDrops().clear();
                int var4 = 0;
                if (!this.method_2662() && this.world.getGameRules().getBoolean("doMobLoot")) {
                    this.method_2587(this.field_3332 > 0, var3);
                    this.method_4472(this.field_3332 > 0, var3);
                    if (this.field_3332 > 0) {
                        var4 = this.random.nextInt(200) - var3;
                        if (var4 < 5) {
                            this.method_2672(var4 <= 0 ? 1 : 0);
                        }
                    }
                }

                this.captureDrops(false);
                if (!ForgeHooks.onLivingDrops((MobEntity)(Object) this, par1DamageSource, this.getCapturedDrops(), var3, this.field_3332 > 0, var4)) {
                    for(ItemEntity item : this.getCapturedDrops()) {
                        this.world.spawnEntity(item);
                    }
                }
            }

            this.world.sendEntityStatus(this, (byte)3);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void method_2490(float par1) {
        par1 = ForgeHooks.onLivingFall((MobEntity)(Object) this, par1);
        if (!(par1 <= 0.0F)) {
            super.method_2490(par1);
            int var2 = MathHelper.ceil(par1 - 3.0F);
            if (var2 > 0) {
                if (var2 > 4) {
                    this.world.playSound(this, "damage.fallbig", 1.0F, 1.0F);
                } else {
                    this.world.playSound(this, "damage.fallsmall", 1.0F, 1.0F);
                }

                this.damage(DamageSource.FALL, var2);
                int var3 = this.world.getBlock(MathHelper.floor(this.x), MathHelper.floor(this.y - 0.2F - (double)this.heightOffset), MathHelper.floor(this.z));
                if (var3 > 0) {
                    BlockSoundGroup var4 = Block.BLOCKS[var3].soundGroup;
                    this.world.playSound(this, var4.method_487(), var4.method_485() * 0.5F, var4.method_486() * 0.75F);
                }
            }

        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_2660() {
        int var1 = MathHelper.floor(this.x);
        int var2 = MathHelper.floor(this.boundingBox.minY);
        int var3 = MathHelper.floor(this.z);
        int var4 = this.world.getBlock(var1, var2, var3);
        return ForgeHooks.isLivingOnLadder(Block.BLOCKS[var4], this.world, var1, var2, var3);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void method_2612() {
        this.velocityY = 0.42F;
        if (this.method_2581(StatusEffect.JUMP_BOOST)) {
            this.velocityY += (double)((float)(this.method_2627(StatusEffect.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting()) {
            float var1 = this.yaw * (float) (Math.PI / 180.0);
            this.velocityX -= (double)(MathHelper.sin(var1) * 0.2F);
            this.velocityZ += (double)(MathHelper.cos(var1) * 0.2F);
        }

        this.velocityDirty = true;
        ForgeHooks.onLivingJump((MobEntity)(Object) this);
    }

    @Override
    public void method_2674(int par1) {
        this.field_3335.remove(par1);
    }

    @Override
    public void curePotionEffects(ItemStack curativeItem) {
        Iterator<Integer> potionKey = this.field_3335.keySet().iterator();
        if (!this.world.isClient) {
            while(potionKey.hasNext()) {
                Integer key = (Integer)potionKey.next();
                StatusEffectInstance effect = (StatusEffectInstance)this.field_3335.get(key);
                if (effect.isCurativeItem(curativeItem)) {
                    potionKey.remove();
                    this.method_2649(effect);
                }
            }

        }
    }

    @Override
    public float getField_3344() {
        return this.field_3344;
    }

    @Override
    public void setField_3344(float field_3344) {
        this.field_3344 = field_3344;
    }
}
