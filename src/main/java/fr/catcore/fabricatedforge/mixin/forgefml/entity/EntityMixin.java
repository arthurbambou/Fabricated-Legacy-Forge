package fr.catcore.fabricatedforge.mixin.forgefml.entity;

import fr.catcore.fabricatedforge.mixininterface.IEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity {

    @Shadow public World world;
    @Shadow public double y;

    @Shadow public abstract void updatePosition(double x, double y, double z);

    @Shadow public double x;
    @Shadow public double z;
    @Shadow @Final public Box boundingBox;
    @Shadow public double velocityX;
    @Shadow public double velocityY;
    @Shadow public double velocityZ;
    @Shadow public float pitch;

    @Shadow protected abstract NbtList toListNbt(float... values);

    @Shadow protected abstract NbtList toListNbt(double... values);

    @Shadow public float field_3214;
    @Shadow public float yaw;
    @Shadow public float fallDistance;
    @Shadow private int fireTicks;

    @Shadow public abstract int getAir();

    @Shadow public boolean onGround;

    @Shadow protected abstract void writeCustomDataToNbt(NbtCompound nbt);

    @Shadow public double prevTickX;
    @Shadow public double prevTickY;
    @Shadow public double prevTickZ;
    @Shadow public double prevX;
    @Shadow public double prevY;
    @Shadow public double prevZ;
    @Shadow public float prevYaw;
    @Shadow public float prevPitch;

    @Shadow public abstract void setAir(int air);

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Shadow protected abstract void readCustomDataFromNbt(NbtCompound nbt);

    @Shadow public Entity vehicle;

    @Shadow protected abstract boolean getFlag(int index);

    @Shadow protected Random random;

    @Shadow public abstract float getEyeHeight();

    @Shadow public int dimension;
    // Forge Fields
    @Unique
    private NbtCompound customEntityData;
    @Unique
    public boolean captureDrops = false;
    @Unique
    public ArrayList<ItemEntity> capturedDrops = new ArrayList<>();
    @Unique
    private UUID persistentID;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    protected void afterSpawn() {
        if (this.world != null) {
            while(true) {
                if (this.y > 0.0) {
                    this.updatePosition(this.x, this.y, this.z);
                    if (!this.world.doesBoxCollide((Entity)(Object) this, this.boundingBox).isEmpty()) {
                        ++this.y;
                        continue;
                    }
                }

                this.velocityX = this.velocityY = this.velocityZ = 0.0;
                this.pitch = 0.0F;
                break;
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void writePlayerData(NbtCompound par1NBTTagCompound) {
        par1NBTTagCompound.put("Pos", this.toListNbt(this.x, this.y + (double)this.field_3214, this.z));
        par1NBTTagCompound.put("Motion", this.toListNbt(this.velocityX, this.velocityY, this.velocityZ));
        par1NBTTagCompound.put("Rotation", this.toListNbt(this.yaw, this.pitch));
        par1NBTTagCompound.putFloat("FallDistance", this.fallDistance);
        par1NBTTagCompound.putShort("Fire", (short)this.fireTicks);
        par1NBTTagCompound.putShort("Air", (short)this.getAir());
        par1NBTTagCompound.putBoolean("OnGround", this.onGround);
        par1NBTTagCompound.putInt("Dimension", this.dimension);
        if (this.persistentID != null) {
            par1NBTTagCompound.putLong("PersistentIDMSB", this.persistentID.getMostSignificantBits());
            par1NBTTagCompound.putLong("PersistentIDLSB", this.persistentID.getLeastSignificantBits());
        }

        if (this.customEntityData != null) {
            par1NBTTagCompound.put("ForgeData", this.customEntityData);
        }

        this.writeCustomDataToNbt(par1NBTTagCompound);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void fromNbt(NbtCompound par1NBTTagCompound) {
        NbtList var2 = par1NBTTagCompound.getList("Pos");
        NbtList var3 = par1NBTTagCompound.getList("Motion");
        NbtList var4 = par1NBTTagCompound.getList("Rotation");
        this.velocityX = ((NbtDouble)var3.method_1218(0)).value;
        this.velocityY = ((NbtDouble)var3.method_1218(1)).value;
        this.velocityZ = ((NbtDouble)var3.method_1218(2)).value;
        if (Math.abs(this.velocityX) > 10.0) {
            this.velocityX = 0.0;
        }

        if (Math.abs(this.velocityY) > 10.0) {
            this.velocityY = 0.0;
        }

        if (Math.abs(this.velocityZ) > 10.0) {
            this.velocityZ = 0.0;
        }

        this.prevX = this.prevTickX = this.x = ((NbtDouble)var2.method_1218(0)).value;
        this.prevY = this.prevTickY = this.y = ((NbtDouble)var2.method_1218(1)).value;
        this.prevZ = this.prevTickZ = this.z = ((NbtDouble)var2.method_1218(2)).value;
        this.prevYaw = this.yaw = ((NbtFloat)var4.method_1218(0)).value;
        this.prevPitch = this.pitch = ((NbtFloat)var4.method_1218(1)).value;
        this.fallDistance = par1NBTTagCompound.getFloat("FallDistance");
        this.fireTicks = par1NBTTagCompound.getShort("Fire");
        this.setAir(par1NBTTagCompound.getShort("Air"));
        this.onGround = par1NBTTagCompound.getBoolean("OnGround");
        this.dimension = par1NBTTagCompound.getInt("Dimension");
        this.updatePosition(this.x, this.y, this.z);
        this.setRotation(this.yaw, this.pitch);
        if (par1NBTTagCompound.contains("ForgeData")) {
            this.customEntityData = par1NBTTagCompound.getCompound("ForgeData");
        }

        if (par1NBTTagCompound.contains("PersistentIDMSB") && par1NBTTagCompound.contains("PersistentIDLSB")) {
            this.persistentID = new UUID(par1NBTTagCompound.getLong("PersistentIDMSB"), par1NBTTagCompound.getLong("PersistentIDLSB"));
        }

        this.readCustomDataFromNbt(par1NBTTagCompound);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public ItemEntity dropItem(ItemStack par1ItemStack, float par2) {
        ItemEntity var3 = new ItemEntity(this.world, this.x, this.y + (double)par2, this.z, par1ItemStack);
        var3.pickupDelay = 10;
        if (this.captureDrops) {
            this.capturedDrops.add(var3);
        } else {
            this.world.spawnEntity(var3);
        }

        return var3;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public boolean hasVehicle() {
        return this.vehicle != null && this.vehicle.shouldRiderSit() || this.getFlag(2);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public float method_4444(Explosion par1Explosion, Block par2Block, int par3, int par4, int par5) {
        return par2Block.getExplosionResistance((Entity)(Object) this, this.world, par3, par4, par5, this.x, this.y + (double)this.getEyeHeight(), this.z);
    }

    @Override
    public NbtCompound getEntityData() {
        if (this.customEntityData == null) {
            this.customEntityData = new NbtCompound();
        }

        return this.customEntityData;
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    public ItemStack getPickedResult(BlockHitResult target) {
        if ((Entity)(Object) this instanceof PaintingEntity) {
            return new ItemStack(Item.PAINTING);
        } else if ((Entity)(Object) this instanceof AbstractMinecartEntity) {
            return ((AbstractMinecartEntity) (Object) this).getCartItem();
        } else if ((Entity)(Object) this instanceof BoatEntity) {
            return new ItemStack(Item.BOAT);
        } else if ((Entity)(Object) this instanceof ItemFrameEntity) {
            ItemStack held = ((ItemFrameEntity) (Object) this).getHeldItemStack();
            return held == null ? new ItemStack(Item.ITEM_FRAME) : held.copy();
        } else {
            int id = EntityType.getIdByEntity((Entity)(Object) this);
            return id > 0 && EntityType.field_3267.containsKey(id) ? new ItemStack(Item.SPAWN_EGG, 1, id) : null;
        }
    }

    @Override
    public UUID getPersistentID() {
        return this.persistentID;
    }

    @Override
    public synchronized void generatePersistentID() {
        if (this.persistentID == null) {
            this.persistentID = UUID.randomUUID();
        }

    }

    @Override
    public boolean captureDrops() {
        return this.captureDrops;
    }

    @Override
    public void captureDrops(boolean captureDrops) {
        this.captureDrops = captureDrops;
    }

    @Override
    public ArrayList<ItemEntity> getCapturedDrops() {
        return this.capturedDrops;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }
}
