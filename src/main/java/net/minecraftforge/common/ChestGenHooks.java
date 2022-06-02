package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.MineshaftPieces;
import net.minecraft.structure.class_15;
import net.minecraft.structure.class_19;
import net.minecraft.structure.class_24;
import net.minecraft.structure.class_49;
import net.minecraft.structure.class_5;
import net.minecraft.structure.class_6;
import net.minecraft.util.WeightedRandomChestContent;

public class ChestGenHooks {
    public static final String MINESHAFT_CORRIDOR = "mineshaftCorridor";
    public static final String PYRAMID_DESERT_CHEST = "pyramidDesertyChest";
    public static final String PYRAMID_JUNGLE_CHEST = "pyramidJungleChest";
    public static final String PYRAMID_JUNGLE_DISPENSER = "pyramidJungleDispenser";
    public static final String STRONGHOLD_CORRIDOR = "strongholdCorridor";
    public static final String STRONGHOLD_LIBRARY = "strongholdLibrary";
    public static final String STRONGHOLD_CROSSING = "strongholdCrossing";
    public static final String VILLAGE_BLACKSMITH = "villageBlacksmith";
    public static final String BONUS_CHEST = "bonusChest";
    private static final HashMap<String, ChestGenHooks> chestInfo = new HashMap();
    private static boolean hasInit = false;
    private String category;
    private int countMin = 0;
    private int countMax = 0;
    private ArrayList<WeightedRandomChestContent> contents = new ArrayList();

    private static void init() {
        if (!hasInit) {
            addInfo("mineshaftCorridor", MineshaftPieces.field_4910, 3, 7);
            addInfo("pyramidDesertyChest", class_5.field_6, 2, 7);
            addInfo("pyramidJungleChest", class_6.field_11, 2, 7);
            addInfo("pyramidJungleDispenser", class_6.field_12, 2, 2);
            addInfo("strongholdCorridor", class_15.field_27, 2, 4);
            addInfo("strongholdLibrary", class_19.field_38, 1, 5);
            addInfo("strongholdCrossing", class_24.field_48, 1, 5);
            addInfo("villageBlacksmith", class_49.field_89, 3, 9);
            addInfo("bonusChest", ServerWorld.field_2817, 10, 10);
        }
    }

    private static void addInfo(String category, WeightedRandomChestContent[] items, int min, int max) {
        chestInfo.put(category, new ChestGenHooks(category, items, min, max));
    }

    public static ChestGenHooks getInfo(String category) {
        if (!chestInfo.containsKey(category)) {
            chestInfo.put(category, new ChestGenHooks(category));
        }

        return (ChestGenHooks)chestInfo.get(category);
    }

    public static ItemStack[] generateStacks(Random rand, ItemStack source, int min, int max) {
        int count = min + rand.nextInt(max - min + 1);
        ItemStack[] ret;
        if (source.getItem() == null) {
            ret = new ItemStack[0];
        } else if (count > source.getItem().getMaxCount()) {
            ret = new ItemStack[count];

            for(int x = 0; x < count; ++x) {
                ret[x] = source.copy();
                ret[x].count = 1;
            }
        } else {
            ret = new ItemStack[1];
            ret[0] = source.copy();
            ret[0].count = count;
        }

        return ret;
    }

    public static WeightedRandomChestContent[] getItems(String category) {
        return getInfo(category).getItems();
    }

    public static int getCount(String category, Random rand) {
        return getInfo(category).getCount(rand);
    }

    public static void addItem(String category, WeightedRandomChestContent item) {
        getInfo(category).addItem(item);
    }

    public static void removeItem(String category, ItemStack item) {
        getInfo(category).removeItem(item);
    }

    public ChestGenHooks(String category) {
        this.category = category;
    }

    public ChestGenHooks(String category, WeightedRandomChestContent[] items, int min, int max) {
        this(category);

        for(WeightedRandomChestContent item : items) {
            this.contents.add(item);
        }

        this.countMin = min;
        this.countMax = max;
    }

    public void addItem(WeightedRandomChestContent item) {
        this.contents.add(item);
    }

    public void removeItem(ItemStack item) {
        Iterator<WeightedRandomChestContent> itr = this.contents.iterator();

        while(itr.hasNext()) {
            WeightedRandomChestContent cont = (WeightedRandomChestContent)itr.next();
            if (item.equalsIgnoreNbt(cont.itemStack) || item.getMeta() == -1 && item.id == cont.itemStack.id) {
                itr.remove();
            }
        }

    }

    public WeightedRandomChestContent[] getItems() {
        return (WeightedRandomChestContent[])this.contents.toArray(new WeightedRandomChestContent[this.contents.size()]);
    }

    public int getCount(Random rand) {
        return this.countMin < this.countMax ? this.countMin + rand.nextInt(this.countMax - this.countMin) : this.countMin;
    }

    public int getMin() {
        return this.countMin;
    }

    public int getMax() {
        return this.countMax;
    }

    public void setMin(int value) {
        this.countMin = value;
    }

    public void setMax(int value) {
        this.countMax = value;
    }

    static {
        init();
    }
}
