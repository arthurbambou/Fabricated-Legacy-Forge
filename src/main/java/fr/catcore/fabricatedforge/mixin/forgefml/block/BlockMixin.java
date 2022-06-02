package fr.catcore.fabricatedforge.mixin.forgefml.block;

import cpw.mods.fml.common.registry.BlockProxy;
import fr.catcore.fabricatedforge.mixininterface.IBlock;
import fr.catcore.fabricatedforge.forged.ReflectionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraftforge.common.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Random;

@Mixin(Block.class)
public abstract class BlockMixin implements IBlock, BlockProxy {

    @Shadow @Final public static Block[] BLOCKS;

    @Shadow protected abstract void method_422(World world, int i, int j, int k, ItemStack itemStack);

    @Shadow @Final public int id;

    @Shadow protected abstract ItemStack method_448(int i);

    @Shadow public abstract void method_445(World world, int i, int j, int k, int l, int m);

    @Shadow @Final public static int[] field_496;
    @Shadow @Final public Material material;

    @Shadow public abstract boolean renderAsNormalBlock();

    @Shadow public abstract boolean hasTransperancy();

    @Shadow @Final public static Block NETHERRACK;
    @Shadow @Final public static Block BEDROCK;
    @Shadow protected boolean blockEntity;

    @Shadow public abstract int getBonusDrops(int id, Random rand);

    @Shadow public abstract int method_398(int i, Random random, int j);

    @Shadow
    public abstract int method_431(int i);

    @Shadow
    public static boolean method_467(int id) {
        return false;
    }

    @Shadow @Final public static Block BED;
    @Shadow @Final public static boolean[] field_493;
    @Shadow @Final public static Block STONE_BLOCK;

    @Shadow public abstract float getBlastResistance(Entity entity);

    @Shadow @Final public static Block WOODEN_FENCE;
    @Shadow @Final public static Block NETHER_BRICK_FENCE;
    @Shadow @Final public static Block GLASS_BLOCK;

    @Shadow public abstract int method_479();

    @Shadow public abstract int method_407(World world, int i, int j, int k);

    @Shadow public abstract int method_463(World world, int i, int j, int k);

    @Shadow @Final public static Block CACTUS;
    @Shadow @Final public static Block SUGARCANE;
    @Shadow @Final public static Block SAND_BLOCK;
    @Shadow @Final public static Block SOULSAND;
    @Shadow @Final public static Block FARMLAND;
    @Shadow @Final public static GrassBlock GRASS_BLOCK;
    @Shadow @Final public static Block DIRT;
    @Shadow @Final public static int[] field_494;
    @Shadow @Final public static Block OBSIDIAN;
    @Shadow @Final public static Block END_STONE;
    @Shadow @Final public static Block COBBLESTONE_WALL;

    // Additional Fields
    @Unique
    protected String currentTexture = "/terrain.png";
    @Unique
    public boolean isDefaultTexture = true;

