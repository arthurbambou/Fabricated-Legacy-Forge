package fr.catcore.fabricatedforge.mixin.forgefml.entity.player;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.Player;
import fr.catcore.fabricatedforge.mixininterface.IPlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CanSleepEnum;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends MobEntity implements CommandSource, IPlayerEntity, Player {
    @Shadow private ItemStack useItem;

    @Shadow public PlayerInventory inventory;

    @Shadow private int itemUseTicks;

    @Shadow protected abstract void drink(ItemStack potion, int i);

    @Shadow protected abstract void useItem();

    @Shadow public abstract void resetUseItem();

    @Shadow public int experiencePickUpDelay;

    @Shadow private int sleepTimer;

    @Shadow public ScreenHandler openScreenHandler;

    @Shadow public abstract void closeHandledScreen();

    @Shadow public ScreenHandler playerScreenHandler;

    @Shadow public PlayerAbilities abilities;

    @Shadow public double capeX;

    @Shadow public double capeY;

    @Shadow public double capeZ;

    @Shadow public double prevCapeX;

    @Shadow public double prevCapeY;

    @Shadow public double prevCapeZ;

    @Shadow public abstract void incrementStat(Stat stat, int amount);

    @Shadow private BlockPos field_4011;

    @Shadow protected HungerManager hungerManager;

    @Shadow public String username;

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Shadow public abstract ItemStack getMainHandStack();

    @Shadow public abstract void addCritParticles(Entity target);

    @Shadow public abstract void addEnchantedHitParticles(Entity target);

    @Shadow public abstract void incrementStat(Stat stat);

    @Shadow protected abstract void method_3163(MobEntity mobEntity, boolean bl);

    @Shadow protected abstract void decrementXp(int xp);

    @Shadow protected boolean inBed;

    @Shadow public BlockPos field_3992;

    @Shadow protected abstract void method_3203();

    @Shadow public FishingBobberEntity fishHook;

    @Shadow public abstract void method_4573(BlockPos blockPos, boolean bl);

    @Shadow public abstract ItemEntity dropStack(ItemStack stack, boolean bl);

    public PlayerEntityMixin(World world) {
        super(world);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void tick() {
        FMLCommonHandler.instance().onPlayerPreTick((PlayerEntity)(Object) this);
        if (this.useItem != null) {
            ItemStack var1 = this.inventory.getMainHandStack();
            if (var1 == this.useItem) {
                this.useItem.getItem().onUsingItemTick(this.useItem, (PlayerEntity)(Object) this, this.itemUseTicks);
                if (this.itemUseTicks <= 25 && this.itemUseTicks % 4 == 0) {
                    this.drink(var1, 5);
                }

                if (--this.itemUseTicks == 0 && !this.world.isClient) {
                    this.useItem();
                }
            } else {
                this.resetUseItem();
            }
        }

        if (this.experiencePickUpDelay > 0) {
            --this.experiencePickUpDelay;
        }

        if (this.method_2641()) {
            ++this.sleepTimer;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }

            if (!this.world.isClient) {
                if (!this.method_3213()) {
                    this.awaken(true, true, false);
                } else if (this.world.isDay()) {
                    this.awaken(false, true, true);
                }
            }
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }

        super.tick();
        if (!this.world.isClient && this.openScreenHandler != null && !this.openScreenHandler.canUse((PlayerEntity)(Object) this)) {
            this.closeHandledScreen();
            this.openScreenHandler = this.playerScreenHandler;
        }

        if (this.isOnFire() && this.abilities.invulnerable) {
            this.extinguish();
        }

        this.capeX = this.prevCapeX;
        this.capeY = this.prevCapeY;
        this.capeZ = this.prevCapeZ;
        double var9 = this.x - this.prevCapeX;
        double var3 = this.y - this.prevCapeY;
        double var5 = this.z - this.prevCapeZ;
        double var7 = 10.0;
        if (var9 > var7) {
            this.capeX = this.prevCapeX = this.x;
        }

        if (var5 > var7) {
            this.capeZ = this.prevCapeZ = this.z;
        }

        if (var3 > var7) {
            this.capeY = this.prevCapeY = this.y;
        }

        if (var9 < -var7) {
            this.capeX = this.prevCapeX = this.x;
        }

        if (var5 < -var7) {
            this.capeZ = this.prevCapeZ = this.z;
        }

        if (var3 < -var7) {
            this.capeY = this.prevCapeY = this.y;
        }

        this.prevCapeX += var9 * 0.25;
        this.prevCapeZ += var5 * 0.25;
        this.prevCapeY += var3 * 0.25;
        this.incrementStat(Stats.MINUTES_PLAYED, 1);
        if (this.vehicle == null) {
            this.field_4011 = null;
        }

        if (!this.world.isClient) {
            this.hungerManager.update((PlayerEntity)(Object) this);
        }

        FMLCommonHandler.instance().onPlayerPostTick((PlayerEntity)(Object) this);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void dropInventory(DamageSource par1DamageSource) {
        super.dropInventory(par1DamageSource);
        this.setBounds(0.2F, 0.2F);
        this.updatePosition(this.x, this.y, this.z);
        this.velocityY = 0.1F;
        this.captureDrops(true);
        this.getCapturedDrops().clear();
        if (this.username.equals("Notch")) {
            this.dropStack(new ItemStack(Item.APPLE, 1), true);
        }

        if (!this.world.getGameRules().getBoolean("keepInventory")) {
            this.inventory.dropAll();
        }

        this.captureDrops(false);
        if (!this.world.isClient) {
            PlayerDropsEvent event = new PlayerDropsEvent((PlayerEntity)(Object) this, par1DamageSource, this.getCapturedDrops(), this.field_3332 > 0);
            if (!MinecraftForge.EVENT_BUS.post(event)) {
                for(ItemEntity item : this.getCapturedDrops()) {
                    this.spawnItemEntity(item);
                }
            }
        }

        if (par1DamageSource != null) {
            this.velocityX = (double)(-MathHelper.cos((this.field_3299 + this.yaw) * (float) Math.PI / 180.0F) * 0.1F);
            this.velocityZ = (double)(-MathHelper.sin((this.field_3299 + this.yaw) * (float) Math.PI / 180.0F) * 0.1F);
        } else {
            this.velocityX = this.velocityZ = 0.0;
        }

        this.heightOffset = 0.1F;
        this.incrementStat(Stats.DEATHS, 1);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public ItemEntity method_4580() {
        ItemStack stack = this.inventory.getMainHandStack();
        if (stack == null) {
            return null;
        } else {
            return stack.getItem().onDroppedByPlayer(stack, (PlayerEntity)(Object) this)
                    ? ForgeHooks.onPlayerTossEvent((PlayerEntity)(Object) this, this.inventory.takeInvStack(this.inventory.selectedSlot, 1))
                    : null;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public ItemEntity dropStack(ItemStack par1ItemStack) {
        return ForgeHooks.onPlayerTossEvent((PlayerEntity)(Object) this, par1ItemStack);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void spawnItemEntity(ItemEntity par1EntityItem) {
        if (this.captureDrops()) {
            this.getCapturedDrops().add(par1EntityItem);
        } else {
            this.world.spawnEntity(par1EntityItem);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public float method_3153(Block par1Block) {
        return this.getCurrentPlayerStrVsBlock(par1Block, 0);
    }

    @Override
    public float getCurrentPlayerStrVsBlock(Block par1Block, int meta) {
        ItemStack stack = this.inventory.getMainHandStack();
        float var2 = stack == null ? 1.0F : stack.getItem().getStrVsBlock(stack, par1Block, meta);
        int var3 = EnchantmentHelper.method_4652(this);
        if (var3 > 0 && ForgeHooks.canHarvestBlock(par1Block, (PlayerEntity)(Object) this, meta)) {
            var2 += (float)(var3 * var3 + 1);
        }

        if (this.method_2581(StatusEffect.HASTE)) {
            var2 *= 1.0F + (float)(this.method_2627(StatusEffect.HASTE).getAmplifier() + 1) * 0.2F;
        }

        if (this.method_2581(StatusEffect.MINING_FATIGUE)) {
            var2 *= 1.0F - (float)(this.method_2627(StatusEffect.MINING_FATIGUE).getAmplifier() + 1) * 0.2F;
        }

        if (this.isSubmergedIn(Material.WATER) && !EnchantmentHelper.method_4656(this)) {
            var2 /= 5.0F;
        }

        if (!this.onGround) {
            var2 /= 5.0F;
        }

        var2 = ForgeEventFactory.getBreakSpeed((PlayerEntity)(Object) this, par1Block, meta, var2);
        return var2 < 0.0F ? 0.0F : var2;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean isUsingEffectiveTool(Block par1Block) {
        return ForgeEventFactory.doPlayerHarvestCheck((PlayerEntity)(Object) this, par1Block, this.inventory.canToolBreak(par1Block));
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void method_2653(DamageSource par1DamageSource, int par2) {
        LivingHurtEvent event = new LivingHurtEvent(this, par1DamageSource, par2);
        if (!MinecraftForge.EVENT_BUS.post(event) && event.ammount != 0) {
            par2 = event.ammount;
            if (!par1DamageSource.bypassesArmor() && this.method_2611()) {
                par2 = 1 + par2 >> 1;
            }

            par2 = ISpecialArmor.ArmorProperties.ApplyArmor(this, this.inventory.armor, par1DamageSource, (double)par2);
            if (par2 > 0) {
                par2 = this.method_2648(par1DamageSource, par2);
                this.addExhaustion(par1DamageSource.getExhaustion());
                this.field_3294 -= par2;
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_3215(Entity par1Entity) {
        if (MinecraftForge.EVENT_BUS.post(new EntityInteractEvent((PlayerEntity)(Object) this, par1Entity))) {
            return false;
        } else if (par1Entity.method_2537((PlayerEntity)(Object) this)) {
            return true;
        } else {
            ItemStack var2 = this.getMainHandStack();
            if (var2 != null && par1Entity instanceof MobEntity) {
                if (this.abilities.creativeMode) {
                    var2 = var2.copy();
                }

                if (var2.method_3411((MobEntity)par1Entity)) {
                    if (var2.count <= 0 && !this.abilities.creativeMode) {
                        this.removeSelectedSlotItem();
                    }

                    return true;
                }
            }

            return false;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void removeSelectedSlotItem() {
        ItemStack orig = this.getMainHandStack();
        this.inventory.setInvStack(this.inventory.selectedSlot, (ItemStack)null);
        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent((PlayerEntity)(Object) this, orig));
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_3216(Entity par1Entity) {
        if (!MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((PlayerEntity)(Object) this, par1Entity))) {
            ItemStack stack = this.getMainHandStack();
            if (stack == null || !stack.getItem().onLeftClickEntity(stack, (PlayerEntity)(Object) this, par1Entity)) {
                if (par1Entity.isAttackable()) {
                    int var2 = this.inventory.method_3127(par1Entity);
                    if (this.method_2581(StatusEffect.STRENGTH)) {
                        var2 += 3 << this.method_2627(StatusEffect.STRENGTH).getAmplifier();
                    }

                    if (this.method_2581(StatusEffect.WEAKNESS)) {
                        var2 -= 2 << this.method_2627(StatusEffect.WEAKNESS).getAmplifier();
                    }

                    int var3 = 0;
                    int var4 = 0;
                    if (par1Entity instanceof MobEntity) {
                        var4 = EnchantmentHelper.method_4647(this, (MobEntity)par1Entity);
                        var3 += EnchantmentHelper.method_4651(this, (MobEntity)par1Entity);
                    }

                    if (this.isSprinting()) {
                        ++var3;
                    }

                    if (var2 > 0 || var4 > 0) {
                        boolean var5 = this.fallDistance > 0.0F
                                && !this.onGround
                                && !this.method_2660()
                                && !this.isTouchingWater()
                                && !this.method_2581(StatusEffect.BLINDNESS)
                                && this.vehicle == null
                                && par1Entity instanceof MobEntity;
                        if (var5) {
                            var2 += this.random.nextInt(var2 / 2 + 2);
                        }

                        var2 += var4;
                        boolean var6 = par1Entity.damage(DamageSource.player((PlayerEntity)(Object) this), var2);
                        if (var6) {
                            if (var3 > 0) {
                                par1Entity.addVelocity(
                                        (double)(-MathHelper.sin(this.yaw * (float) Math.PI / 180.0F) * (float)var3 * 0.5F),
                                        0.1,
                                        (double)(MathHelper.cos(this.yaw * (float) Math.PI / 180.0F) * (float)var3 * 0.5F)
                                );
                                this.velocityX *= 0.6;
                                this.velocityZ *= 0.6;
                                this.setSprinting(false);
                            }

                            if (var5) {
                                this.addCritParticles(par1Entity);
                            }

                            if (var4 > 0) {
                                this.addEnchantedHitParticles(par1Entity);
                            }

                            if (var2 >= 18) {
                                this.incrementStat(AchievementsAndCriterions.OVERKILL);
                            }

                            this.method_2669(par1Entity);
                        }

                        ItemStack var7 = this.getMainHandStack();
                        if (var7 != null && par1Entity instanceof MobEntity) {
                            var7.method_3412((MobEntity)par1Entity, (PlayerEntity)(Object) this);
                            if (var7.count <= 0) {
                                this.removeSelectedSlotItem();
                            }
                        }

                        if (par1Entity instanceof MobEntity) {
                            if (par1Entity.isAlive()) {
                                this.method_3163((MobEntity)par1Entity, true);
                            }

                            this.incrementStat(Stats.DAMAGE_DEALT, var2);
                            int var8 = EnchantmentHelper.method_4646(this, (MobEntity)par1Entity);
                            if (var8 > 0 && var6) {
                                par1Entity.setOnFireFor(var8 * 4);
                            }
                        }

                        this.addExhaustion(0.3F);
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
    public CanSleepEnum method_3152(int par1, int par2, int par3) {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent((PlayerEntity)(Object) this, par1, par2, par3);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.result != null) {
            return event.result;
        } else {
            if (!this.world.isClient) {
                if (this.method_2641() || !this.isAlive()) {
                    return CanSleepEnum.OTHER_PROBLEM;
                }

                if (!this.world.dimension.canPlayersSleep()) {
                    return CanSleepEnum.NOT_POSSIBLE_HERE;
                }

                if (this.world.isDay()) {
                    return CanSleepEnum.NOT_POSSIBLE_NOW;
                }

                if (Math.abs(this.x - (double)par1) > 3.0 || Math.abs(this.y - (double)par2) > 2.0 || Math.abs(this.z - (double)par3) > 3.0) {
                    return CanSleepEnum.TOO_FAR_AWAY;
                }

                double var4 = 8.0;
                double var6 = 5.0;
                List var8 = this.world
                        .getEntitiesInBox(
                                HostileEntity.class,
                                Box.getLocalPool()
                                        .getOrCreate(
                                                (double)par1 - var4, (double)par2 - var6, (double)par3 - var4, (double)par1 + var4, (double)par2 + var6, (double)par3 + var4
                                        )
                        );
                if (!var8.isEmpty()) {
                    return CanSleepEnum.NOT_SAFE;
                }
            }

            this.setBounds(0.2F, 0.2F);
            this.heightOffset = 0.2F;
            if (this.world.isPosLoaded(par1, par2, par3)) {
                int var9 = this.world.getBlockData(par1, par2, par3);
                int var5 = BedBlock.method_297(var9);
                Block block = Block.BLOCKS[this.world.getBlock(par1, par2, par3)];
                if (block != null) {
                    var5 = block.getBedDirection(this.world, par1, par2, par3);
                }

                float var10 = 0.5F;
                float var7 = 0.5F;
                switch(var5) {
                    case 0:
                        var7 = 0.9F;
                        break;
                    case 1:
                        var10 = 0.1F;
                        break;
                    case 2:
                        var7 = 0.1F;
                        break;
                    case 3:
                        var10 = 0.9F;
                }

                this.decrementXp(var5);
                this.updatePosition((double)((float)par1 + var10), (double)((float)par2 + 0.9375F), (double)((float)par3 + var7));
            } else {
                this.updatePosition((double)((float)par1 + 0.5F), (double)((float)par2 + 0.9375F), (double)((float)par3 + 0.5F));
            }

            this.inBed = true;
            this.sleepTimer = 0;
            this.field_3992 = new BlockPos(par1, par2, par3);
            this.velocityX = this.velocityZ = this.velocityY = 0.0;
            if (!this.world.isClient) {
                this.world.method_3669();
            }

            return CanSleepEnum.OK;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void awaken(boolean par1, boolean par2, boolean par3) {
        this.setBounds(0.6F, 1.8F);
        this.method_3203();
        BlockPos var4 = this.field_3992;
        BlockPos var5 = this.field_3992;
        Block block = var4 == null ? null : Block.BLOCKS[this.world.getBlock(var4.x, var4.y, var4.z)];
        if (var4 != null && block != null && block.isBed(this.world, var4.x, var4.y, var4.z, this)) {
            block.setBedOccupied(this.world, var4.x, var4.y, var4.z, (PlayerEntity)(Object) this, false);
            var5 = block.getBedSpawnPosition(this.world, var4.x, var4.y, var4.z, (PlayerEntity)(Object) this);
            if (var5 == null) {
                var5 = new BlockPos(var4.x, var4.y + 1, var4.z);
            }

            this.updatePosition((double)((float)var5.x + 0.5F), (double)((float)var5.y + this.heightOffset + 0.1F), (double)((float)var5.z + 0.5F));
        }

        this.inBed = false;
        if (!this.world.isClient && par2) {
            this.world.method_3669();
        }

        if (par1) {
            this.sleepTimer = 0;
        } else {
            this.sleepTimer = 100;
        }

        if (par3) {
            this.method_4573(this.field_3992, false);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private boolean method_3213() {
        BlockPos c = this.field_3992;
        int blockID = this.world.getBlock(c.x, c.y, c.z);
        return Block.BLOCKS[blockID] != null && Block.BLOCKS[blockID].isBed(this.world, c.x, c.y, c.z, this);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static BlockPos method_3169(World par0World, BlockPos par1ChunkCoordinates, boolean par2) {
        ChunkProvider var3 = par0World.getChunkProvider();
        var3.method_3871(par1ChunkCoordinates.x - 3 >> 4, par1ChunkCoordinates.z - 3 >> 4);
        var3.method_3871(par1ChunkCoordinates.x + 3 >> 4, par1ChunkCoordinates.z - 3 >> 4);
        var3.method_3871(par1ChunkCoordinates.x - 3 >> 4, par1ChunkCoordinates.z + 3 >> 4);
        var3.method_3871(par1ChunkCoordinates.x + 3 >> 4, par1ChunkCoordinates.z + 3 >> 4);
        Block block = Block.BLOCKS[par0World.getBlock(par1ChunkCoordinates.x, par1ChunkCoordinates.y, par1ChunkCoordinates.z)];
        if (block != null && block.isBed(par0World, par1ChunkCoordinates.x, par1ChunkCoordinates.y, par1ChunkCoordinates.z, null)) {
            return block.getBedSpawnPosition(par0World, par1ChunkCoordinates.x, par1ChunkCoordinates.y, par1ChunkCoordinates.z, null);
        } else {
            return par2
                    && par0World.isAir(par1ChunkCoordinates.x, par1ChunkCoordinates.y, par1ChunkCoordinates.z)
                    && par0World.isAir(par1ChunkCoordinates.x, par1ChunkCoordinates.y + 1, par1ChunkCoordinates.z)
                    ? par1ChunkCoordinates
                    : null;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public float method_3183() {
        if (this.field_3992 != null) {
            int x = this.field_3992.x;
            int y = this.field_3992.y;
            int z = this.field_3992.z;
            Block block = Block.BLOCKS[this.world.getBlock(x, y, z)];
            int var2 = block == null ? 0 : block.getBedDirection(this.world, x, y, z);
            switch(var2) {
                case 0:
                    return 90.0F;
                case 1:
                    return 0.0F;
                case 2:
                    return 270.0F;
                case 3:
                    return 180.0F;
            }
        }

        return 0.0F;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public int method_2630(ItemStack par1ItemStack, int par2) {
        int var3 = super.method_2630(par1ItemStack, par2);
        if (par1ItemStack.id == Item.field_4253.id && this.fishHook != null) {
            var3 = par1ItemStack.method_3429() + 16;
        } else {
            if (par1ItemStack.getItem().method_3397()) {
                return par1ItemStack.getItem().method_3369(par1ItemStack.getMeta(), par2);
            }

            if (this.useItem != null && par1ItemStack.id == Item.field_4349.id) {
                int var4 = par1ItemStack.getMaxUseTime() - this.itemUseTicks;
                if (var4 >= 18) {
                    return 133;
                }

                if (var4 > 13) {
                    return 117;
                }

                if (var4 > 0) {
                    return 101;
                }
            }

            var3 = par1ItemStack.getItem().getIconIndex(par1ItemStack, par2, (PlayerEntity)(Object) this, this.useItem, this.itemUseTicks);
        }

        return var3;
    }

    @Override
    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
        FMLNetworkHandler.openGui((PlayerEntity)(Object) this, mod, modGuiId, world, x, y, z);
    }
}
