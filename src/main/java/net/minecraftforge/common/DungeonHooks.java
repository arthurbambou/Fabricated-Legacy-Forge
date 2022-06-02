package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.collection.Weighting;

public class DungeonHooks {
    private static int dungeonLootAttempts = 8;
    private static ArrayList<DungeonHooks.DungeonMob> dungeonMobs = new ArrayList();
    private static ArrayList<DungeonHooks.DungeonLoot> dungeonLoot = new ArrayList();

    public DungeonHooks() {
    }

    public static void setDungeonLootTries(int number) {
        dungeonLootAttempts = number;
    }

    public static int getDungeonLootTries() {
        return dungeonLootAttempts;
    }

    public static float addDungeonMob(String name, int rarity) {
        if (rarity <= 0) {
            throw new IllegalArgumentException("Rarity must be greater then zero");
        } else {
            for(DungeonHooks.DungeonMob mob : dungeonMobs) {
                if (name.equals(mob.type)) {
                    return (float)(mob.weight += rarity);
                }
            }

            dungeonMobs.add(new DungeonHooks.DungeonMob(rarity, name));
            return (float)rarity;
        }
    }

    public static int removeDungeonMob(String name) {
        for(DungeonHooks.DungeonMob mob : dungeonMobs) {
            if (name.equals(mob.type)) {
                dungeonMobs.remove(mob);
                return mob.weight;
            }
        }

        return 0;
    }

    public static String getRandomDungeonMob(Random rand) {
        DungeonHooks.DungeonMob mob = (DungeonHooks.DungeonMob)Weighting.getRandom(rand, dungeonMobs);
        return mob == null ? "" : mob.type;
    }

    public static void addDungeonLoot(ItemStack item, int rarity) {
        addDungeonLoot(item, rarity, 1, 1);
    }

    public static float addDungeonLoot(ItemStack item, int rarity, int minCount, int maxCount) {
        for(DungeonHooks.DungeonLoot loot : dungeonLoot) {
            if (loot.equals(item, minCount, maxCount)) {
                return (float)(loot.weight += rarity);
            }
        }

        dungeonLoot.add(new DungeonHooks.DungeonLoot(rarity, item, minCount, maxCount));
        return (float)rarity;
    }

    public static void removeDungeonLoot(ItemStack item) {
        removeDungeonLoot(item, -1, 0);
    }

    public static void removeDungeonLoot(ItemStack item, int minCount, int maxCount) {
        ArrayList<DungeonHooks.DungeonLoot> lootTmp = (ArrayList)dungeonLoot.clone();
        if (minCount < 0) {
            for(DungeonHooks.DungeonLoot loot : lootTmp) {
                if (loot.equals(item)) {
                    dungeonLoot.remove(loot);
                }
            }
        } else {
            for(DungeonHooks.DungeonLoot loot : lootTmp) {
                if (loot.equals(item, minCount, maxCount)) {
                    dungeonLoot.remove(loot);
                }
            }
        }

    }

    public static ItemStack getRandomDungeonLoot(Random rand) {
        DungeonHooks.DungeonLoot ret = (DungeonHooks.DungeonLoot)Weighting.getRandom(rand, dungeonLoot);
        return ret != null ? ret.generateStack(rand) : null;
    }

    public void addDungeonLoot(DungeonHooks.DungeonLoot loot) {
        dungeonLoot.add(loot);
    }

    public boolean removeDungeonLoot(DungeonHooks.DungeonLoot loot) {
        return dungeonLoot.remove(loot);
    }

    static {
        addDungeonMob("Skeleton", 100);
        addDungeonMob("Zombie", 200);
        addDungeonMob("Spider", 100);
        addDungeonLoot(new ItemStack(Item.SADDLE), 100);
        addDungeonLoot(new ItemStack(Item.IRON_INGOT), 100, 1, 4);
        addDungeonLoot(new ItemStack(Item.BREAD), 100);
        addDungeonLoot(new ItemStack(Item.WHEAT), 100, 1, 4);
        addDungeonLoot(new ItemStack(Item.GUNPOWDER), 100, 1, 4);
        addDungeonLoot(new ItemStack(Item.STRING), 100, 1, 4);
        addDungeonLoot(new ItemStack(Item.BUCKET), 100);
        addDungeonLoot(new ItemStack(Item.GOLDEN_APPLE), 1);
        addDungeonLoot(new ItemStack(Item.REDSTONE), 40, 1, 4);
        addDungeonLoot(new ItemStack(Item.RECORD_13), 5);
        addDungeonLoot(new ItemStack(Item.RECORD_CAT), 5);
        addDungeonLoot(new ItemStack(Item.DYES, 1, 3), 100);
    }

    public static class DungeonLoot extends Weight {
        private ItemStack itemStack;
        private int minCount = 1;
        private int maxCount = 1;

        public DungeonLoot(int weight, ItemStack item, int min, int max) {
            super(weight);
            this.itemStack = item;
            this.minCount = min;
            this.maxCount = max;
        }

        public ItemStack generateStack(Random rand) {
            ItemStack ret = this.itemStack.copy();
            ret.count = this.minCount + rand.nextInt(this.maxCount - this.minCount + 1);
            return ret;
        }

        public boolean equals(ItemStack item, int min, int max) {
            return min == this.minCount && max == this.maxCount && item.equalsIgnoreNbt(this.itemStack);
        }

        public boolean equals(ItemStack item) {
            return item.equalsIgnoreNbt(this.itemStack);
        }
    }

    public static class DungeonMob extends Weight {
        public String type;

        public DungeonMob(int weight, String type) {
            super(weight);
            this.type = type;
        }

        public boolean equals(Object target) {
            return target instanceof DungeonHooks.DungeonMob ? this.type.equals(((DungeonHooks.DungeonMob)target).type) : false;
        }
    }
}
