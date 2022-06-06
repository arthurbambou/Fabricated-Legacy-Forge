package fr.catcore.fabricatedforge.mixin.forgefml.entity;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.entity.SpawnEntry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpecialSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

@Mixin(MobSpawnerHelper.class)
public abstract class MobSpawnerHelperMixin {

    @Shadow private static HashMap field_4593;

    @Shadow
    protected static Vec3i method_3799(World world, int i, int j) {
        return null;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static final int tickSpawners(ServerWorld par0WorldServer, boolean par1, boolean par2, boolean par3) {
        if (!par1 && !par2) {
            return 0;
        } else {
            field_4593.clear();

            for(int var4 = 0; var4 < par0WorldServer.playerEntities.size(); ++var4) {
                PlayerEntity var5 = (PlayerEntity)par0WorldServer.playerEntities.get(var4);
                int var6 = MathHelper.floor(var5.x / 16.0);
                int var7 = MathHelper.floor(var5.z / 16.0);
                byte var8 = 8;

                for(int var9 = -var8; var9 <= var8; ++var9) {
                    for(int var10 = -var8; var10 <= var8; ++var10) {
                        boolean var11 = var9 == -var8 || var9 == var8 || var10 == -var8 || var10 == var8;
                        ChunkPos var12 = new ChunkPos(var9 + var6, var10 + var7);
                        if (!var11) {
                            field_4593.put(var12, false);
                        } else if (!field_4593.containsKey(var12)) {
                            field_4593.put(var12, true);
                        }
                    }
                }
            }

            int var351 = 0;
            BlockPos var32 = par0WorldServer.getWorldSpawnPos();

            for(EntityCategory var35 : EntityCategory.values()) {
                if ((!var35.isHostile() || par2)
                        && (var35.isHostile() || par1)
                        && (!var35.isBreedable() || par3)
                        && par0WorldServer.method_3616(var35.getCategoryClass()) <= var35.getSpawnCap() * field_4593.size() / 256) {
                    Iterator var37 = field_4593.keySet().iterator();
                    ArrayList<ChunkPos> tmp = new ArrayList(field_4593.keySet());
                    Collections.shuffle(tmp);

                    label122:
                    for(ChunkPos var36 : tmp) {
                        if (!(Boolean) field_4593.get(var36)) {
                            Vec3i var38 = method_3799(par0WorldServer, var36.x, var36.z);
                            int var13 = var38.field_4613;
                            int var14 = var38.field_4614;
                            int var15 = var38.field_4615;
                            if (!par0WorldServer.method_3783(var13, var14, var15) && par0WorldServer.getMaterial(var13, var14, var15) == var35.getMaterial()) {
                                int var16 = 0;

                                for(int var17 = 0; var17 < 3; ++var17) {
                                    int var18 = var13;
                                    int var19 = var14;
                                    int var20 = var15;
                                    byte var21 = 6;
                                    SpawnEntry var22 = null;

                                    for(int var23 = 0; var23 < 4; ++var23) {
                                        var18 += par0WorldServer.random.nextInt(var21) - par0WorldServer.random.nextInt(var21);
                                        var19 += par0WorldServer.random.nextInt(1) - par0WorldServer.random.nextInt(1);
                                        var20 += par0WorldServer.random.nextInt(var21) - par0WorldServer.random.nextInt(var21);
                                        if (method_3798(var35, par0WorldServer, var18, var19, var20)) {
                                            float var24 = (float)var18 + 0.5F;
                                            float var25 = (float)var19;
                                            float var26 = (float)var20 + 0.5F;
                                            if (par0WorldServer.getClosestPlayer((double)var24, (double)var25, (double)var26, 24.0) == null) {
                                                float var27 = var24 - (float)var32.x;
                                                float var28 = var25 - (float)var32.y;
                                                float var29 = var26 - (float)var32.z;
                                                float var30 = var27 * var27 + var28 * var28 + var29 * var29;
                                                if (var30 >= 576.0F) {
                                                    if (var22 == null) {
                                                        var22 = par0WorldServer.method_2136(var35, var18, var19, var20);
                                                        if (var22 == null) {
                                                            break;
                                                        }
                                                    }

                                                    MobEntity var39;
                                                    try {
                                                        var39 = (MobEntity)var22.type.getConstructor(World.class).newInstance(par0WorldServer);
                                                    } catch (Exception var341) {
                                                        var341.printStackTrace();
                                                        return var351;
                                                    }

                                                    var39.refreshPositionAndAngles(
                                                            (double)var24, (double)var25, (double)var26, par0WorldServer.random.nextFloat() * 360.0F, 0.0F
                                                    );
                                                    if (var39.canSpawn()) {
                                                        ++var16;
                                                        par0WorldServer.spawnEntity(var39);
                                                        method_3797(var39, par0WorldServer, var24, var25, var26);
                                                        if (var16 >= var39.getLimitPerChunk()) {
                                                            continue label122;
                                                        }
                                                    }

                                                    var351 += var16;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return var351;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static boolean method_3798(EntityCategory par0EnumCreatureType, World par1World, int par2, int par3, int par4) {
        if (par0EnumCreatureType.getMaterial() == Material.WATER) {
            return par1World.getMaterial(par2, par3, par4).isFluid() && !par1World.method_3783(par2, par3 + 1, par4);
        } else if (!par1World.method_3784(par2, par3 - 1, par4)) {
            return false;
        } else {
            int var5 = par1World.getBlock(par2, par3 - 1, par4);
            boolean spawnBlock = Block.BLOCKS[var5] != null && Block.BLOCKS[var5].canCreatureSpawn(par0EnumCreatureType, par1World, par2, par3 - 1, par4);
            return spawnBlock
                    && var5 != Block.BEDROCK.id
                    && !par1World.method_3783(par2, par3, par4)
                    && !par1World.getMaterial(par2, par3, par4).isFluid()
                    && !par1World.method_3783(par2, par3 + 1, par4);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private static void method_3797(MobEntity par0EntityLiving, World par1World, float par2, float par3, float par4) {
        if (!MinecraftForge.EVENT_BUS.post(new LivingSpecialSpawnEvent(par0EntityLiving, par1World, par2, par3, par4))) {
            par0EntityLiving.method_4475();
        }
    }
}
