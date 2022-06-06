package fr.catcore.fabricatedforge.mixin.forgefml.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.thrown.*;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(EntityTracker.class)
public abstract class EntityTrackerMixin {

    @Shadow public abstract void startTracking(Entity entity, int i, int j);

    @Shadow public abstract void startTracking(Entity entity, int i, int j, boolean bl);

    @Shadow private Set trackedEntities;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void startTracking(Entity par1Entity) {
        if (!EntityRegistry.instance().tryTrackingEntity((EntityTracker)(Object) this, par1Entity)) {
            if (par1Entity instanceof ServerPlayerEntity) {
                this.startTracking(par1Entity, 512, 2);
                ServerPlayerEntity var2 = (ServerPlayerEntity)par1Entity;

                for(TrackedEntityInstance var4 : (Set<TrackedEntityInstance>) this.trackedEntities) {
                    if (var4.trackedEntity != var2) {
                        var4.method_2184(var2);
                    }
                }
            } else if (par1Entity instanceof FishingBobberEntity) {
                this.startTracking(par1Entity, 64, 5, true);
            } else if (par1Entity instanceof AbstractArrowEntity) {
                this.startTracking(par1Entity, 64, 20, false);
            } else if (par1Entity instanceof SmallFireballEntity) {
                this.startTracking(par1Entity, 64, 10, false);
            } else if (par1Entity instanceof ExplosiveProjectileEntity) {
                this.startTracking(par1Entity, 64, 10, false);
            } else if (par1Entity instanceof SnowballEntity) {
                this.startTracking(par1Entity, 64, 10, true);
            } else if (par1Entity instanceof EnderPearlEntity) {
                this.startTracking(par1Entity, 64, 10, true);
            } else if (par1Entity instanceof EyeOfEnderEntity) {
                this.startTracking(par1Entity, 64, 4, true);
            } else if (par1Entity instanceof EggEntity) {
                this.startTracking(par1Entity, 64, 10, true);
            } else if (par1Entity instanceof PotionEntity) {
                this.startTracking(par1Entity, 64, 10, true);
            } else if (par1Entity instanceof ExperienceBottleEntity) {
                this.startTracking(par1Entity, 64, 10, true);
            } else if (par1Entity instanceof ItemEntity) {
                this.startTracking(par1Entity, 64, 20, true);
            } else if (par1Entity instanceof AbstractMinecartEntity) {
                this.startTracking(par1Entity, 80, 3, true);
            } else if (par1Entity instanceof BoatEntity) {
                this.startTracking(par1Entity, 80, 3, true);
            } else if (par1Entity instanceof SquidEntity) {
                this.startTracking(par1Entity, 64, 3, true);
            } else if (par1Entity instanceof WitherEntity) {
                this.startTracking(par1Entity, 80, 3, false);
            } else if (par1Entity instanceof BatEntity) {
                this.startTracking(par1Entity, 80, 3, false);
            } else if (par1Entity instanceof EntityCategoryProvider) {
                this.startTracking(par1Entity, 80, 3, true);
            } else if (par1Entity instanceof EnderDragonEntity) {
                this.startTracking(par1Entity, 160, 3, true);
            } else if (par1Entity instanceof TntEntity) {
                this.startTracking(par1Entity, 160, 10, true);
            } else if (par1Entity instanceof FallingBlockEntity) {
                this.startTracking(par1Entity, 160, 20, true);
            } else if (par1Entity instanceof PaintingEntity) {
                this.startTracking(par1Entity, 160, Integer.MAX_VALUE, false);
            } else if (par1Entity instanceof ExperienceOrbEntity) {
                this.startTracking(par1Entity, 160, 20, true);
            } else if (par1Entity instanceof EndCrystalEntity) {
                this.startTracking(par1Entity, 256, Integer.MAX_VALUE, false);
            } else if (par1Entity instanceof ItemFrameEntity) {
                this.startTracking(par1Entity, 160, Integer.MAX_VALUE, false);
            }

        }
    }
}
