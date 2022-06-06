package fr.catcore.fabricatedforge.mixin.forgefml.entity;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import net.minecraft.class_5114;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.thrown.*;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.class_642;
import net.minecraft.network.class_665;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Set;

@Mixin(TrackedEntityInstance.class)
public abstract class TrackedEntityInstanceMixin {

    @Shadow public Entity trackedEntity;

    @Shadow public int serializedX;

    @Shadow public int serializedZ;

    @Shadow public int trackingDistance;

    @Shadow protected abstract boolean method_2187(ServerPlayerEntity player);

    @Shadow public Set players;

    @Shadow public double velocityX;

    @Shadow public double velocityY;

    @Shadow public double velocityZ;

    @Shadow public int serializedY;

    @Shadow private boolean trackVelocity;

    @Shadow public int headRotationYaw;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_2184(ServerPlayerEntity par1EntityPlayerMP) {
        if (par1EntityPlayerMP != this.trackedEntity) {
            double var2 = par1EntityPlayerMP.x - (double)(this.serializedX / 32);
            double var4 = par1EntityPlayerMP.z - (double)(this.serializedZ / 32);
            if (var2 >= (double)(-this.trackingDistance)
                    && var2 <= (double)this.trackingDistance
                    && var4 >= (double)(-this.trackingDistance)
                    && var4 <= (double)this.trackingDistance) {
                if (!this.players.contains(par1EntityPlayerMP) && this.method_2187(par1EntityPlayerMP)) {
                    this.players.add(par1EntityPlayerMP);
                    Packet var6 = this.method_2182();
                    par1EntityPlayerMP.field_2823.sendPacket(var6);
                    if (this.trackedEntity instanceof ItemFrameEntity) {
                        par1EntityPlayerMP.field_2823
                                .sendPacket(new EntityTrackerUpdateS2CPacket(this.trackedEntity.id, this.trackedEntity.getDataTracker(), true));
                    }

                    this.velocityX = this.trackedEntity.velocityX;
                    this.velocityY = this.trackedEntity.velocityY;
                    this.velocityZ = this.trackedEntity.velocityZ;
                    int posX = MathHelper.floor(this.trackedEntity.x * 32.0);
                    int posY = MathHelper.floor(this.trackedEntity.y * 32.0);
                    int posZ = MathHelper.floor(this.trackedEntity.z * 32.0);
                    if (posX != this.serializedX || posY != this.serializedY || posZ != this.serializedZ) {
                        FMLNetworkHandler.makeEntitySpawnAdjustment(
                                this.trackedEntity.id, par1EntityPlayerMP, this.serializedX, this.serializedY, this.serializedZ
                        );
                    }

                    if (this.trackVelocity && !(var6 instanceof class_642)) {
                        par1EntityPlayerMP.field_2823
                                .sendPacket(
                                        new EntityVelocityChangeS2CPacket(
                                                this.trackedEntity.id, this.trackedEntity.velocityX, this.trackedEntity.velocityY, this.trackedEntity.velocityZ
                                        )
                                );
                    }

                    if (this.trackedEntity.vehicle != null) {
                        par1EntityPlayerMP.field_2823.sendPacket(new EntityAttachS2CPacket(this.trackedEntity, this.trackedEntity.vehicle));
                    }

                    if (this.trackedEntity instanceof MobEntity) {
                        for(int var7 = 0; var7 < 5; ++var7) {
                            ItemStack var8 = ((MobEntity)this.trackedEntity).method_4484(var7);
                            if (var8 != null) {
                                par1EntityPlayerMP.field_2823.sendPacket(new EntityEquipmentUpdate_S2CPacket(this.trackedEntity.id, var7, var8));
                            }
                        }
                    }

                    if (this.trackedEntity instanceof PlayerEntity) {
                        PlayerEntity var11 = (PlayerEntity)this.trackedEntity;
                        if (var11.method_2641()) {
                            par1EntityPlayerMP.field_2823
                                    .sendPacket(
                                            new class_665(
                                                    this.trackedEntity,
                                                    0,
                                                    MathHelper.floor(this.trackedEntity.x),
                                                    MathHelper.floor(this.trackedEntity.y),
                                                    MathHelper.floor(this.trackedEntity.z)
                                            )
                                    );
                        }
                    }

                    if (this.trackedEntity instanceof MobEntity) {
                        MobEntity var10 = (MobEntity)this.trackedEntity;

                        for(StatusEffectInstance var9 : (Collection<StatusEffectInstance>) var10.method_2644()) {
                            par1EntityPlayerMP.field_2823.sendPacket(new EntityStatusEffect_S2CPacket(this.trackedEntity.id, var9));
                        }
                    }
                }
            } else if (this.players.contains(par1EntityPlayerMP)) {
                this.players.remove(par1EntityPlayerMP);
                par1EntityPlayerMP.removedEntities.add(this.trackedEntity.id);
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private Packet method_2182() {
        if (this.trackedEntity.removed) {
            System.out.println("Fetching addPacket for removed entity");
        }

        Packet pkt = FMLNetworkHandler.getEntitySpawningPacket(this.trackedEntity);
        if (pkt != null) {
            return pkt;
        } else if (this.trackedEntity instanceof ItemEntity) {
            ItemEntity var9 = (ItemEntity)this.trackedEntity;
            class_5114 var10 = new class_5114(var9);
            var9.x = (double)var10.field_23078 / 32.0;
            var9.y = (double)var10.field_23079 / 32.0;
            var9.z = (double)var10.field_23080 / 32.0;
            return var10;
        } else if (this.trackedEntity instanceof ServerPlayerEntity) {
            return new PlayerSpawn_S2CPacket((PlayerEntity)this.trackedEntity);
        } else {
            if (this.trackedEntity instanceof AbstractMinecartEntity) {
                AbstractMinecartEntity var1 = (AbstractMinecartEntity)this.trackedEntity;
                if (var1.field_3897 == 0) {
                    return new EntitySpawn_S2CPacket(this.trackedEntity, 10);
                }

                if (var1.field_3897 == 1) {
                    return new EntitySpawn_S2CPacket(this.trackedEntity, 11);
                }

                if (var1.field_3897 == 2) {
                    return new EntitySpawn_S2CPacket(this.trackedEntity, 12);
                }
            }

            if (this.trackedEntity instanceof BoatEntity) {
                return new EntitySpawn_S2CPacket(this.trackedEntity, 1);
            } else if (this.trackedEntity instanceof EntityCategoryProvider || this.trackedEntity instanceof EnderDragonEntity) {
                this.headRotationYaw = MathHelper.floor(this.trackedEntity.getHeadRotation() * 256.0F / 360.0F);
                return new class_642((MobEntity)this.trackedEntity);
            } else if (this.trackedEntity instanceof FishingBobberEntity) {
                PlayerEntity var8 = ((FishingBobberEntity)this.trackedEntity).thrower;
                return new EntitySpawn_S2CPacket(this.trackedEntity, 90, var8 != null ? var8.id : this.trackedEntity.id);
            } else if (this.trackedEntity instanceof AbstractArrowEntity) {
                Entity var7 = ((AbstractArrowEntity)this.trackedEntity).owner;
                return new EntitySpawn_S2CPacket(this.trackedEntity, 60, var7 != null ? var7.id : this.trackedEntity.id);
            } else if (this.trackedEntity instanceof SnowballEntity) {
                return new EntitySpawn_S2CPacket(this.trackedEntity, 61);
            } else if (this.trackedEntity instanceof PotionEntity) {
                return new EntitySpawn_S2CPacket(this.trackedEntity, 73, ((PotionEntity)this.trackedEntity).method_3237());
            } else if (this.trackedEntity instanceof ExperienceBottleEntity) {
                return new EntitySpawn_S2CPacket(this.trackedEntity, 75);
            } else if (this.trackedEntity instanceof EnderPearlEntity) {
                return new EntitySpawn_S2CPacket(this.trackedEntity, 65);
            } else if (this.trackedEntity instanceof EyeOfEnderEntity) {
                return new EntitySpawn_S2CPacket(this.trackedEntity, 72);
            } else if (this.trackedEntity instanceof ExplosiveProjectileEntity) {
                ExplosiveProjectileEntity var6 = (ExplosiveProjectileEntity)this.trackedEntity;
                EntitySpawn_S2CPacket var2 = null;
                byte var3 = 63;
                if (this.trackedEntity instanceof SmallFireballEntity) {
                    var3 = 64;
                } else if (this.trackedEntity instanceof WitherSkullEntity) {
                    var3 = 66;
                }

                if (var6.field_4040 != null) {
                    var2 = new EntitySpawn_S2CPacket(this.trackedEntity, var3, ((ExplosiveProjectileEntity)this.trackedEntity).field_4040.id);
                } else {
                    var2 = new EntitySpawn_S2CPacket(this.trackedEntity, var3, 0);
                }

                var2.velocityX = (int)(var6.powerX * 8000.0);
                var2.velocityY = (int)(var6.powerY * 8000.0);
                var2.velocityZ = (int)(var6.powerZ * 8000.0);
                return var2;
            } else if (this.trackedEntity instanceof EggEntity) {
                return new EntitySpawn_S2CPacket(this.trackedEntity, 62);
            } else if (this.trackedEntity instanceof TntEntity) {
                return new EntitySpawn_S2CPacket(this.trackedEntity, 50);
            } else if (this.trackedEntity instanceof EndCrystalEntity) {
                return new EntitySpawn_S2CPacket(this.trackedEntity, 51);
            } else if (this.trackedEntity instanceof FallingBlockEntity) {
                FallingBlockEntity var5 = (FallingBlockEntity)this.trackedEntity;
                return new EntitySpawn_S2CPacket(this.trackedEntity, 70, var5.field_3888 | var5.field_3889 << 16);
            } else if (this.trackedEntity instanceof PaintingEntity) {
                return new PaintingSpawn_S2CPacket((PaintingEntity)this.trackedEntity);
            } else if (this.trackedEntity instanceof ItemFrameEntity) {
                ItemFrameEntity var4 = (ItemFrameEntity)this.trackedEntity;
                EntitySpawn_S2CPacket var2 = new EntitySpawn_S2CPacket(this.trackedEntity, 71, var4.field_5328);
                var2.x = MathHelper.floor((float)(var4.field_5329 * 32));
                var2.y = MathHelper.floor((float)(var4.field_5330 * 32));
                var2.z = MathHelper.floor((float)(var4.field_5331 * 32));
                return var2;
            } else if (this.trackedEntity instanceof ExperienceOrbEntity) {
                return new ExperienceOrbSpawn_S2CPacket((ExperienceOrbEntity)this.trackedEntity);
            } else {
                throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
            }
        }
    }
}
