package fr.catcore.fabricatedforge.mixin.forgefml.world;

import com.google.common.collect.SetMultimap;
import fr.catcore.fabricatedforge.mixininterface.IWorld;
import fr.catcore.fabricatedforge.forged.ReflectionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicates;
import net.minecraft.util.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.VillageState;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import org.spongepowered.asm.mixin.*;

import java.util.*;

@Mixin(World.class)
public abstract class WorldMixin implements BlockView, IWorld {

    @Mutable
    @Shadow @Final public Dimension dimension;

    @Shadow public abstract boolean isPosLoaded(int x, int y, int z);

    @Shadow public abstract Chunk getChunkFromPos(int x, int z);

    @Shadow protected ChunkProvider chunkProvider;

    @Shadow protected abstract ChunkProvider getChunkCache();

    @Shadow public abstract void calculateAmbientDarkness();
    @Shadow protected LevelProperties levelProperties;

    @Shadow protected List eventListeners;

    @Shadow protected abstract boolean isChunkLoaded(int chunkX, int chunkZ);

    @Shadow public List playerEntities;

    @Shadow public abstract void method_3669();

    @Shadow public abstract Chunk getChunk(int chunkX, int chunkZ);

    @Shadow public List loadedEntities;

    @Shadow protected abstract void onEntitySpawned(Entity entity);

    @Shadow public abstract float getSkyAngle(float tickDelta);

    @Shadow public abstract float getRainGradient(float offset);

    @Shadow public abstract float getThunderGradient(float offset);
    @Shadow private long cloudColor;
    @Mutable
    @Shadow @Final public Profiler profiler;
    @Shadow public List entities;
    @Shadow protected List unloadedEntities;

    @Shadow protected abstract void onEntityRemoved(Entity entity);

    @Shadow public abstract void method_3705(Entity entity);

    @Shadow private boolean iteratingTickingBlockEntities;
    @Shadow public List blockEntities;
    @Shadow private List unloadedBlockEntities;
    @Shadow private List pendingBlockEntities;

