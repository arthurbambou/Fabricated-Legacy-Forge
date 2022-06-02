package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResultType;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class ForgeHooks {
    static final List<ForgeHooks.GrassEntry> grassList = new ArrayList();
    static final List<ForgeHooks.SeedEntry> seedList = new ArrayList();
    private static boolean toolInit = false;
    static HashMap<Item, List> toolClasses = new HashMap();
    static HashMap<List, Integer> toolHarvestLevels = new HashMap();
    static HashSet<List> toolEffectiveness = new HashSet();

    public ForgeHooks() {
    }

    public static void plantGrass(World world, int x, int y, int z) {
        ForgeHooks.GrassEntry grass = (ForgeHooks.GrassEntry)Weighting.getRandom(world.random, grassList);
        if (grass != null && grass.block != null && grass.block.method_450(world, x, y, z)) {
            world.method_3683(x, y, z, grass.block.id, grass.metadata);
        }
    }

    public static ItemStack getGrassSeed(World world) {
        ForgeHooks.SeedEntry entry = (ForgeHooks.SeedEntry)Weighting.getRandom(world.random, seedList);
        return entry != null && entry.seed != null ? entry.seed.copy() : null;
    }

    public static boolean canHarvestBlock(Block block, PlayerEntity player, int metadata) {
        if (block.material.doesBlockMovement()) {
            return true;
        } else {
            ItemStack stack = player.inventory.getMainHandStack();
            if (stack == null) {
                return player.isUsingEffectiveTool(block);
            } else {
                List info = (List)toolClasses.get(stack.getItem());
                if (info == null) {
                    return player.isUsingEffectiveTool(block);
                } else {
                    Object[] tmp = info.toArray();
                    String toolClass = (String)tmp[0];
                    int harvestLevel = (int) tmp[1];
                    Integer blockHarvestLevel = (Integer)toolHarvestLevels.get(Arrays.asList(block, metadata, toolClass));
                    if (blockHarvestLevel == null) {
                        return player.isUsingEffectiveTool(block);
                    } else {
                        return blockHarvestLevel <= harvestLevel;
                    }
                }
            }
        }
    }

    public static float blockStrength(Block block, PlayerEntity player, World world, int x, int y, int z) {
        int metadata = world.getBlockData(x, y, z);
        float hardness = block.method_471(world, x, y, z);
        if (hardness < 0.0F) {
            return 0.0F;
        } else if (!canHarvestBlock(block, player, metadata)) {
            float speed = ForgeEventFactory.getBreakSpeed(player, block, metadata, 1.0F);
            return (speed < 0.0F ? 0.0F : speed) / hardness / 100.0F;
        } else {
            return player.getCurrentPlayerStrVsBlock(block, metadata) / hardness / 30.0F;
        }
    }

    public static boolean isToolEffective(ItemStack stack, Block block, int metadata) {
        List toolClass = (List)toolClasses.get(stack.getItem());
        return toolClass == null ? false : toolEffectiveness.contains(Arrays.asList(block, metadata, (String)toolClass.get(0)));
    }

    static void initTools() {
        if (!toolInit) {
            toolInit = true;
            MinecraftForge.setToolClass(Item.WOOD_PICKAXE, "pickaxe", 0);
            MinecraftForge.setToolClass(Item.STONE_PICKAXE, "pickaxe", 1);
            MinecraftForge.setToolClass(Item.IRON_PICKAXE, "pickaxe", 2);
            MinecraftForge.setToolClass(Item.GOLD_PICKAXE, "pickaxe", 0);
            MinecraftForge.setToolClass(Item.DIAMOND_PICKAXE, "pickaxe", 3);
            MinecraftForge.setToolClass(Item.WOOD_AXE, "axe", 0);
            MinecraftForge.setToolClass(Item.STONE_AXE, "axe", 1);
            MinecraftForge.setToolClass(Item.IRON_AXE, "axe", 2);
            MinecraftForge.setToolClass(Item.GOLD_AXE, "axe", 0);
            MinecraftForge.setToolClass(Item.DIAMOND_AXE, "axe", 3);
            MinecraftForge.setToolClass(Item.WOOD_SHOVEL, "shovel", 0);
            MinecraftForge.setToolClass(Item.STONE_SHOVEL, "shovel", 1);
            MinecraftForge.setToolClass(Item.IRON_SHOVEL, "shovel", 2);
            MinecraftForge.setToolClass(Item.GOLD_SHOVEL, "shovel", 0);
            MinecraftForge.setToolClass(Item.DIAMOND_SHOVEL, "shovel", 3);

            for(Block block : PickaxeItem.field_4382) {
                MinecraftForge.setBlockHarvestLevel(block, "pickaxe", 0);
            }

            for(Block block : ShovelItem.field_4396) {
                MinecraftForge.setBlockHarvestLevel(block, "shovel", 0);
            }

            for(Block block : AxeItem.field_4207) {
                MinecraftForge.setBlockHarvestLevel(block, "axe", 0);
            }

            MinecraftForge.setBlockHarvestLevel(Block.OBSIDIAN, "pickaxe", 3);
            MinecraftForge.setBlockHarvestLevel(Block.EMERALD_ORE, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.DIAMOND_ORE, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.DIAMOND_BLOCK, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.GOLD_ORE, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.GOLD_BLOCK, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.IRON_ORE, "pickaxe", 1);
            MinecraftForge.setBlockHarvestLevel(Block.IRON_BLOCK, "pickaxe", 1);
            MinecraftForge.setBlockHarvestLevel(Block.LAPIS_ORE, "pickaxe", 1);
            MinecraftForge.setBlockHarvestLevel(Block.LAPIS_BLOCK, "pickaxe", 1);
            MinecraftForge.setBlockHarvestLevel(Block.REDSTONE_ORE, "pickaxe", 2);
            MinecraftForge.setBlockHarvestLevel(Block.LIT_REDSTONE_ORE, "pickaxe", 2);
            MinecraftForge.removeBlockEffectiveness(Block.REDSTONE_ORE, "pickaxe");
            MinecraftForge.removeBlockEffectiveness(Block.OBSIDIAN, "pickaxe");
            MinecraftForge.removeBlockEffectiveness(Block.LIT_REDSTONE_ORE, "pickaxe");
        }
    }

    public static String getTexture(String _default, Object obj) {
        if (obj instanceof Item) {
            return ((Item)obj).getTextureFile();
        } else {
            return obj instanceof Block ? ((Block)obj).getTextureFile() : _default;
        }
    }

    public static int getTotalArmorValue(PlayerEntity player) {
        int ret = 0;

        for(int x = 0; x < player.inventory.armor.length; ++x) {
            ItemStack stack = player.inventory.armor[x];
            if (stack != null && stack.getItem() instanceof ISpecialArmor) {
                ret += ((ISpecialArmor)stack.getItem()).getArmorDisplay(player, stack, x);
            } else if (stack != null && stack.getItem() instanceof ArmorItem) {
                ret += ((ArmorItem)stack.getItem()).protection;
            }
        }

        return ret;
    }

    public static boolean onPickBlock(BlockHitResult target, PlayerEntity player, World world) {
        ItemStack result = null;
        boolean isCreative = player.abilities.creativeMode;
        if (target.field_595 == HitResultType.TILE) {
            int x = target.x;
            int y = target.y;
            int z = target.z;
            Block var8 = Block.BLOCKS[world.getBlock(x, y, z)];
            if (var8 == null) {
                return false;
            }

            result = var8.getPickBlock(target, world, x, y, z);
        } else {
            if (target.field_595 != HitResultType.ENTITY || target.entity == null || !isCreative) {
                return false;
            }

            result = target.entity.getPickedResult(target);
        }

        if (result == null) {
            return false;
        } else {
            for(int x = 0; x < 9; ++x) {
                ItemStack stack = player.inventory.getInvStack(x);
                if (stack != null && stack.equalsIgnoreNbt(result)) {
                    player.inventory.selectedSlot = x;
                    return true;
                }
            }

            if (!isCreative) {
                return false;
            } else {
                int slot = player.inventory.method_3146();
                if (slot < 0 || slot >= 9) {
                    slot = player.inventory.selectedSlot;
                }

                player.inventory.setInvStack(slot, result);
                player.inventory.selectedSlot = slot;
                return true;
            }
        }
    }

    public static void onLivingSetAttackTarget(MobEntity entity, MobEntity target) {
        MinecraftForge.EVENT_BUS.post(new LivingSetAttackTargetEvent(entity, target));
    }

    public static boolean onLivingUpdate(MobEntity entity) {
        return MinecraftForge.EVENT_BUS.post(new LivingUpdateEvent(entity));
    }

    public static boolean onLivingAttack(MobEntity entity, DamageSource src, int amount) {
        return MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
    }

    public static int onLivingHurt(MobEntity entity, DamageSource src, int amount) {
        LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
        return MinecraftForge.EVENT_BUS.post(event) ? 0 : event.ammount;
    }

    public static boolean onLivingDeath(MobEntity entity, DamageSource src) {
        return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
    }

    public static boolean onLivingDrops(
            MobEntity entity, DamageSource source, ArrayList<ItemEntity> drops, int lootingLevel, boolean recentlyHit, int specialDropValue
    ) {
        return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit, specialDropValue));
    }

    public static float onLivingFall(MobEntity entity, float distance) {
        LivingFallEvent event = new LivingFallEvent(entity, distance);
        return MinecraftForge.EVENT_BUS.post(event) ? 0.0F : event.distance;
    }

    public static boolean isLivingOnLadder(Block block, World world, int x, int y, int z) {
        return block != null && block.isLadder(world, x, y, z);
    }

    public static void onLivingJump(MobEntity entity) {
        MinecraftForge.EVENT_BUS.post(new LivingJumpEvent(entity));
    }

    public static ItemEntity onPlayerTossEvent(PlayerEntity player, ItemStack item) {
        player.captureDrops = true;
        ItemEntity ret = player.dropStack(item, false);
        player.capturedDrops.clear();
        player.captureDrops = false;
        ItemTossEvent event = new ItemTossEvent(ret, player);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return null;
        } else {
            player.spawnItemEntity(event.entityItem);
            return event.entityItem;
        }
    }

    static {
        grassList.add(new ForgeHooks.GrassEntry(Block.YELLOW_FLOWER, 0, 20));
        grassList.add(new ForgeHooks.GrassEntry(Block.RED_FLOWER, 0, 10));
        seedList.add(new ForgeHooks.SeedEntry(new ItemStack(Item.WHEAT_SEEDS), 10));
        initTools();
    }

    static class GrassEntry extends Weight {
        public final Block block;
        public final int metadata;

        public GrassEntry(Block block, int meta, int weight) {
            super(weight);
            this.block = block;
            this.metadata = meta;
        }
    }

    static class SeedEntry extends Weight {
        public final ItemStack seed;

        public SeedEntry(ItemStack seed, int weight) {
            super(weight);
            this.seed = seed;
        }
    }
}