    @Inject(method = "<init>(ILnet/minecraft/block/Material;)V", at = @At("RETURN"))
    private void ctrPart2(int material, Material par2, CallbackInfo ci) {
        this.isDefaultTexture = this.getTextureFile() != null && this.getTextureFile().equalsIgnoreCase("/terrain.png");
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean hasBlockEntity() {
        return this.hasTileEntity(0);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public float method_458(BlockView par1IBlockAccess, int par2, int par3, int par4) {
        return par1IBlockAccess.method_3779(par2, par3, par4, this.getLightValue(par1IBlockAccess, par2, par3, par4));
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public int method_455(BlockView par1IBlockAccess, int par2, int par3, int par4) {
        return par1IBlockAccess.method_3778(par2, par3, par4, this.getLightValue(par1IBlockAccess, par2, par3, par4));
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public float method_405(PlayerEntity par1EntityPlayer, World par2World, int par3, int par4, int par5) {
        return ForgeHooks.blockStrength((Block)(Object) this, par1EntityPlayer, par2World, par3, par4, par5);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_410(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
        if (!par1World.isClient) {
            ArrayList<ItemStack> items = this.getBlockDropped(par1World, par2, par3, par4, par5, par7);

            for (ItemStack item : items) {
                if (par1World.random.nextFloat() <= par6) {
                    this.method_422(par1World, par2, par3, par4, item);
                }
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_424(World par1World, PlayerEntity par2EntityPlayer, int par3, int par4, int par5, int par6) {
        par2EntityPlayer.incrementStat(Stats.BLOCK_STATS[this.id], 1);
        par2EntityPlayer.addExhaustion(0.025F);
        if (this.canSilkHarvest(par1World, par2EntityPlayer, par3, par4, par5, par6) && EnchantmentHelper.method_4653(par2EntityPlayer)) {
            ItemStack var8 = this.method_448(par6);
            if (var8 != null) {
                this.method_422(par1World, par3, par4, par5, var8);
            }
        } else {
            int var7 = EnchantmentHelper.method_4654(par2EntityPlayer);
            this.method_445(par1World, par3, par4, par5, par6, var7);
        }

    }

    // Forge Methods
    @Override
    public int getLightValue(BlockView world, int x, int y, int z) {
        return field_496[this.id];
    }

    @Override
    public boolean isLadder(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(World world, int x, int y, int z) {
        return this.material.isOpaque() && this.renderAsNormalBlock();
    }

    @Override
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
        int meta = world.getBlockData(x, y, z);
        if (((Object)this) instanceof SlabBlock) {
            return (meta & 8) == 8 && side == ForgeDirection.UP || this.hasTransperancy();
        } else if (((Object)this) instanceof FarmlandBlock) {
            return side != ForgeDirection.DOWN && side != ForgeDirection.UP;
        } else if (((Object)this) instanceof StairsBlock) {
            boolean flipped = (meta & 4) != 0;
            return (meta & 3) + side.ordinal() == 5 || side == ForgeDirection.UP && flipped;
        } else {
            return this.isBlockNormalCube(world, x, y, z);
        }
    }

    @Override
    public boolean isBlockReplaceable(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isBlockBurning(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isAirBlock(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(PlayerEntity player, int meta) {
        return ForgeHooks.canHarvestBlock(((Block)(Object)this), player, meta);
    }

    @Override
    public boolean removeBlockByPlayer(World world, PlayerEntity player, int x, int y, int z) {
        return world.method_3690(x, y, z, 0);
    }

    @Override
    public void addCreativeItems(ArrayList itemList) {
    }

    @Override
    public int getFlammability(BlockView world, int x, int y, int z, int metadata, ForgeDirection face) {
        return ReflectionUtils.Block_blockFlammability[this.id];
    }

    @Override
    public boolean isFlammable(BlockView world, int x, int y, int z, int metadata, ForgeDirection face) {
        return this.getFlammability(world, x, y, z, metadata, face) > 0;
    }

    @Override
    public int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face) {
        return ReflectionUtils.Block_blockFireSpreadSpeed[this.id];
    }

    @Override
    public boolean isFireSource(World world, int x, int y, int z, int metadata, ForgeDirection side) {
        if (this.id == NETHERRACK.id && side == ForgeDirection.UP) {
            return true;
        } else {
            return world.dimension instanceof TheEndDimension && this.id == BEDROCK.id && side == ForgeDirection.UP;
        }
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return this.blockEntity;
    }

    @Override
    public BlockEntity createTileEntity(World world, int metadata) {
        return ((Object)this) instanceof BlockWithEntity ? ((BlockWithEntity)((Object)this)).createNewTileEntity(world, metadata) : null;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return this.getBonusDrops(fortune, random);
    }

    @Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList();
        int count = this.quantityDropped(metadata, fortune, world.random);

        for(int i = 0; i < count; ++i) {
            int id = this.method_398(metadata, world.random, 0);
            if (id > 0) {
                ret.add(new ItemStack(id, 1, this.method_431(metadata)));
            }
        }

        return ret;
    }

    @Override
    public boolean canSilkHarvest(World world, PlayerEntity player, int x, int y, int z, int metadata) {
        if (!(((Object)this) instanceof GlassBlock) && !(((Object)this) instanceof EnderChestBlock)) {
            return this.renderAsNormalBlock() && !this.hasTileEntity(metadata);
        } else {
            return true;
        }
    }

    @Override
    public boolean canCreatureSpawn(EntityCategory type, World world, int x, int y, int z) {
        int meta = world.getBlockData(x, y, z);
        if (((Object)this) instanceof StoneSlabBlock) {
            if (!MinecraftForge.SPAWNER_ALLOW_ON_INVERTED) {
                return method_467(this.id);
            } else {
                return (meta & 8) == 8 || this.hasTransperancy();
            }
        } else if (((Object)this) instanceof StairsBlock) {
            if (MinecraftForge.SPAWNER_ALLOW_ON_INVERTED) {
                return (meta & 4) != 0;
            } else {
                return method_467(this.id);
            }
        } else {
            return this.isBlockSolidOnSide(world, x, y, z, ForgeDirection.UP);
        }
    }

    @Override
    public boolean isBed(World world, int x, int y, int z, MobEntity player) {
        return this.id == BED.id;
    }

    @Override
    public BlockPos getBedSpawnPosition(World world, int x, int y, int z, PlayerEntity player) {
        return BedBlock.method_279(world, x, y, z, 0);
    }

    @Override
    public void setBedOccupied(World world, int x, int y, int z, PlayerEntity player, boolean occupied) {
        BedBlock.method_277(world, x, y, z, occupied);
    }

    @Override
    public int getBedDirection(BlockView world, int x, int y, int z) {
        return BedBlock.method_297(world.getBlockData(x, y, z));
    }

    @Override
    public boolean isBedFoot(BlockView world, int x, int y, int z) {
        return BedBlock.method_278(world.getBlockData(x, y, z));
    }

    @Override
    public void beginLeavesDecay(World world, int x, int y, int z) {
    }

    @Override
    public boolean canSustainLeaves(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isLeaves(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(World world, int x, int y, int z) {
        return !field_493[this.id];
    }

    @Override
    public boolean isWood(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isGenMineableReplaceable(World world, int x, int y, int z) {
        return this.id == STONE_BLOCK.id;
    }

    @Override
    public String getTextureFile() {
        return this.currentTexture;
    }

    @Override
    public void setTextureFile(String texture) {
        this.currentTexture = texture;
        this.isDefaultTexture = false;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
        return this.getBlastResistance(par1Entity);
    }

    @Override
    public boolean canConnectRedstone(BlockView world, int x, int y, int z, int side) {
        return BLOCKS[this.id].emitsRedstonePower() && side != -1;
    }

    @Override
    public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
        if (world.method_3784(x, y, z)) {
            return true;
        } else {
            int id = world.getBlock(x, y, z);
            return id == WOODEN_FENCE.id || id == NETHER_BRICK_FENCE.id || id == GLASS_BLOCK.id || id == COBBLESTONE_WALL.id;
        }
    }

    @Override
    public boolean canRenderInPass(int pass) {
        return pass == this.method_479();
    }

    @Override
    public ItemStack getPickBlock(BlockHitResult target, World world, int x, int y, int z) {
        int id = this.method_407(world, x, y, z);
        if (id == 0) {
            return null;
        } else {
            Item item = Item.ITEMS[id];
            return item == null ? null : new ItemStack(id, 1, this.method_463(world, x, y, z));
        }
    }

    @Override
    public boolean isBlockFoliage(World world, int x, int y, int z) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean addBlockHitEffects(World worldObj, BlockHitResult target, ParticleManager effectRenderer) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean addBlockDestroyEffects(World world, int x, int y, int z, int meta, ParticleManager effectRenderer) {
        return false;
    }

    @Override
    public boolean canSustainPlant(World world, int x, int y, int z, ForgeDirection direction, IPlantable plant) {
        int plantID = plant.getPlantID(world, x, y + 1, z);
        EnumPlantType plantType = plant.getPlantType(world, x, y + 1, z);
        if (plantID == CACTUS.id && this.id == CACTUS.id) {
            return true;
        } else if (plantID == SUGARCANE.id && this.id == SUGARCANE.id) {
            return true;
        } else if (plant instanceof FlowerBlock && ((FlowerBlockAccessor)plant).method_283_invoker(this.id)) {
            return true;
        } else {
            switch (plantType) {
                case Desert:
                    return this.id == SAND_BLOCK.id;
                case Nether:
                    return this.id == SOULSAND.id;
                case Crop:
                    return this.id == FARMLAND.id;
                case Cave:
                    return this.isBlockSolidOnSide(world, x, y, z, ForgeDirection.UP);
                case Plains:
                    return this.id == GRASS_BLOCK.id || this.id == DIRT.id;
                case Water:
                    return world.getMaterial(x, y, z) == Material.WATER && world.getBlockData(x, y, z) == 0;
                case Beach:
                    boolean isBeach = this.id == GRASS_BLOCK.id || this.id == DIRT.id || this.id == SAND_BLOCK.id;
                    boolean hasWater = world.getMaterial(x - 1, y - 1, z) == Material.WATER
                            || world.getMaterial(x + 1, y - 1, z) == Material.WATER
                            || world.getMaterial(x, y - 1, z - 1) == Material.WATER
                            || world.getMaterial(x, y - 1, z + 1) == Material.WATER;
                    return isBeach && hasWater;
                default:
                    return false;
            }
        }
    }

    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        if (this.id == FARMLAND.id) {
            return world.getBlockData(x, y, z) > 0;
        } else {
            return false;
        }
    }

    @Override
    public int getLightOpacity(World world, int x, int y, int z) {
        return field_494[this.id];
    }

    @Override
    public boolean canDragonDestroy(World world, int x, int y, int z) {
        return this.id != OBSIDIAN.id && this.id != END_STONE.id && this.id != BEDROCK.id;
    }

    @Override
    public boolean isDefaultTexture() {
        return this.isDefaultTexture;
    }
}