    @Shadow public abstract boolean isRegionLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);

    @Shadow protected float rainGradient;
    @Shadow protected float thunderGradient;
    @Shadow protected float thunderGradientPrev;
    @Shadow protected float rainGradientPrev;
    @Shadow public Random random;
    @Shadow
    public int field_4553;
    @Shadow protected Set field_4530;
    @Shadow private int field_4534;

    @Shadow public abstract void method_3736(int i, int j, int k);

    @Shadow public abstract int method_3667(LightType lightType, int x, int y, int z);

    @Shadow public abstract boolean isRegionAroundLoaded(int x, int y, int z, int radius);

    @Shadow public abstract int method_3651(int i, int j, int k);

    @Shadow protected abstract int method_3600(int i, int j, int k, int l, int m, int n);

    @Shadow
    int[] updateLightBlocks;

    @Shadow public abstract void method_3668(LightType lightType, int x, int y, int z, int i);

    @Shadow private List field_4535;

    @Shadow public abstract boolean hasEntityIn(Box box, Entity exclusion);

    @Shadow protected boolean spawnAnimals;
    @Shadow protected boolean spawnMonsters;
    @Shadow public boolean isClient;
    @Mutable
    @Shadow @Final protected SaveHandler saveHandler;
    @Shadow public PersistentStateManager persistentStateManager;
    @Shadow public VillageState villageState;
    @Shadow protected int field_23088;

    // Forge Fields
    @Unique
    private static PersistentStateManager s_mapStorage;
    @Unique
    private static SaveHandler s_savehandler;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public Biome method_3773(int par1, int par2) {
        return this.dimension.getBiomeGenForCoords(par1, par2);
    }

    @Override
    public Biome getBiomeGenForCoordsBody(int par1, int par2) {
        if (this.isPosLoaded(par1, 0, par2)) {
            Chunk var3 = this.getChunkFromPos(par1, par2);
            if (var3 != null) {
                return var3.method_3883(par1 & 15, par2 & 15, this.dimension.biomeSource);
            }
        }

        return this.dimension.biomeSource.method_3853(par1, par2);
    }

    /* TODO: Overwrite constructor this way.

    *   public World(SaveHandler par1ISaveHandler, String par2Str, Dimension par3WorldProvider, LevelInfo par4WorldSettings, Profiler par5Profiler) {
            this.saveHandler = par1ISaveHandler;
            this.profiler = par5Profiler;
            this.levelProperties = new LevelProperties(par4WorldSettings, par2Str);
            this.dimension = par3WorldProvider;
        }
    */

    @Environment(EnvType.CLIENT)
    @Override
    public void finishSetup() {
        this.dimension.copyFromWorls((World)(Object) this);
        this.persistentStateManager = new PersistentStateManager(this.saveHandler);
        VillageState var6 = (VillageState)this.persistentStateManager.getOrCreate(VillageState.class, "villages");
        if (var6 == null) {
            this.villageState = new VillageState((World)(Object) this);
            this.persistentStateManager.replace("villages", this.villageState);
        } else {
            this.villageState = var6;
            this.villageState.setWorld((World)(Object) this);
        }

        this.dimension.copyFromWorls((World)(Object) this);
        this.chunkProvider = this.getChunkCache();
        this.calculateAmbientDarkness();
        this.initWeatherGradients();
    }

    @Unique
    private PersistentStateManager getMapStorage(SaveHandler savehandler) {
        if (s_savehandler != savehandler || s_mapStorage == null) {
            s_mapStorage = new PersistentStateManager(savehandler);
            s_savehandler = savehandler;
        }

        return s_mapStorage;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean isAir(int par1, int par2, int par3) {
        int id = this.getBlock(par1, par2, par3);
        return id == 0 || Block.BLOCKS[id] == null || Block.BLOCKS[id].isAirBlock((World)(Object) this, par1, par2, par3);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean hasBlockEntity(int par1, int par2, int par3) {
        int var4 = this.getBlock(par1, par2, par3);
        int meta = this.getBlockData(par1, par2, par3);
        return Block.BLOCKS[var4] != null && Block.BLOCKS[var4].hasTileEntity(meta);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean isDay() {
        return this.dimension.isDaytime();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public BlockHitResult rayTrace(Vec3d par1Vec3, Vec3d par2Vec3, boolean par3, boolean par4) {
        if (Double.isNaN(par1Vec3.x) || Double.isNaN(par1Vec3.y) || Double.isNaN(par1Vec3.z)) {
            return null;
        } else if (!Double.isNaN(par2Vec3.x) && !Double.isNaN(par2Vec3.y) && !Double.isNaN(par2Vec3.z)) {
            int var5 = MathHelper.floor(par2Vec3.x);
            int var6 = MathHelper.floor(par2Vec3.y);
            int var7 = MathHelper.floor(par2Vec3.z);
            int var8 = MathHelper.floor(par1Vec3.x);
            int var9 = MathHelper.floor(par1Vec3.y);
            int var10 = MathHelper.floor(par1Vec3.z);
            int var11 = this.getBlock(var8, var9, var10);
            int var12 = this.getBlockData(var8, var9, var10);
            Block var13 = Block.BLOCKS[var11];
            if (var13 != null && (!par4 || var13 == null || var13.method_454((World)(Object) this, var8, var9, var10) != null) && var11 > 0 && var13.method_400(var12, par3)) {
                BlockHitResult var14 = var13.method_414((World)(Object) this, var8, var9, var10, par1Vec3, par2Vec3);
                if (var14 != null) {
                    return var14;
                }
            }

            var11 = 200;

            while(var11-- >= 0) {
                if (Double.isNaN(par1Vec3.x) || Double.isNaN(par1Vec3.y) || Double.isNaN(par1Vec3.z)) {
                    return null;
                }

                if (var8 == var5 && var9 == var6 && var10 == var7) {
                    return null;
                }

                boolean var39 = true;
                boolean var40 = true;
                boolean var41 = true;
                double var15 = 999.0;
                double var17 = 999.0;
                double var19 = 999.0;
                if (var5 > var8) {
                    var15 = (double)var8 + 1.0;
                } else if (var5 < var8) {
                    var15 = (double)var8 + 0.0;
                } else {
                    var39 = false;
                }

                if (var6 > var9) {
                    var17 = (double)var9 + 1.0;
                } else if (var6 < var9) {
                    var17 = (double)var9 + 0.0;
                } else {
                    var40 = false;
                }

                if (var7 > var10) {
                    var19 = (double)var10 + 1.0;
                } else if (var7 < var10) {
                    var19 = (double)var10 + 0.0;
                } else {
                    var41 = false;
                }

                double var21 = 999.0;
                double var23 = 999.0;
                double var25 = 999.0;
                double var27 = par2Vec3.x - par1Vec3.x;
                double var29 = par2Vec3.y - par1Vec3.y;
                double var31 = par2Vec3.z - par1Vec3.z;
                if (var39) {
                    var21 = (var15 - par1Vec3.x) / var27;
                }

                if (var40) {
                    var23 = (var17 - par1Vec3.y) / var29;
                }

                if (var41) {
                    var25 = (var19 - par1Vec3.z) / var31;
                }

                boolean var33 = false;
                byte var42;
                if (var21 < var23 && var21 < var25) {
                    if (var5 > var8) {
                        var42 = 4;
                    } else {
                        var42 = 5;
                    }

                    par1Vec3.x = var15;
                    par1Vec3.y += var29 * var21;
                    par1Vec3.z += var31 * var21;
                } else if (var23 < var25) {
                    if (var6 > var9) {
                        var42 = 0;
                    } else {
                        var42 = 1;
                    }

                    par1Vec3.x += var27 * var23;
                    par1Vec3.y = var17;
                    par1Vec3.z += var31 * var23;
                } else {
                    if (var7 > var10) {
                        var42 = 2;
                    } else {
                        var42 = 3;
                    }

                    par1Vec3.x += var27 * var25;
                    par1Vec3.y += var29 * var25;
                    par1Vec3.z = var19;
                }

                Vec3d var34 = this.method_4696().getOrCreate(par1Vec3.x, par1Vec3.y, par1Vec3.z);
                var8 = (int)(var34.x = (double)MathHelper.floor(par1Vec3.x));
                if (var42 == 5) {
                    --var8;
                    ++var34.x;
                }

                var9 = (int)(var34.y = (double)MathHelper.floor(par1Vec3.y));
                if (var42 == 1) {
                    --var9;
                    ++var34.y;
                }

                var10 = (int)(var34.z = (double)MathHelper.floor(par1Vec3.z));
                if (var42 == 3) {
                    --var10;
                    ++var34.z;
                }

                int var35 = this.getBlock(var8, var9, var10);
                int var36 = this.getBlockData(var8, var9, var10);
                Block var37 = Block.BLOCKS[var35];
                if ((!par4 || var37 == null || var37.method_454((World)(Object) this, var8, var9, var10) != null) && var35 > 0 && var37.method_400(var36, par3)) {
                    BlockHitResult var38 = var37.method_414((World)(Object) this, var8, var9, var10, par1Vec3, par2Vec3);
                    if (var38 != null) {
                        return var38;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void playSound(Entity par1Entity, String par2Str, float par3, float par4) {
        PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(par1Entity, par2Str, par3, par4);
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            par2Str = event.name;
            if (par1Entity != null && par2Str != null) {
                for(WorldEventListener var6 : (List<WorldEventListener>) this.eventListeners) {
                    var6.playSound(par2Str, par1Entity.x, par1Entity.y - (double)par1Entity.heightOffset, par1Entity.z, par3, par4);
                }
            }

        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean spawnEntity(Entity par1Entity) {
        int var2 = MathHelper.floor(par1Entity.x / 16.0);
        int var3 = MathHelper.floor(par1Entity.z / 16.0);
        boolean var4 = false;
        if (par1Entity instanceof PlayerEntity) {
            var4 = true;
        }

        if (!var4 && !this.isChunkLoaded(var2, var3)) {
            return false;
        } else {
            if (par1Entity instanceof PlayerEntity) {
                PlayerEntity var5 = (PlayerEntity)par1Entity;
                this.playerEntities.add(var5);
                this.method_3669();
            }

            if (!var4 && MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(par1Entity, (World)(Object) this))) {
                return false;
            } else {
                this.getChunk(var2, var3).addEntity(par1Entity);
                this.loadedEntities.add(par1Entity);
                this.onEntitySpawned(par1Entity);
                return true;
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public Vec3d method_3631(Entity par1Entity, float par2) {
        return this.dimension.getSkyColor(par1Entity, par2);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Vec3d getSkyColorBody(Entity par1Entity, float par2) {
        float var3 = this.getSkyAngle(par2);
        float var4 = MathHelper.cos(var3 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        if (var4 < 0.0F) {
            var4 = 0.0F;
        }

        if (var4 > 1.0F) {
            var4 = 1.0F;
        }

        int var5 = MathHelper.floor(par1Entity.x);
        int var6 = MathHelper.floor(par1Entity.z);
        Biome var7 = this.method_3773(var5, var6);
        float var8 = var7.getTemperatureValue();
        int var9 = var7.getSkyColor(var8);
        float var10 = (float)(var9 >> 16 & 0xFF) / 255.0F;
        float var11 = (float)(var9 >> 8 & 0xFF) / 255.0F;
        float var12 = (float)(var9 & 0xFF) / 255.0F;
        var10 *= var4;
        var11 *= var4;
        var12 *= var4;
        float var13 = this.getRainGradient(par2);
        if (var13 > 0.0F) {
            float var14 = (var10 * 0.3F + var11 * 0.59F + var12 * 0.11F) * 0.6F;
            float var15 = 1.0F - var13 * 0.75F;
            var10 = var10 * var15 + var14 * (1.0F - var15);
            var11 = var11 * var15 + var14 * (1.0F - var15);
            var12 = var12 * var15 + var14 * (1.0F - var15);
        }

        float var14 = this.getThunderGradient(par2);
        if (var14 > 0.0F) {
            float var15 = (var10 * 0.3F + var11 * 0.59F + var12 * 0.11F) * 0.2F;
            float var16 = 1.0F - var14 * 0.75F;
            var10 = var10 * var16 + var15 * (1.0F - var16);
            var11 = var11 * var16 + var15 * (1.0F - var16);
            var12 = var12 * var16 + var15 * (1.0F - var16);
        }

        if (this.field_4553 > 0) {
            float var15 = (float)this.field_4553 - par2;
            if (var15 > 1.0F) {
                var15 = 1.0F;
            }

            var15 *= 0.45F;
            var10 = var10 * (1.0F - var15) + 0.8F * var15;
            var11 = var11 * (1.0F - var15) + 0.8F * var15;
            var12 = var12 * (1.0F - var15) + 1.0F * var15;
        }

        return this.method_4696().getOrCreate((double)var10, (double)var11, (double)var12);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public Vec3d getCloudColor(float par1) {
        return this.dimension.drawClouds(par1);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Vec3d drawCloudsBody(float par1) {
        float var2 = this.getSkyAngle(par1);
        float var3 = MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        if (var3 < 0.0F) {
            var3 = 0.0F;
        }

        if (var3 > 1.0F) {
            var3 = 1.0F;
        }

        float var4 = (float)(this.cloudColor >> 16 & 255L) / 255.0F;
        float var5 = (float)(this.cloudColor >> 8 & 255L) / 255.0F;
        float var6 = (float)(this.cloudColor & 255L) / 255.0F;
        float var7 = this.getRainGradient(par1);
        if (var7 > 0.0F) {
            float var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
            float var9 = 1.0F - var7 * 0.95F;
            var4 = var4 * var9 + var8 * (1.0F - var9);
            var5 = var5 * var9 + var8 * (1.0F - var9);
            var6 = var6 * var9 + var8 * (1.0F - var9);
        }

        var4 *= var3 * 0.9F + 0.1F;
        var5 *= var3 * 0.9F + 0.1F;
        var6 *= var3 * 0.85F + 0.15F;
        float var8 = this.getThunderGradient(par1);
        if (var8 > 0.0F) {
            float var9 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
            float var10 = 1.0F - var8 * 0.95F;
            var4 = var4 * var10 + var9 * (1.0F - var10);
            var5 = var5 * var10 + var9 * (1.0F - var10);
            var6 = var6 * var10 + var9 * (1.0F - var10);
        }

        return this.method_4696().getOrCreate((double)var4, (double)var5, (double)var6);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int method_3708(int par1, int par2) {
        Chunk var3 = this.getChunkFromPos(par1, par2);
        int var4 = var3.getHighestNonEmptySectionYOffset() + 15;
        par1 &= 15;

        for(int var7 = par2 & 15; var4 > 0; --var4) {
            int var5 = var3.method_3879(par1, var4, var7);
            if (var5 != 0
                    && Block.BLOCKS[var5].material.blocksMovement()
                    && Block.BLOCKS[var5].material != Material.FOILAGE
                    && !Block.BLOCKS[var5].isBlockFoliage((World)(Object) this, par1, var4, var7)) {
                return var4 + 1;
            }
        }

        return -1;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public float method_3707(float par1) {
        return this.dimension.getStarBrightness(par1);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getStarBrightnessBody(float par1) {
        float var2 = this.getSkyAngle(par1);
        float var3 = 1.0F - (MathHelper.cos(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
        if (var3 < 0.0F) {
            var3 = 0.0F;
        }

        if (var3 > 1.0F) {
            var3 = 1.0F;
        }

        return var3 * var3 * 0.5F;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void tickEntities() {
        this.profiler.push("entities");
        this.profiler.push("global");

        for(int var1 = 0; var1 < this.entities.size(); ++var1) {
            Entity var2 = (Entity)this.entities.get(var1);
            var2.tick();
            if (var2.removed) {
                this.entities.remove(var1--);
            }
        }

        this.profiler.swap("remove");
        this.loadedEntities.removeAll(this.unloadedEntities);

        for(Entity var2 : (List<Entity>) this.unloadedEntities) {
            int var3 = var2.chunkX;
            int var4 = var2.chunkZ;
            if (var2.updateNeeded && this.isChunkLoaded(var3, var4)) {
                this.getChunk(var3, var4).removeEntity(var2);
            }
        }

        for(Entity var2 : (List<Entity>) this.unloadedEntities) {
            this.onEntityRemoved(var2);
        }

        this.unloadedEntities.clear();
        this.profiler.swap("regular");

        for(int var91 = 0; var91 < this.loadedEntities.size(); ++var91) {
            Entity var2 = (Entity)this.loadedEntities.get(var91);
            if (var2.vehicle != null) {
                if (!var2.vehicle.removed && var2.vehicle.rider == var2) {
                    continue;
                }

                var2.vehicle.rider = null;
                var2.vehicle = null;
            }

            this.profiler.push("tick");
            if (!var2.removed) {
                this.method_3705(var2);
            }

            this.profiler.pop();
            this.profiler.push("remove");
            if (var2.removed) {
                int var3 = var2.chunkX;
                int var4 = var2.chunkZ;
                if (var2.updateNeeded && this.isChunkLoaded(var3, var4)) {
                    this.getChunk(var3, var4).removeEntity(var2);
                }

                this.loadedEntities.remove(var91--);
                this.onEntityRemoved(var2);
            }

            this.profiler.pop();
        }

        this.profiler.swap("tileEntities");
        this.iteratingTickingBlockEntities = true;
        Iterator var14 = this.blockEntities.iterator();

        while(var14.hasNext()) {
            BlockEntity var6 = (BlockEntity)var14.next();
            if (!var6.isRemoved() && var6.hasWorld() && this.isPosLoaded(var6.x, var6.y, var6.z)) {
                var6.method_545();
            }

            if (var6.isRemoved()) {
                var14.remove();
                if (this.isChunkLoaded(var6.x >> 4, var6.z >> 4)) {
                    Chunk var8 = this.getChunk(var6.x >> 4, var6.z >> 4);
                    if (var8 != null) {
                        var8.cleanChunkBlockTileEntity(var6.x & 15, var6.y, var6.z & 15);
                    }
                }
            }
        }

        this.iteratingTickingBlockEntities = false;
        if (!this.unloadedBlockEntities.isEmpty()) {
            for(Object tile : this.unloadedBlockEntities) {
                ((BlockEntity)tile).onChunkUnload();
            }

            this.blockEntities.removeAll(this.unloadedBlockEntities);
            this.unloadedBlockEntities.clear();
        }

        this.profiler.swap("pendingTileEntities");
        if (!this.pendingBlockEntities.isEmpty()) {
            for(BlockEntity var9 : (List<BlockEntity>) this.pendingBlockEntities) {
                if (!var9.isRemoved()) {
                    if (!this.blockEntities.contains(var9)) {
                        this.blockEntities.add(var9);
                    }
                } else if (this.isChunkLoaded(var9.x >> 4, var9.z >> 4)) {
                    Chunk var10 = this.getChunk(var9.x >> 4, var9.z >> 4);
                    if (var10 != null) {
                        var10.method_3882(var9.x & 15, var9.y, var9.z & 15, var9);
                    }
                }
            }

            this.pendingBlockEntities.clear();
        }

        this.profiler.pop();
        this.profiler.pop();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void addBlockEntities(Collection par1Collection) {
        List dest = this.iteratingTickingBlockEntities ? this.pendingBlockEntities : this.blockEntities;

        for(Object entity : par1Collection) {
            if (((BlockEntity)entity).canUpdate()) {
                dest.add(entity);
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void checkChunk(Entity par1Entity, boolean par2) {
        int var3 = MathHelper.floor(par1Entity.x);
        int var4 = MathHelper.floor(par1Entity.z);
        boolean isForced = this.getPersistentChunks().containsKey(new ChunkPos(var3 >> 4, var4 >> 4));
        byte var5 = (byte)(isForced ? 0 : 32);
        boolean canUpdate = !par2 || this.isRegionLoaded(var3 - var5, 0, var4 - var5, var3 + var5, 0, var4 + var5);
        if (!canUpdate) {
            EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(par1Entity);
            MinecraftForge.EVENT_BUS.post(event);
            canUpdate = event.canUpdate;
        }

        if (canUpdate) {
            par1Entity.prevTickX = par1Entity.x;
            par1Entity.prevTickY = par1Entity.y;
            par1Entity.prevTickZ = par1Entity.z;
            par1Entity.prevYaw = par1Entity.yaw;
            par1Entity.prevPitch = par1Entity.pitch;
            if (par2 && par1Entity.updateNeeded) {
                if (par1Entity.vehicle != null) {
                    par1Entity.tickRiding();
                } else {
                    par1Entity.tick();
                }
            }

            this.profiler.push("chunkCheck");
            if (Double.isNaN(par1Entity.x) || Double.isInfinite(par1Entity.x)) {
                par1Entity.x = par1Entity.prevTickX;
            }

            if (Double.isNaN(par1Entity.y) || Double.isInfinite(par1Entity.y)) {
                par1Entity.y = par1Entity.prevTickY;
            }

            if (Double.isNaN(par1Entity.z) || Double.isInfinite(par1Entity.z)) {
                par1Entity.z = par1Entity.prevTickZ;
            }

            if (Double.isNaN((double)par1Entity.pitch) || Double.isInfinite((double)par1Entity.pitch)) {
                par1Entity.pitch = par1Entity.prevPitch;
            }

            if (Double.isNaN((double)par1Entity.yaw) || Double.isInfinite((double)par1Entity.yaw)) {
                par1Entity.yaw = par1Entity.prevYaw;
            }

            int var6 = MathHelper.floor(par1Entity.x / 16.0);
            int var7 = MathHelper.floor(par1Entity.y / 16.0);
            int var8 = MathHelper.floor(par1Entity.z / 16.0);
            if (!par1Entity.updateNeeded || par1Entity.chunkX != var6 || par1Entity.chunkY != var7 || par1Entity.chunkZ != var8) {
                if (par1Entity.updateNeeded && this.isChunkLoaded(par1Entity.chunkX, par1Entity.chunkZ)) {
                    this.getChunk(par1Entity.chunkX, par1Entity.chunkZ).removeEntity(par1Entity, par1Entity.chunkY);
                }

                if (this.isChunkLoaded(var6, var8)) {
                    par1Entity.updateNeeded = true;
                    this.getChunk(var6, var8).addEntity(par1Entity);
                } else {
                    par1Entity.updateNeeded = false;
                }
            }

            this.profiler.pop();
            if (par2 && par1Entity.updateNeeded && par1Entity.rider != null) {
                if (!par1Entity.rider.removed && par1Entity.rider.vehicle == par1Entity) {
                    this.method_3705(par1Entity.rider);
                } else {
                    par1Entity.rider.vehicle = null;
                    par1Entity.rider = null;
                }
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean containsFireSource(Box par1AxisAlignedBB) {
        int var2 = MathHelper.floor(par1AxisAlignedBB.minX);
        int var3 = MathHelper.floor(par1AxisAlignedBB.maxX + 1.0);
        int var4 = MathHelper.floor(par1AxisAlignedBB.minY);
        int var5 = MathHelper.floor(par1AxisAlignedBB.maxY + 1.0);
        int var6 = MathHelper.floor(par1AxisAlignedBB.minZ);
        int var7 = MathHelper.floor(par1AxisAlignedBB.maxZ + 1.0);
        if (this.isRegionLoaded(var2, var4, var6, var3, var5, var7)) {
            for(int var8 = var2; var8 < var3; ++var8) {
                for(int var9 = var4; var9 < var5; ++var9) {
                    for(int var10 = var6; var10 < var7; ++var10) {
                        int var11 = this.getBlock(var8, var9, var10);
                        if (var11 == Block.FIRE.id || var11 == Block.field_336.id || var11 == Block.LAVA.id) {
                            return true;
                        }

                        Block block = Block.BLOCKS[var11];
                        if (block != null && block.isBlockBurning((World)(Object) this, var8, var9, var10)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_3603(int par1, int par2, int par3, BlockEntity par4TileEntity) {
        if (par4TileEntity != null && !par4TileEntity.isRemoved()) {
            if (par4TileEntity.canUpdate()) {
                List dest = this.iteratingTickingBlockEntities ? this.pendingBlockEntities : this.blockEntities;
                dest.add(par4TileEntity);
            }

            Chunk chunk = this.getChunk(par1 >> 4, par3 >> 4);
            if (chunk != null) {
                chunk.method_3882(par1 & 15, par2, par3 & 15, par4TileEntity);
            }

        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_3725(int par1, int par2, int par3) {
        Chunk chunk = this.getChunk(par1 >> 4, par3 >> 4);
        if (chunk != null) {
            chunk.method_3913(par1 & 15, par2, par3 & 15);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_3783(int par1, int par2, int par3) {
        Block block = Block.BLOCKS[this.getBlock(par1, par2, par3)];
        return block != null && block.isBlockNormalCube((World)(Object) this, par1, par2, par3);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_3784(int par1, int par2, int par3) {
        return this.isBlockSolidOnSide(par1, par2, par3, ForgeDirection.UP);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_3656(int par1, int par2, int par3, boolean par4) {
        if (par1 >= -30000000 && par3 >= -30000000 && par1 < 30000000 && par3 < 30000000) {
            Chunk var5 = this.chunkProvider.getChunk(par1 >> 4, par3 >> 4);
            if (var5 != null && !var5.isEmpty()) {
                Block var6 = Block.BLOCKS[this.getBlock(par1, par2, par3)];
                return var6 == null ? false : this.method_3783(par1, par2, par3);
            } else {
                return par4;
            }
        } else {
            return par4;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_3645(boolean par1, boolean par2) {
        this.dimension.setAllowedSpawnTypes(par1, par2);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void initWeatherGradients() {
        this.dimension.calculateInitialWeather();
    }

    @Override
    public void calculateInitialWeatherBody() {
        if (this.levelProperties.isRaining()) {
            this.rainGradient = 1.0F;
            if (this.levelProperties.isThundering()) {
                this.thunderGradient = 1.0F;
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void tickWeather() {
        this.dimension.updateWeather();
    }

    @Override
    public void updateWeatherBody() {
        if (!this.dimension.isNether) {
            if (this.field_23088 > 0) {
                --this.field_23088;
            }

            int var1 = this.levelProperties.getThunderTime();
            if (var1 <= 0) {
                if (this.levelProperties.isThundering()) {
                    this.levelProperties.setThunderTime(this.random.nextInt(12000) + 3600);
                } else {
                    this.levelProperties.setThunderTime(this.random.nextInt(168000) + 12000);
                }
            } else {
                this.levelProperties.setThunderTime(--var1);
                if (var1 <= 0) {
                    this.levelProperties.setThundering(!this.levelProperties.isThundering());
                }
            }

            int var2 = this.levelProperties.getRainTIme();
            if (var2 <= 0) {
                if (this.levelProperties.isRaining()) {
                    this.levelProperties.setRainTime(this.random.nextInt(12000) + 12000);
                } else {
                    this.levelProperties.setRainTime(this.random.nextInt(168000) + 12000);
                }
            } else {
                this.levelProperties.setRainTime(--var2);
                if (var2 <= 0) {
                    this.levelProperties.setRaining(!this.levelProperties.isRaining());
                }
            }

            this.rainGradientPrev = this.rainGradient;
            if (this.levelProperties.isRaining()) {
                this.rainGradient = (float)((double)this.rainGradient + 0.01);
            } else {
                this.rainGradient = (float)((double)this.rainGradient - 0.01);
            }

            if (this.rainGradient < 0.0F) {
                this.rainGradient = 0.0F;
            }

            if (this.rainGradient > 1.0F) {
                this.rainGradient = 1.0F;
            }

            this.thunderGradientPrev = this.thunderGradient;
            if (this.levelProperties.isThundering()) {
                this.thunderGradient = (float)((double)this.thunderGradient + 0.01);
            } else {
                this.thunderGradient = (float)((double)this.thunderGradient - 0.01);
            }

            if (this.thunderGradient < 0.0F) {
                this.thunderGradient = 0.0F;
            }

            if (this.thunderGradient > 1.0F) {
                this.thunderGradient = 1.0F;
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_3733() {
        this.dimension.toggleRain();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    protected void updateLighting() {
        this.field_4530.clear();
        this.field_4530.addAll(this.getPersistentChunks().keySet());
        this.profiler.push("buildList");

        for(int var1 = 0; var1 < this.playerEntities.size(); ++var1) {
            PlayerEntity var2 = (PlayerEntity)this.playerEntities.get(var1);
            int var3 = MathHelper.floor(var2.x / 16.0);
            int var4 = MathHelper.floor(var2.z / 16.0);
            byte var5 = 7;

            for(int var6 = -var5; var6 <= var5; ++var6) {
                for(int var7 = -var5; var7 <= var5; ++var7) {
                    this.field_4530.add(new ChunkPos(var6 + var3, var7 + var4));
                }
            }
        }

        this.profiler.pop();
        if (this.field_4534 > 0) {
            --this.field_4534;
        }

        this.profiler.push("playerCheckLight");
        if (!this.playerEntities.isEmpty()) {
            int var81 = this.random.nextInt(this.playerEntities.size());
            PlayerEntity var2 = (PlayerEntity)this.playerEntities.get(var81);
            int var3 = MathHelper.floor(var2.x) + this.random.nextInt(11) - 5;
            int var4 = MathHelper.floor(var2.y) + this.random.nextInt(11) - 5;
            int var8 = MathHelper.floor(var2.z) + this.random.nextInt(11) - 5;
            this.method_3736(var3, var4, var8);
        }

        this.profiler.pop();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_3675(int par1, int par2, int par3, boolean par4) {
        return this.dimension.canBlockFreeze(par1, par2, par3, par4);
    }

    @Override
    public boolean canBlockFreezeBody(int par1, int par2, int par3, boolean par4) {
        Biome var5 = this.method_3773(par1, par3);
        float var6 = var5.getTemperatureValue();
        if (var6 > 0.15F) {
            return false;
        } else {
            if (par2 >= 0 && par2 < 256 && this.method_3667(LightType.BLOCK, par1, par2, par3) < 10) {
                int var7 = this.getBlock(par1, par2, par3);
                if ((var7 == Block.WATER.id || var7 == Block.field_334.id) && this.getBlockData(par1, par2, par3) == 0) {
                    if (!par4) {
                        return true;
                    }

                    boolean var8 = true;
                    if (var8 && this.getMaterial(par1 - 1, par2, par3) != Material.WATER) {
                        var8 = false;
                    }

                    if (var8 && this.getMaterial(par1 + 1, par2, par3) != Material.WATER) {
                        var8 = false;
                    }

                    if (var8 && this.getMaterial(par1, par2, par3 - 1) != Material.WATER) {
                        var8 = false;
                    }

                    if (var8 && this.getMaterial(par1, par2, par3 + 1) != Material.WATER) {
                        var8 = false;
                    }

                    if (!var8) {
                        return true;
                    }
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
    public boolean method_3734(int par1, int par2, int par3) {
        return this.dimension.canSnowAt(par1, par2, par3);
    }

    @Override
    public boolean canSnowAtBody(int par1, int par2, int par3) {
        Biome var4 = this.method_3773(par1, par3);
        float var5 = var4.getTemperatureValue();
        if (var5 > 0.15F) {
            return false;
        } else {
            if (par2 >= 0 && par2 < 256 && this.method_3667(LightType.BLOCK, par1, par2, par3) < 10) {
                int var6 = this.getBlock(par1, par2 - 1, par3);
                int var7 = this.getBlock(par1, par2, par3);
                if (var7 == 0
                        && Block.SNOW_LAYER.method_434((World)(Object) this, par1, par2, par3)
                        && var6 != 0
                        && var6 != Block.ICE.id
                        && Block.BLOCKS[var6].material.blocksMovement()) {
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
    private int method_3699(int par1, int par2, int par3, int par4, int par5, int par6) {
        int var7 = par5 != 0 && Block.BLOCKS[par5] != null ? Block.BLOCKS[par5].getLightValue(this, par2, par3, par4) : 0;
        int var8 = this.method_3667(LightType.BLOCK, par2 - 1, par3, par4) - par6;
        int var9 = this.method_3667(LightType.BLOCK, par2 + 1, par3, par4) - par6;
        int var10 = this.method_3667(LightType.BLOCK, par2, par3 - 1, par4) - par6;
        int var11 = this.method_3667(LightType.BLOCK, par2, par3 + 1, par4) - par6;
        int var12 = this.method_3667(LightType.BLOCK, par2, par3, par4 - 1) - par6;
        int var13 = this.method_3667(LightType.BLOCK, par2, par3, par4 + 1) - par6;
        if (var8 > var7) {
            var7 = var8;
        }

        if (var9 > var7) {
            var7 = var9;
        }

        if (var10 > var7) {
            var7 = var10;
        }

        if (var11 > var7) {
            var7 = var11;
        }

        if (var12 > var7) {
            var7 = var12;
        }

        if (var13 > var7) {
            var7 = var13;
        }

        return var7;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_3678(LightType par1EnumSkyBlock, int par2, int par3, int par4) {
        if (this.isRegionAroundLoaded(par2, par3, par4, 17)) {
            int var5 = 0;
            int var6 = 0;
            this.profiler.push("getBrightness");
            int var7 = this.method_3667(par1EnumSkyBlock, par2, par3, par4);
            boolean var8 = false;
            int var9 = this.getBlock(par2, par3, par4);
            int var10 = this.method_3651(par2, par3, par4);
            if (var10 == 0) {
                var10 = 1;
            }

            boolean var11 = false;
            int var24;
            if (par1EnumSkyBlock == LightType.SKY) {
                var24 = this.method_3600(var7, par2, par3, par4, var9, var10);
            } else {
                var24 = this.method_3699(var7, par2, par3, par4, var9, var10);
            }

            if (var24 > var7) {
                this.updateLightBlocks[var6++] = 133152;
            } else if (var24 < var7) {
                if (par1EnumSkyBlock != LightType.BLOCK) {
                }

                this.updateLightBlocks[var6++] = 133152 + (var7 << 18);

                while(var5 < var6) {
                    var9 = this.updateLightBlocks[var5++];
                    var10 = (var9 & 63) - 32 + par2;
                    var24 = (var9 >> 6 & 63) - 32 + par3;
                    int var12 = (var9 >> 12 & 63) - 32 + par4;
                    int var13 = var9 >> 18 & 15;
                    int var14 = this.method_3667(par1EnumSkyBlock, var10, var24, var12);
                    if (var14 == var13) {
                        this.method_3668(par1EnumSkyBlock, var10, var24, var12, 0);
                        if (var13 > 0) {
                            int var15 = var10 - par2;
                            int var16 = var24 - par3;
                            int var17 = var12 - par4;
                            if (var15 < 0) {
                                var15 = -var15;
                            }

                            if (var16 < 0) {
                                var16 = -var16;
                            }

                            if (var17 < 0) {
                                var17 = -var17;
                            }

                            if (var15 + var16 + var17 < 17) {
                                for(int var18 = 0; var18 < 6; ++var18) {
                                    int var19 = var18 % 2 * 2 - 1;
                                    int var20 = var10 + var18 / 2 % 3 / 2 * var19;
                                    int var21 = var24 + (var18 / 2 + 1) % 3 / 2 * var19;
                                    int var22 = var12 + (var18 / 2 + 2) % 3 / 2 * var19;
                                    var14 = this.method_3667(par1EnumSkyBlock, var20, var21, var22);
                                    int var23 = this.method_3651(var20, var21, var22);
                                    if (var23 == 0) {
                                        var23 = 1;
                                    }

                                    if (var14 == var13 - var23 && var6 < this.updateLightBlocks.length) {
                                        this.updateLightBlocks[var6++] = var20
                                                - par2
                                                + 32
                                                + (var21 - par3 + 32 << 6)
                                                + (var22 - par4 + 32 << 12)
                                                + (var13 - var23 << 18);
                                    }
                                }
                            }
                        }
                    }
                }

                var5 = 0;
            }

            this.profiler.pop();
            this.profiler.push("checkedPosition < toCheckCount");

            while(var5 < var6) {
                var9 = this.updateLightBlocks[var5++];
                var10 = (var9 & 63) - 32 + par2;
                var24 = (var9 >> 6 & 63) - 32 + par3;
                int var12 = (var9 >> 12 & 63) - 32 + par4;
                int var13 = this.method_3667(par1EnumSkyBlock, var10, var24, var12);
                int var14 = this.getBlock(var10, var24, var12);
                int var15 = this.method_3651(var10, var24, var12);
                if (var15 == 0) {
                    var15 = 1;
                }

                boolean var25 = false;
                int var16;
                if (par1EnumSkyBlock == LightType.SKY) {
                    var16 = this.method_3600(var13, var10, var24, var12, var14, var15);
                } else {
                    var16 = this.method_3699(var13, var10, var24, var12, var14, var15);
                }

                if (var16 != var13) {
                    this.method_3668(par1EnumSkyBlock, var10, var24, var12, var16);
                    if (var16 > var13) {
                        int var17 = var10 - par2;
                        int var18 = var24 - par3;
                        int var19 = var12 - par4;
                        if (var17 < 0) {
                            var17 = -var17;
                        }

                        if (var18 < 0) {
                            var18 = -var18;
                        }

                        if (var19 < 0) {
                            var19 = -var19;
                        }

                        if (var17 + var18 + var19 < 17 && var6 < this.updateLightBlocks.length - 6) {
                            if (this.method_3667(par1EnumSkyBlock, var10 - 1, var24, var12) < var16) {
                                this.updateLightBlocks[var6++] = var10 - 1 - par2 + 32 + (var24 - par3 + 32 << 6) + (var12 - par4 + 32 << 12);
                            }

                            if (this.method_3667(par1EnumSkyBlock, var10 + 1, var24, var12) < var16) {
                                this.updateLightBlocks[var6++] = var10 + 1 - par2 + 32 + (var24 - par3 + 32 << 6) + (var12 - par4 + 32 << 12);
                            }

                            if (this.method_3667(par1EnumSkyBlock, var10, var24 - 1, var12) < var16) {
                                this.updateLightBlocks[var6++] = var10 - par2 + 32 + (var24 - 1 - par3 + 32 << 6) + (var12 - par4 + 32 << 12);
                            }

                            if (this.method_3667(par1EnumSkyBlock, var10, var24 + 1, var12) < var16) {
                                this.updateLightBlocks[var6++] = var10 - par2 + 32 + (var24 + 1 - par3 + 32 << 6) + (var12 - par4 + 32 << 12);
                            }

                            if (this.method_3667(par1EnumSkyBlock, var10, var24, var12 - 1) < var16) {
                                this.updateLightBlocks[var6++] = var10 - par2 + 32 + (var24 - par3 + 32 << 6) + (var12 - 1 - par4 + 32 << 12);
                            }

                            if (this.method_3667(par1EnumSkyBlock, var10, var24, var12 + 1) < var16) {
                                this.updateLightBlocks[var6++] = var10 - par2 + 32 + (var24 - par3 + 32 << 6) + (var12 + 1 - par4 + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.profiler.pop();
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public List getEntitiesIn(Entity par1Entity, Box par2AxisAlignedBB) {
        this.field_4535.clear();
        int var3 = MathHelper.floor((par2AxisAlignedBB.minX - ReflectionUtils.World_MAX_ENTITY_RADIUS) / 16.0);
        int var4 = MathHelper.floor((par2AxisAlignedBB.maxX + ReflectionUtils.World_MAX_ENTITY_RADIUS) / 16.0);
        int var5 = MathHelper.floor((par2AxisAlignedBB.minZ - ReflectionUtils.World_MAX_ENTITY_RADIUS) / 16.0);
        int var6 = MathHelper.floor((par2AxisAlignedBB.maxZ + ReflectionUtils.World_MAX_ENTITY_RADIUS) / 16.0);

        for(int var7 = var3; var7 <= var4; ++var7) {
            for(int var8 = var5; var8 <= var6; ++var8) {
                if (this.isChunkLoaded(var7, var8)) {
                    this.getChunk(var7, var8).method_3889(par1Entity, par2AxisAlignedBB, this.field_4535);
                }
            }
        }

        return this.field_4535;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public List getEntitiesInBox(Class par1Class, Box par2AxisAlignedBB, EntityPredicates par3IEntitySelector) {
        int var4 = MathHelper.floor((par2AxisAlignedBB.minX - ReflectionUtils.World_MAX_ENTITY_RADIUS) / 16.0);
        int var5 = MathHelper.floor((par2AxisAlignedBB.maxX + ReflectionUtils.World_MAX_ENTITY_RADIUS) / 16.0);
        int var6 = MathHelper.floor((par2AxisAlignedBB.minZ - ReflectionUtils.World_MAX_ENTITY_RADIUS) / 16.0);
        int var7 = MathHelper.floor((par2AxisAlignedBB.maxZ + ReflectionUtils.World_MAX_ENTITY_RADIUS) / 16.0);
        ArrayList var8 = new ArrayList();

        for(int var9 = var4; var9 <= var5; ++var9) {
            for(int var10 = var6; var10 <= var7; ++var10) {
                if (this.isChunkLoaded(var9, var10)) {
                    this.getChunk(var9, var10).method_3886(par1Class, par2AxisAlignedBB, var8, par3IEntitySelector);
                }
            }
        }

        return var8;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_3625(List par1List) {
        for(int var2 = 0; var2 < par1List.size(); ++var2) {
            Entity entity = (Entity)par1List.get(var2);
            if (!MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(entity, (World)(Object) this))) {
                this.loadedEntities.add(entity);
                this.onEntitySpawned(entity);
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_3602(int par1, int par2, int par3, int par4, boolean par5, int par6, Entity par7Entity) {
        int var8 = this.getBlock(par2, par3, par4);
        Block var9 = Block.BLOCKS[var8];
        Block var10 = Block.BLOCKS[par1];
        Box var11 = var10.method_454((World)(Object) this, par2, par3, par4);
        if (par5) {
            var11 = null;
        }

        if (var11 != null && !this.hasEntityIn(var11, par7Entity)) {
            return false;
        } else {
            if (var9 != null
                    && (
                    var9 == Block.field_334
                            || var9 == Block.WATER
                            || var9 == Block.field_336
                            || var9 == Block.LAVA
                            || var9 == Block.FIRE
                            || var9.material.isReplaceable()
            )) {
                var9 = null;
            }

            if (var9 != null && var9.isBlockReplaceable((World)(Object) this, par2, par3, par4)) {
                var9 = null;
            }

            return var9 != null && var9.material == Material.PART && var10 == Block.ANVIL_BLOCK
                    ? true
                    : par1 > 0 && var9 == null && var10.method_428((World)(Object) this, par2, par3, par4, par6);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public long getTimeOfDay() {
        return this.dimension.getWorldTime();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void setTimeOfDay(long par1) {
        this.dimension.setWorldTime(par1);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public BlockPos getWorldSpawnPos() {
        return this.dimension.getSpawnPoint();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public void method_3578(int par1, int par2, int par3) {
        this.dimension.setSpawnPoint(par1, par2, par3);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public void loadEntity(Entity par1Entity) {
        int var2 = MathHelper.floor(par1Entity.x / 16.0);
        int var3 = MathHelper.floor(par1Entity.z / 16.0);
        byte var4 = 2;

        for(int var5 = var2 - var4; var5 <= var2 + var4; ++var5) {
            for(int var6 = var3 - var4; var6 <= var3 + var4; ++var6) {
                this.getChunk(var5, var6);
            }
        }

        if (!this.loadedEntities.contains(par1Entity) && !MinecraftForge.EVENT_BUS.post(new EntityJoinWorldEvent(par1Entity, (World)(Object) this))) {
            this.loadedEntities.add(par1Entity);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_3637(PlayerEntity par1EntityPlayer, int par2, int par3, int par4) {
        return this.dimension.canMineBlock(par1EntityPlayer, par2, par3, par4);
    }

    @Override
    public boolean canMineBlockBody(PlayerEntity par1EntityPlayer, int par2, int par3, int par4) {
        return true;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_3582(int par1, int par2, int par3) {
        return this.dimension.isBlockHighHumidity(par1, par2, par3);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int method_3771() {
        return this.dimension.getHeight();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int getEffectiveHeight() {
        return this.dimension.getActualHeight();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Environment(EnvType.CLIENT)
    @Overwrite
    public double getHorizonHeight() {
        return this.dimension.getHorizon();
    }

    @Override
    public void addTileEntity(BlockEntity entity) {
        List dest = this.iteratingTickingBlockEntities ? this.pendingBlockEntities : this.blockEntities;
        if (entity.canUpdate()) {
            dest.add(entity);
        }

    }

    @Override
    public boolean isBlockSolidOnSide(int X, int Y, int Z, ForgeDirection side) {
        return this.isBlockSolidOnSide(X, Y, Z, side, false);
    }

    @Override
    public boolean isBlockSolidOnSide(int X, int Y, int Z, ForgeDirection side, boolean _default) {
        if (X >= -30000000 && Z >= -30000000 && X < 30000000 && Z < 30000000) {
            Chunk var5 = this.chunkProvider.getChunk(X >> 4, Z >> 4);
            if (var5 != null && !var5.isEmpty()) {
                Block block = Block.BLOCKS[this.getBlock(X, Y, Z)];
                return block == null ? false : block.isBlockSolidOnSide((World)(Object) this, X, Y, Z, side);
            } else {
                return _default;
            }
        } else {
            return _default;
        }
    }

    public SetMultimap<ChunkPos, ForgeChunkManager.Ticket> getPersistentChunks() {
        return ForgeChunkManager.getPersistentChunksFor((World)(Object) this);
    }

    @Override
    public LevelProperties getLevelProperties() {
        return this.levelProperties;
    }

    @Override
    public void setSpawnAnimals(boolean bool) {
        this.spawnAnimals = bool;
    }

    @Override
    public void setSpawnMonsters(boolean bool) {
        this.spawnMonsters = bool;
    }
}
