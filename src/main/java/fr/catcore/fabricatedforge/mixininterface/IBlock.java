package fr.catcore.fabricatedforge.mixininterface;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;

import java.util.ArrayList;
import java.util.Random;

public interface IBlock {

    int getLightValue(BlockView world, int x, int y, int z);
    boolean isLadder(World world, int x, int y, int z);
    boolean isBlockNormalCube(World world, int x, int y, int z);
    boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side);
    boolean isBlockReplaceable(World world, int x, int y, int z);
    boolean isBlockBurning(World world, int x, int y, int z);
    boolean isAirBlock(World world, int x, int y, int z);
    boolean canHarvestBlock(PlayerEntity player, int meta);
    boolean removeBlockByPlayer(World world, PlayerEntity player, int x, int y, int z);
    void addCreativeItems(ArrayList itemList);
    int getFlammability(BlockView world, int x, int y, int z, int metadata, ForgeDirection face);
    boolean isFlammable(BlockView world, int x, int y, int z, int metadata, ForgeDirection face);
    int getFireSpreadSpeed(World world, int x, int y, int z, int metadata, ForgeDirection face);
    boolean isFireSource(World world, int x, int y, int z, int metadata, ForgeDirection side);
    boolean hasTileEntity(int metadata);
    BlockEntity createTileEntity(World world, int metadata);
    int quantityDropped(int meta, int fortune, Random random);
    ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune);
    boolean canSilkHarvest(World world, PlayerEntity player, int x, int y, int z, int metadata);
    boolean canCreatureSpawn(EntityCategory type, World world, int x, int y, int z);
    boolean isBed(World world, int x, int y, int z, MobEntity player);
    BlockPos getBedSpawnPosition(World world, int x, int y, int z, PlayerEntity player);
    void setBedOccupied(World world, int x, int y, int z, PlayerEntity player, boolean occupied);
    int getBedDirection(BlockView world, int x, int y, int z);
    boolean isBedFoot(BlockView world, int x, int y, int z);
    void beginLeavesDecay(World world, int x, int y, int z);
    boolean canSustainLeaves(World world, int x, int y, int z);
    boolean isLeaves(World world, int x, int y, int z);
    boolean canBeReplacedByLeaves(World world, int x, int y, int z);
    boolean isWood(World world, int x, int y, int z);
    boolean isGenMineableReplaceable(World world, int x, int y, int z);
    String getTextureFile();
    void setTextureFile(String texture);
    float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ);
    boolean canConnectRedstone(BlockView world, int x, int y, int z, int side);
    boolean canPlaceTorchOnTop(World world, int x, int y, int z);
    boolean canRenderInPass(int pass);
    ItemStack getPickBlock(BlockHitResult target, World world, int x, int y, int z);
    boolean isBlockFoliage(World world, int x, int y, int z);
    @Environment(EnvType.CLIENT)
    boolean addBlockHitEffects(World worldObj, BlockHitResult target, ParticleManager effectRenderer);
    @Environment(EnvType.CLIENT)
    boolean addBlockDestroyEffects(World world, int x, int y, int z, int meta, ParticleManager effectRenderer);
    boolean canSustainPlant(World world, int x, int y, int z, ForgeDirection direction, IPlantable plant);
    boolean isFertile(World world, int x, int y, int z);
    int getLightOpacity(World world, int x, int y, int z);
    boolean canDragonDestroy(World world, int x, int y, int z);

    boolean isDefaultTexture();
}
