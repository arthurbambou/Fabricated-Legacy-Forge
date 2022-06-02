package net.minecraftforge.oredict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

public class OreDictionary {
    private static int maxID = 0;
    private static HashMap<String, Integer> oreIDs = new HashMap();
    private static HashMap<Integer, ArrayList<ItemStack>> oreStacks = new HashMap();

    public OreDictionary() {
    }

    public static int getOreID(String name) {
        Integer val = (Integer)oreIDs.get(name);
        if (val == null) {
            val = maxID++;
            oreIDs.put(name, val);
            oreStacks.put(val, new ArrayList());
        }

        return val;
    }

    public static String getOreName(int id) {
        for(Entry<String, Integer> entry : oreIDs.entrySet()) {
            if (id == entry.getValue()) {
                return (String)entry.getKey();
            }
        }

        return "Unknown";
    }

    public static ArrayList<ItemStack> getOres(String name) {
        return getOres(getOreID(name));
    }

    public static String[] getOreNames() {
        return (String[])oreIDs.keySet().toArray(new String[0]);
    }

    public static ArrayList<ItemStack> getOres(Integer id) {
        ArrayList<ItemStack> val = (ArrayList)oreStacks.get(id);
        if (val == null) {
            val = new ArrayList();
            oreStacks.put(id, val);
        }

        return val;
    }

    public static void registerOre(String name, Item ore) {
        registerOre(name, new ItemStack(ore));
    }

    public static void registerOre(String name, Block ore) {
        registerOre(name, new ItemStack(ore));
    }

    public static void registerOre(String name, ItemStack ore) {
        registerOre(name, getOreID(name), ore);
    }

    public static void registerOre(int id, Item ore) {
        registerOre(id, new ItemStack(ore));
    }

    public static void registerOre(int id, Block ore) {
        registerOre(id, new ItemStack(ore));
    }

    public static void registerOre(int id, ItemStack ore) {
        registerOre(getOreName(id), id, ore);
    }

    private static void registerOre(String name, int id, ItemStack ore) {
        ArrayList<ItemStack> ores = getOres(id);
        ore = ore.copy();
        ores.add(ore);
        MinecraftForge.EVENT_BUS.post(new OreDictionary.OreRegisterEvent(name, ore));
    }

    public static class OreRegisterEvent extends Event {
        public final String Name;
        public final ItemStack Ore;

        public OreRegisterEvent(String name, ItemStack ore) {
            this.Name = name;
            this.Ore = ore;
        }
    }
}
