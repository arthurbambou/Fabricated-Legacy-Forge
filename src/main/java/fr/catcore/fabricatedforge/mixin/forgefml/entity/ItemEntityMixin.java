package fr.catcore.fabricatedforge.mixin.forgefml.entity;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow public int pickupDelay;

    @Shadow public abstract boolean tryMerge(ItemEntity other);

    @Shadow public int age;
    @Shadow private int health;
    @Shadow public ItemStack field_23087;
    // Forge Field
    @Unique
    public int lifespan = 6000;

    public ItemEntityMixin(World world) {
        super(world);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("RETURN"))
    private void fmlCtr(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack, CallbackInfo ci) {
        this.lifespan = par8ItemStack.getItem() == null ? 6000 : par8ItemStack.getItem().getEntityLifespan(par8ItemStack, par1World);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void tick() {
        super.tick();
        if (this.pickupDelay > 0) {
            --this.pickupDelay;
        }

        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        this.velocityY -= 0.04F;
        this.pushOutOfBlocks(this.x, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0, this.z);
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        boolean var1 = (int)this.prevX != (int)this.x || (int)this.prevY != (int)this.y || (int)this.prevZ != (int)this.z;
        if (var1) {
            if (this.world.getMaterial(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z)) == Material.LAVA) {
                this.velocityY = 0.2F;
                this.velocityX = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                this.velocityZ = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                this.world.playSound(this, "random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }

            if (!this.world.isClient) {
                for(ItemEntity var3 : (List<ItemEntity>) this.world.getEntitiesInBox(ItemEntity.class, this.boundingBox.expand(0.5, 0.0, 0.5))) {
                    this.tryMerge(var3);
                }
            }
        }

        float var4 = 0.98F;
        if (this.onGround) {
            var4 = 0.58800006F;
            int var5 = this.world.getBlock(MathHelper.floor(this.x), MathHelper.floor(this.boundingBox.minY) - 1, MathHelper.floor(this.z));
            if (var5 > 0) {
                var4 = Block.BLOCKS[var5].slipperiness * 0.98F;
            }
        }

        this.velocityX *= (double)var4;
        this.velocityY *= 0.98F;
        this.velocityZ *= (double)var4;
        if (this.onGround) {
            this.velocityY *= -0.5;
        }

        ++this.age;
        if (!this.world.isClient && this.age >= this.lifespan) {
            ItemExpireEvent event = new ItemExpireEvent(
                    (ItemEntity)(Object) this, this.field_23087.getItem() == null ? 6000 : this.field_23087.getItem().getEntityLifespan(this.field_23087, this.world)
            );
            if (MinecraftForge.EVENT_BUS.post(event)) {
                this.lifespan += event.extraLife;
            } else {
                this.remove();
            }
        }

        if (this.field_23087 == null || this.field_23087.count <= 0) {
            this.remove();
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void writeCustomDataToNbt(NbtCompound par1NBTTagCompound) {
        par1NBTTagCompound.putShort("Health", (short)((byte)this.health));
        par1NBTTagCompound.putShort("Age", (short)this.age);
        par1NBTTagCompound.putInt("Lifespan", this.lifespan);
        if (this.field_23087 != null) {
            par1NBTTagCompound.put("Item", this.field_23087.toNbt(new NbtCompound()));
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void readCustomDataFromNbt(NbtCompound par1NBTTagCompound) {
        this.health = par1NBTTagCompound.getShort("Health") & 255;
        this.age = par1NBTTagCompound.getShort("Age");
        NbtCompound var2 = par1NBTTagCompound.getCompound("Item");
        this.field_23087 = ItemStack.fromNbt(var2);
        if (this.field_23087 == null || this.field_23087.count <= 0) {
            this.remove();
        }

        if (par1NBTTagCompound.contains("Lifespan")) {
            this.lifespan = par1NBTTagCompound.getInt("Lifespan");
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void onPlayerCollision(PlayerEntity par1EntityPlayer) {
        if (!this.world.isClient) {
            if (this.pickupDelay > 0) {
                return;
            }

            EntityItemPickupEvent event = new EntityItemPickupEvent(par1EntityPlayer, (ItemEntity)(Object) this);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }

            int var2 = this.field_23087.count;
            if (this.pickupDelay <= 0 && (event.getResult() == Event.Result.ALLOW || var2 <= 0 || par1EntityPlayer.inventory.insertStack(this.field_23087))) {
                if (this.field_23087.id == Block.LOG.id) {
                    par1EntityPlayer.incrementStat(AchievementsAndCriterions.GETTING_WOOD);
                }

                if (this.field_23087.id == Item.LEATHER.id) {
                    par1EntityPlayer.incrementStat(AchievementsAndCriterions.KILL_COW);
                }

                if (this.field_23087.id == Item.DIAMOND.id) {
                    par1EntityPlayer.incrementStat(AchievementsAndCriterions.DIAOMNDS);
                }

                if (this.field_23087.id == Item.BLAZE_ROD.id) {
                    par1EntityPlayer.incrementStat(AchievementsAndCriterions.BLAZE_ROD);
                }

                GameRegistry.onPickupNotification(par1EntityPlayer, (ItemEntity)(Object) this);
                this.world.playSound(this, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.method_3162(this, var2);
                if (this.field_23087.count <= 0) {
                    this.remove();
                }
            }
        }

    }
}
