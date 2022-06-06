package fr.catcore.fabricatedforge.mixin.forgefml.entity.vehicle;

import fr.catcore.fabricatedforge.mixininterface.IAbstractMinecartEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecartRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static fr.catcore.fabricatedforge.forged.ReflectionUtils.*;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements Inventory, IAbstractMinecartEntity {
    @Shadow public abstract void setDamageWobbleSide(int wobbleSide);

    @Shadow public abstract int getDamageWobbleSide();

    @Shadow public abstract void setDamageWobbleTicks(int wobbleTicks);

    @Shadow public abstract int method_3065();

    @Shadow public abstract void method_3061(int i);

    @Shadow public abstract int getDamageWobbleTicks();

    @Shadow
    public abstract boolean method_3063();

    @Shadow public int field_3897;

    @Shadow public int clientInterpolationSteps;

    @Shadow public double clientX;

    @Shadow public double clientY;

    @Shadow public double clientZ;

    @Shadow public double clientYaw;

    @Shadow public double clientPitch;

    @Shadow @Final public static int[][][] ADJACENT_RAIL_POSITIONS;

    @Shadow public boolean yawFlipped;

    @Shadow public double field_3904;

    @Shadow public double field_3905;

    @Shadow public int field_3907;

    @Shadow public ItemStack[] field_3906;

    @Shadow protected abstract void method_3062(boolean bl);

    @Shadow public boolean field_5384;

    @Shadow @Final public Tickable field_5383;

    public AbstractMinecartEntityMixin(World world) {
        super(world);
    }

    // Forge Field
    @Unique
    protected boolean canUseRail = true;
    @Unique
    protected boolean canBePushed = true;
    @Unique
    protected float maxSpeedRail;
    @Unique
    protected float maxSpeedGround;
    @Unique
    protected float maxSpeedAirLateral;
    @Unique
    protected float maxSpeedAirVertical;
    @Unique
    protected double dragAir;

    @Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void fmlCtr(World par1, CallbackInfo ci) {
        this.field_3906 = new ItemStack[36];
        this.field_3907 = 0;
        this.yawFlipped = false;
        this.field_5384 = true;

        this.maxSpeedRail = AbstractMinecartEntity_defaultMaxSpeedRail;
        this.maxSpeedGround = AbstractMinecartEntity_defaultMaxSpeedGround;
        this.maxSpeedAirLateral = AbstractMinecartEntity_defaultMaxSpeedAirLateral;
        this.maxSpeedAirVertical = AbstractMinecartEntity_defaultMaxSpeedAirVertical;
        this.dragAir = AbstractMinecartEntity_defaultDragAir;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public Box getHardCollisionBox(Entity par1Entity) {
        return AbstractMinecartEntity_getCollisionHandler() != null ? AbstractMinecartEntity_getCollisionHandler().getCollisionBox((AbstractMinecartEntity)(Object) this, par1Entity) : par1Entity.boundingBox;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public Box getBox() {
        return AbstractMinecartEntity_getCollisionHandler() != null ? AbstractMinecartEntity_getCollisionHandler().getBoundingBox((AbstractMinecartEntity)(Object) this) : null;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean isPushable() {
        return this.canBePushed;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean damage(DamageSource par1DamageSource, int par2) {
        if (!this.world.isClient && !this.removed) {
            this.setDamageWobbleSide(-this.getDamageWobbleSide());
            this.setDamageWobbleTicks(10);
            this.scheduleVelocityUpdate();
            this.method_3061(this.method_3065() + par2 * 10);
            if (par1DamageSource.getAttacker() instanceof PlayerEntity && ((PlayerEntity)par1DamageSource.getAttacker()).abilities.creativeMode) {
                this.method_3061(100);
            }

            if (this.method_3065() > 40) {
                if (this.rider != null) {
                    this.rider.startRiding(this);
                }

                this.remove();
                this.dropCartAsItem();
            }

            return true;
        } else {
            return true;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void tick() {
        if (this.field_5383 != null) {
            this.field_5383.tick();
        }

        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }

        if (this.method_3065() > 0) {
            this.method_3061(this.method_3065() - 1);
        }

        if (this.y < -64.0) {
            this.destroy();
        }

        if (this.method_3063() && this.random.nextInt(4) == 0 && this.field_3897 == 2 && ((Object) this.getClass()) == AbstractMinecartEntity.class) {
            this.world.method_3621("largesmoke", this.x, this.y + 0.8, this.z, 0.0, 0.0, 0.0);
        }

        if (this.world.isClient) {
            if (this.clientInterpolationSteps > 0) {
                double var45 = this.x + (this.clientX - this.x) / (double)this.clientInterpolationSteps;
                double var46 = this.y + (this.clientY - this.y) / (double)this.clientInterpolationSteps;
                double var5 = this.z + (this.clientZ - this.z) / (double)this.clientInterpolationSteps;
                double var7 = MathHelper.wrapDegrees(this.clientYaw - (double)this.yaw);
                this.yaw = (float)((double)this.yaw + var7 / (double)this.clientInterpolationSteps);
                this.pitch = (float)((double)this.pitch + (this.clientPitch - (double)this.pitch) / (double)this.clientInterpolationSteps);
                --this.clientInterpolationSteps;
                this.updatePosition(var45, var46, var5);
                this.setRotation(this.yaw, this.pitch);
            } else {
                this.updatePosition(this.x, this.y, this.z);
                this.setRotation(this.yaw, this.pitch);
            }
        } else {
            this.prevX = this.x;
            this.prevY = this.y;
            this.prevZ = this.z;
            this.velocityY -= 0.04F;
            int var1 = MathHelper.floor(this.x);
            int var2 = MathHelper.floor(this.y);
            int var3 = MathHelper.floor(this.z);
            if (RailBlock.method_355(this.world, var1, var2 - 1, var3)) {
                --var2;
            }

            double var4 = 0.4;
            double var6 = 0.0078125;
            int var8 = this.world.getBlock(var1, var2, var3);
            if (this.canUseRail() && RailBlock.method_354(var8)) {
                Vec3d var9 = this.snapPositionToRail(this.x, this.y, this.z);
                int var10 = ((RailBlock)Block.BLOCKS[var8]).getBasicRailMetadata(this.world, (AbstractMinecartEntity)(Object) this, var1, var2, var3);
                this.y = (double)var2;
                boolean var11 = false;
                boolean var12 = false;
                if (var8 == Block.POWERED_RAIL.id) {
                    var11 = (this.world.getBlockData(var1, var2, var3) & 8) != 0;
                    var12 = !var11;
                }

                if (((RailBlock)Block.BLOCKS[var8]).method_356()) {
                    var10 &= 7;
                }

                if (var10 >= 2 && var10 <= 5) {
                    this.y = (double)(var2 + 1);
                }

                this.adjustSlopeVelocities(var10);
                int[][] var13 = ADJACENT_RAIL_POSITIONS[var10];
                double var14 = (double)(var13[1][0] - var13[0][0]);
                double var16 = (double)(var13[1][2] - var13[0][2]);
                double var18 = Math.sqrt(var14 * var14 + var16 * var16);
                double var20 = this.velocityX * var14 + this.velocityZ * var16;
                if (var20 < 0.0) {
                    var14 = -var14;
                    var16 = -var16;
                }

                double var22 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
                this.velocityX = var22 * var14 / var18;
                this.velocityZ = var22 * var16 / var18;
                if (this.rider != null) {
                    double var24 = this.rider.velocityX * this.rider.velocityX + this.rider.velocityZ * this.rider.velocityZ;
                    double var26 = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
                    if (var24 > 1.0E-4 && var26 < 0.01) {
                        this.velocityX += this.rider.velocityX * 0.1;
                        this.velocityZ += this.rider.velocityZ * 0.1;
                        var12 = false;
                    }
                }

                if (var12 && this.shouldDoRailFunctions()) {
                    double var24 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
                    if (var24 < 0.03) {
                        this.velocityX *= 0.0;
                        this.velocityY *= 0.0;
                        this.velocityZ *= 0.0;
                    } else {
                        this.velocityX *= 0.5;
                        this.velocityY *= 0.0;
                        this.velocityZ *= 0.5;
                    }
                }

                double var24 = 0.0;
                double var26 = (double)var1 + 0.5 + (double)var13[0][0] * 0.5;
                double var28 = (double)var3 + 0.5 + (double)var13[0][2] * 0.5;
                double var30 = (double)var1 + 0.5 + (double)var13[1][0] * 0.5;
                double var32 = (double)var3 + 0.5 + (double)var13[1][2] * 0.5;
                var14 = var30 - var26;
                var16 = var32 - var28;
                if (var14 == 0.0) {
                    this.x = (double)var1 + 0.5;
                    var24 = this.z - (double)var3;
                } else if (var16 == 0.0) {
                    this.z = (double)var3 + 0.5;
                    var24 = this.x - (double)var1;
                } else {
                    double var34 = this.x - var26;
                    double var36 = this.z - var28;
                    var24 = (var34 * var14 + var36 * var16) * 2.0;
                }

                this.x = var26 + var14 * var24;
                this.z = var28 + var16 * var24;
                this.updatePosition(this.x, this.y + (double)this.heightOffset, this.z);
                this.moveMinecartOnRail(var1, var2, var3);
                if (var13[0][1] != 0 && MathHelper.floor(this.x) - var1 == var13[0][0] && MathHelper.floor(this.z) - var3 == var13[0][2]) {
                    this.updatePosition(this.x, this.y + (double)var13[0][1], this.z);
                } else if (var13[1][1] != 0 && MathHelper.floor(this.x) - var1 == var13[1][0] && MathHelper.floor(this.z) - var3 == var13[1][2]) {
                    this.updatePosition(this.x, this.y + (double)var13[1][1], this.z);
                }

                this.applyDragAndPushForces();
                Vec3d var52 = this.snapPositionToRail(this.x, this.y, this.z);
                if (var52 != null && var9 != null) {
                    double var39 = (var9.y - var52.y) * 0.05;
                    var22 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
                    if (var22 > 0.0) {
                        this.velocityX = this.velocityX / var22 * (var22 + var39);
                        this.velocityZ = this.velocityZ / var22 * (var22 + var39);
                    }

                    this.updatePosition(this.x, var52.y, this.z);
                }

                int var51 = MathHelper.floor(this.x);
                int var53 = MathHelper.floor(this.z);
                if (var51 != var1 || var53 != var3) {
                    var22 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
                    this.velocityX = var22 * (double)(var51 - var1);
                    this.velocityZ = var22 * (double)(var53 - var3);
                }

                this.updatePushForces();
                if (this.shouldDoRailFunctions()) {
                    ((RailBlock)Block.BLOCKS[var8]).onMinecartPass(this.world, (AbstractMinecartEntity)(Object) this, var1, var2, var3);
                }

                if (var11 && this.shouldDoRailFunctions()) {
                    double var41 = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
                    if (var41 > 0.01) {
                        double var43 = 0.06;
                        this.velocityX += this.velocityX / var41 * var43;
                        this.velocityZ += this.velocityZ / var41 * var43;
                    } else if (var10 == 1) {
                        if (this.world.method_3783(var1 - 1, var2, var3)) {
                            this.velocityX = 0.02;
                        } else if (this.world.method_3783(var1 + 1, var2, var3)) {
                            this.velocityX = -0.02;
                        }
                    } else if (var10 == 0) {
                        if (this.world.method_3783(var1, var2, var3 - 1)) {
                            this.velocityZ = 0.02;
                        } else if (this.world.method_3783(var1, var2, var3 + 1)) {
                            this.velocityZ = -0.02;
                        }
                    }
                }
            } else {
                this.moveMinecartOffRail(var1, var2, var3);
            }

            this.checkBlockCollision();
            this.pitch = 0.0F;
            double var47 = this.prevX - this.x;
            double var48 = this.prevZ - this.z;
            if (var47 * var47 + var48 * var48 > 0.001) {
                this.yaw = (float)(Math.atan2(var48, var47) * 180.0 / Math.PI);
                if (this.yawFlipped) {
                    this.yaw += 180.0F;
                }
            }

            double var49 = (double)MathHelper.wrapDegrees(this.yaw - this.prevYaw);
            if (var49 < -170.0 || var49 >= 170.0) {
                this.yaw += 180.0F;
                this.yawFlipped = !this.yawFlipped;
            }

            this.setRotation(this.yaw, this.pitch);
            Box box = null;
            if (AbstractMinecartEntity_getCollisionHandler() != null) {
                box = AbstractMinecartEntity_getCollisionHandler().getMinecartCollisionBox((AbstractMinecartEntity)(Object) this);
            } else {
                box = this.boundingBox.expand(0.2, 0.0, 0.2);
            }

            List var15 = this.world.getEntitiesIn(this, box);
            if (var15 != null && !var15.isEmpty()) {
                for(int var50 = 0; var50 < var15.size(); ++var50) {
                    Entity var17 = (Entity)var15.get(var50);
                    if (var17 != this.rider && var17.isPushable() && var17 instanceof AbstractMinecartEntity) {
                        var17.pushAwayFrom(this);
                    }
                }
            }

            if (this.rider != null && this.rider.removed) {
                if (this.rider.vehicle == this) {
                    this.rider.vehicle = null;
                }

                this.rider = null;
            }

            this.updateFuel();
            MinecraftForge.EVENT_BUS.post(new MinecartUpdateEvent((AbstractMinecartEntity)(Object) this, (float)var1, (float)var2, (float)var3));
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public Vec3d snapPositionToRailWithOffset(double par1, double par3, double par5, double par7) {
        int var9 = MathHelper.floor(par1);
        int var10 = MathHelper.floor(par3);
        int var11 = MathHelper.floor(par5);
        if (RailBlock.method_355(this.world, var9, var10 - 1, var11)) {
            --var10;
        }

        int var12 = this.world.getBlock(var9, var10, var11);
        if (!RailBlock.method_354(var12)) {
            return null;
        } else {
            int var13 = ((RailBlock)Block.BLOCKS[var12]).getBasicRailMetadata(this.world, (AbstractMinecartEntity)(Object) this, var9, var10, var11);
            par3 = (double)var10;
            if (var13 >= 2 && var13 <= 5) {
                par3 = (double)(var10 + 1);
            }

            int[][] var14 = ADJACENT_RAIL_POSITIONS[var13];
            double var15 = (double)(var14[1][0] - var14[0][0]);
            double var17 = (double)(var14[1][2] - var14[0][2]);
            double var19 = Math.sqrt(var15 * var15 + var17 * var17);
            var15 /= var19;
            var17 /= var19;
            par1 += var15 * par7;
            par5 += var17 * par7;
            if (var14[0][1] != 0 && MathHelper.floor(par1) - var9 == var14[0][0] && MathHelper.floor(par5) - var11 == var14[0][2]) {
                par3 += (double)var14[0][1];
            } else if (var14[1][1] != 0 && MathHelper.floor(par1) - var9 == var14[1][0] && MathHelper.floor(par5) - var11 == var14[1][2]) {
                par3 += (double)var14[1][1];
            }

            return this.snapPositionToRail(par1, par3, par5);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public Vec3d snapPositionToRail(double par1, double par3, double par5) {
        int var7 = MathHelper.floor(par1);
        int var8 = MathHelper.floor(par3);
        int var9 = MathHelper.floor(par5);
        if (RailBlock.method_355(this.world, var7, var8 - 1, var9)) {
            --var8;
        }

        int var10 = this.world.getBlock(var7, var8, var9);
        if (RailBlock.method_354(var10)) {
            int var11 = ((RailBlock)Block.BLOCKS[var10]).getBasicRailMetadata(this.world, (AbstractMinecartEntity)(Object) this, var7, var8, var9);
            par3 = (double)var8;
            if (var11 >= 2 && var11 <= 5) {
                par3 = (double)(var8 + 1);
            }

            int[][] var12 = ADJACENT_RAIL_POSITIONS[var11];
            double var13 = 0.0;
            double var15 = (double)var7 + 0.5 + (double)var12[0][0] * 0.5;
            double var17 = (double)var8 + 0.5 + (double)var12[0][1] * 0.5;
            double var19 = (double)var9 + 0.5 + (double)var12[0][2] * 0.5;
            double var21 = (double)var7 + 0.5 + (double)var12[1][0] * 0.5;
            double var23 = (double)var8 + 0.5 + (double)var12[1][1] * 0.5;
            double var25 = (double)var9 + 0.5 + (double)var12[1][2] * 0.5;
            double var27 = var21 - var15;
            double var29 = (var23 - var17) * 2.0;
            double var31 = var25 - var19;
            if (var27 == 0.0) {
                par1 = (double)var7 + 0.5;
                var13 = par5 - (double)var9;
            } else if (var31 == 0.0) {
                par5 = (double)var9 + 0.5;
                var13 = par1 - (double)var7;
            } else {
                double var33 = par1 - var15;
                double var35 = par5 - var19;
                var13 = (var33 * var27 + var35 * var31) * 2.0;
            }

            par1 = var15 + var27 * var13;
            par3 = var17 + var29 * var13;
            par5 = var19 + var31 * var13;
            if (var29 < 0.0) {
                ++par3;
            }

            if (var29 > 0.0) {
                par3 += 0.5;
            }

            return this.world.method_4696().getOrCreate(par1, par3, par5);
        } else {
            return null;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void writeCustomDataToNbt(NbtCompound par1NBTTagCompound) {
        par1NBTTagCompound.putInt("Type", this.field_3897);
        if (this.isPoweredCart()) {
            par1NBTTagCompound.putDouble("PushX", this.field_3904);
            par1NBTTagCompound.putDouble("PushZ", this.field_3905);
            par1NBTTagCompound.putInt("Fuel", this.field_3907);
        }

        if (this.getInvSize() > 0) {
            NbtList var2 = new NbtList();

            for(int var3 = 0; var3 < this.field_3906.length; ++var3) {
                if (this.field_3906[var3] != null) {
                    NbtCompound var4 = new NbtCompound();
                    var4.putByte("Slot", (byte)var3);
                    this.field_3906[var3].toNbt(var4);
                    var2.method_1217(var4);
                }
            }

            par1NBTTagCompound.put("Items", var2);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void readCustomDataFromNbt(NbtCompound par1NBTTagCompound) {
        this.field_3897 = par1NBTTagCompound.getInt("Type");
        if (this.isPoweredCart()) {
            this.field_3904 = par1NBTTagCompound.getDouble("PushX");
            this.field_3905 = par1NBTTagCompound.getDouble("PushZ");

            try {
                this.field_3907 = par1NBTTagCompound.getInt("Fuel");
            } catch (ClassCastException var6) {
                this.field_3907 = par1NBTTagCompound.getShort("Fuel");
            }
        }

        if (this.getInvSize() > 0) {
            NbtList var2 = par1NBTTagCompound.getList("Items");
            this.field_3906 = new ItemStack[this.getInvSize()];

            for(int var3 = 0; var3 < var2.size(); ++var3) {
                NbtCompound var4 = (NbtCompound)var2.method_1218(var3);
                int var5 = var4.getByte("Slot") & 255;
                if (var5 >= 0 && var5 < this.field_3906.length) {
                    this.field_3906[var5] = ItemStack.fromNbt(var4);
                }
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void pushAwayFrom(Entity par1Entity) {
        MinecraftForge.EVENT_BUS.post(new MinecartCollisionEvent((AbstractMinecartEntity)(Object) this, par1Entity));
        if (AbstractMinecartEntity_getCollisionHandler() != null) {
            AbstractMinecartEntity_getCollisionHandler().onEntityCollision((AbstractMinecartEntity)(Object) this, par1Entity);
        } else {
            if (!this.world.isClient && par1Entity != this.rider) {
                if (par1Entity instanceof MobEntity
                        && !(par1Entity instanceof PlayerEntity)
                        && !(par1Entity instanceof IronGolemEntity)
                        && this.canBeRidden()
                        && this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.01
                        && this.rider == null
                        && par1Entity.vehicle == null) {
                    par1Entity.startRiding(this);
                }

                double var2 = par1Entity.x - this.x;
                double var4 = par1Entity.z - this.z;
                double var6 = var2 * var2 + var4 * var4;
                if (var6 >= 1.0E-4F) {
                    var6 = (double)MathHelper.sqrt(var6);
                    var2 /= var6;
                    var4 /= var6;
                    double var8 = 1.0 / var6;
                    if (var8 > 1.0) {
                        var8 = 1.0;
                    }

                    var2 *= var8;
                    var4 *= var8;
                    var2 *= 0.1F;
                    var4 *= 0.1F;
                    var2 *= (double)(1.0F - this.pushSpeedReduction);
                    var4 *= (double)(1.0F - this.pushSpeedReduction);
                    var2 *= 0.5;
                    var4 *= 0.5;
                    if (par1Entity instanceof AbstractMinecartEntity) {
                        double var10 = par1Entity.x - this.x;
                        double var12 = par1Entity.z - this.z;
                        Vec3d var14 = this.world.method_4696().getOrCreate(var10, 0.0, var12).normalize();
                        Vec3d var15 = this.world
                                .method_4696()
                                .getOrCreate(
                                        (double)MathHelper.cos(this.yaw * (float) Math.PI / 180.0F), 0.0, (double)MathHelper.sin(this.yaw * (float) Math.PI / 180.0F)
                                )
                                .normalize();
                        double var16 = Math.abs(var14.dotProduct(var15));
                        if (var16 < 0.8F) {
                            return;
                        }

                        double var18 = par1Entity.velocityX + this.velocityX;
                        double var20 = par1Entity.velocityZ + this.velocityZ;
                        if (((AbstractMinecartEntity)par1Entity).isPoweredCart() && !this.isPoweredCart()) {
                            this.velocityX *= 0.2F;
                            this.velocityZ *= 0.2F;
                            this.addVelocity(par1Entity.velocityX - var2, 0.0, par1Entity.velocityZ - var4);
                            par1Entity.velocityX *= 0.95F;
                            par1Entity.velocityZ *= 0.95F;
                        } else if (!((AbstractMinecartEntity)par1Entity).isPoweredCart() && this.isPoweredCart()) {
                            par1Entity.velocityX *= 0.2F;
                            par1Entity.velocityZ *= 0.2F;
                            par1Entity.addVelocity(this.velocityX + var2, 0.0, this.velocityZ + var4);
                            this.velocityX *= 0.95F;
                            this.velocityZ *= 0.95F;
                        } else {
                            var18 /= 2.0;
                            var20 /= 2.0;
                            this.velocityX *= 0.2F;
                            this.velocityZ *= 0.2F;
                            this.addVelocity(var18 - var2, 0.0, var20 - var4);
                            par1Entity.velocityX *= 0.2F;
                            par1Entity.velocityZ *= 0.2F;
                            par1Entity.addVelocity(var18 + var2, 0.0, var20 + var4);
                        }
                    } else {
                        this.addVelocity(-var2, 0.0, -var4);
                        par1Entity.addVelocity(var2 / 4.0, 0.0, var4 / 4.0);
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
    public int getInvSize() {
        return this.field_3897 == 1 && ((Object)this.getClass()) == AbstractMinecartEntity.class ? 27 : 0;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_2537(PlayerEntity par1EntityPlayer) {
        if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent((AbstractMinecartEntity)(Object) this, par1EntityPlayer))) {
            return true;
        } else {
            if (this.canBeRidden()) {
                if (this.rider != null && this.rider instanceof PlayerEntity && this.rider != par1EntityPlayer) {
                    return true;
                }

                if (!this.world.isClient) {
                    par1EntityPlayer.startRiding(this);
                }
            } else if (this.getInvSize() > 0) {
                if (!this.world.isClient) {
                    par1EntityPlayer.openInventory(this);
                }
            } else if (this.field_3897 == 2 && ((Object)this.getClass()) == AbstractMinecartEntity.class) {
                ItemStack var2 = par1EntityPlayer.inventory.getMainHandStack();
                if (var2 != null && var2.id == Item.COAL.id) {
                    if (--var2.count == 0) {
                        par1EntityPlayer.inventory.setInvStack(par1EntityPlayer.inventory.selectedSlot, (ItemStack)null);
                    }

                    this.field_3907 += 3600;
                }

                this.field_3904 = this.x - par1EntityPlayer.x;
                this.field_3905 = this.z - par1EntityPlayer.z;
            }

            return true;
        }
    }

    @Override
    public void dropCartAsItem() {
        for(ItemStack item : this.getItemsDropped()) {
            this.dropItem(item, 0.0F);
        }

    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList();
        items.add(new ItemStack(Item.MINECART));
        switch(this.field_3897) {
            case 1:
                items.add(new ItemStack(Block.field_407));
                break;
            case 2:
                items.add(new ItemStack(Block.FURNACE));
        }

        return items;
    }

    @Override
    public ItemStack getCartItem() {
        return MinecartRegistry.getItemForCart((AbstractMinecartEntity)(Object) this);
    }

    @Override
    public boolean isPoweredCart() {
        return this.field_3897 == 2 && ((Object)this.getClass()) == AbstractMinecartEntity.class;
    }

    @Override
    public boolean isStorageCart() {
        return this.field_3897 == 1 && ((Object)this.getClass()) == AbstractMinecartEntity.class;
    }

    @Override
    public boolean canBeRidden() {
        return this.field_3897 == 0 && ((Object)this.getClass()) == AbstractMinecartEntity.class;
    }

    @Override
    public boolean canUseRail() {
        return this.canUseRail;
    }

    @Override
    public void setCanUseRail(boolean use) {
        this.canUseRail = use;
    }

    @Override
    public boolean shouldDoRailFunctions() {
        return true;
    }

    @Override
    public int getMinecartType() {
        return this.field_3897;
    }

    @Override
    public double getDrag() {
        return this.rider != null ? AbstractMinecartEntity_defaultDragRidden : AbstractMinecartEntity_defaultDragEmpty;
    }

    @Override
    public void applyDragAndPushForces() {
        if (this.isPoweredCart()) {
            double d27 = (double)MathHelper.sqrt(this.field_3904 * this.field_3904 + this.field_3905 * this.field_3905);
            if (d27 > 0.01) {
                this.field_3904 /= d27;
                this.field_3905 /= d27;
                double d29 = 0.04;
                this.velocityX *= 0.8;
                this.velocityY *= 0.0;
                this.velocityZ *= 0.8;
                this.velocityX += this.field_3904 * d29;
                this.velocityZ += this.field_3905 * d29;
            } else {
                this.velocityX *= 0.9;
                this.velocityY *= 0.0;
                this.velocityZ *= 0.9;
            }
        }

        this.velocityX *= this.getDrag();
        this.velocityY *= 0.0;
        this.velocityZ *= this.getDrag();
    }

    @Override
    public void updatePushForces() {
        if (this.isPoweredCart()) {
            double push = (double)MathHelper.sqrt(this.field_3904 * this.field_3904 + this.field_3905 * this.field_3905);
            if (push > 0.01 && this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.001) {
                this.field_3904 /= push;
                this.field_3905 /= push;
                if (this.field_3904 * this.velocityX + this.field_3905 * this.velocityZ < 0.0) {
                    this.field_3904 = 0.0;
                    this.field_3905 = 0.0;
                } else {
                    this.field_3904 = this.velocityX;
                    this.field_3905 = this.velocityZ;
                }
            }
        }

    }

    @Override
    public void moveMinecartOnRail(int i, int j, int k) {
        int id = this.world.getBlock(i, j, k);
        if (RailBlock.method_354(id)) {
            float railMaxSpeed = ((RailBlock)Block.BLOCKS[id]).getRailMaxSpeed(this.world, (AbstractMinecartEntity)(Object) this, i, j, k);
            double maxSpeed = (double)Math.min(railMaxSpeed, this.getMaxSpeedRail());
            double mX = this.velocityX;
            double mZ = this.velocityZ;
            if (this.rider != null) {
                mX *= 0.75;
                mZ *= 0.75;
            }

            if (mX < -maxSpeed) {
                mX = -maxSpeed;
            }

            if (mX > maxSpeed) {
                mX = maxSpeed;
            }

            if (mZ < -maxSpeed) {
                mZ = -maxSpeed;
            }

            if (mZ > maxSpeed) {
                mZ = maxSpeed;
            }

            this.move(mX, 0.0, mZ);
        }
    }

    @Override
    public void moveMinecartOffRail(int i, int j, int k) {
        double d2 = (double)this.getMaxSpeedGround();
        if (!this.onGround) {
            d2 = (double)this.getMaxSpeedAirLateral();
        }

        if (this.velocityX < -d2) {
            this.velocityX = -d2;
        }

        if (this.velocityX > d2) {
            this.velocityX = d2;
        }

        if (this.velocityZ < -d2) {
            this.velocityZ = -d2;
        }

        if (this.velocityZ > d2) {
            this.velocityZ = d2;
        }

        double moveY = this.velocityY;
        if (this.getMaxSpeedAirVertical() > 0.0F && this.velocityY > (double)this.getMaxSpeedAirVertical()) {
            moveY = (double)this.getMaxSpeedAirVertical();
            if (Math.abs(this.velocityX) < 0.3F && Math.abs(this.velocityZ) < 0.3F) {
                moveY = 0.15F;
                this.velocityY = moveY;
            }
        }

        if (this.onGround) {
            this.velocityX *= 0.5;
            this.velocityY *= 0.5;
            this.velocityZ *= 0.5;
        }

        this.move(this.velocityX, moveY, this.velocityZ);
        if (!this.onGround) {
            this.velocityX *= this.getDragAir();
            this.velocityY *= this.getDragAir();
            this.velocityZ *= this.getDragAir();
        }

    }

    @Override
    public void updateFuel() {
        if (this.field_3907 > 0) {
            --this.field_3907;
        }

        if (this.field_3907 <= 0) {
            this.field_3904 = this.field_3905 = 0.0;
        }

        this.method_3062(this.field_3907 > 0);
    }

    @Override
    public void adjustSlopeVelocities(int metadata) {
        double acceleration = 0.0078125;
        if (metadata == 2) {
            this.velocityX -= acceleration;
        } else if (metadata == 3) {
            this.velocityX += acceleration;
        } else if (metadata == 4) {
            this.velocityZ += acceleration;
        } else if (metadata == 5) {
            this.velocityZ -= acceleration;
        }

    }

    @Override
    public float getMaxSpeedRail() {
        return this.maxSpeedRail;
    }

    @Override
    public void setMaxSpeedRail(float value) {
        this.maxSpeedRail = value;
    }

    @Override
    public float getMaxSpeedGround() {
        return this.maxSpeedGround;
    }

    @Override
    public void setMaxSpeedGround(float value) {
        this.maxSpeedGround = value;
    }

    @Override
    public float getMaxSpeedAirLateral() {
        return this.maxSpeedAirLateral;
    }

    @Override
    public void setMaxSpeedAirLateral(float value) {
        this.maxSpeedAirLateral = value;
    }

    @Override
    public float getMaxSpeedAirVertical() {
        return this.maxSpeedAirVertical;
    }

    @Override
    public void setMaxSpeedAirVertical(float value) {
        this.maxSpeedAirVertical = value;
    }

    @Override
    public double getDragAir() {
        return this.dragAir;
    }

    @Override
    public void setDragAir(double value) {
        this.dragAir = value;
    }
}
