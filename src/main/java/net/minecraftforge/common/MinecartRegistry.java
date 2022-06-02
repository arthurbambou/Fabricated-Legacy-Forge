package net.minecraftforge.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MinecartRegistry {
    private static Map<MinecartRegistry.MinecartKey, ItemStack> itemForMinecart = new HashMap();
    private static Map<ItemStack, MinecartRegistry.MinecartKey> minecartForItem = new HashMap();

    public MinecartRegistry() {
    }

    public static void registerMinecart(Class<? extends AbstractMinecartEntity> cart, ItemStack item) {
        registerMinecart(cart, 0, item);
    }

    public static void registerMinecart(Class<? extends AbstractMinecartEntity> minecart, int type, ItemStack item) {
        MinecartRegistry.MinecartKey key = new MinecartRegistry.MinecartKey(minecart, type);
        itemForMinecart.put(key, item);
        minecartForItem.put(item, key);
    }

    public static void removeMinecart(Class<? extends AbstractMinecartEntity> minecart, int type) {
        MinecartRegistry.MinecartKey key = new MinecartRegistry.MinecartKey(minecart, type);
        ItemStack item = (ItemStack)itemForMinecart.remove(key);
        if (item != null) {
            minecartForItem.remove(item);
        }

    }

    public static ItemStack getItemForCart(Class<? extends AbstractMinecartEntity> minecart) {
        return getItemForCart(minecart, 0);
    }

    public static ItemStack getItemForCart(Class<? extends AbstractMinecartEntity> minecart, int type) {
        ItemStack item = (ItemStack)itemForMinecart.get(new MinecartRegistry.MinecartKey(minecart, type));
        return item == null ? null : item.copy();
    }

    public static ItemStack getItemForCart(AbstractMinecartEntity cart) {
        return getItemForCart(cart.getClass(), cart.getMinecartType());
    }

    public static Class<? extends AbstractMinecartEntity> getCartClassForItem(ItemStack item) {
        MinecartRegistry.MinecartKey key = null;

        for(Entry<ItemStack, MinecartRegistry.MinecartKey> entry : minecartForItem.entrySet()) {
            if (((ItemStack)entry.getKey()).equalsIgnoreNbt(item)) {
                key = (MinecartRegistry.MinecartKey)entry.getValue();
                break;
            }
        }

        return key != null ? key.minecart : null;
    }

    public static int getCartTypeForItem(ItemStack item) {
        MinecartRegistry.MinecartKey key = null;

        for(Entry<ItemStack, MinecartRegistry.MinecartKey> entry : minecartForItem.entrySet()) {
            if (((ItemStack)entry.getKey()).equalsIgnoreNbt(item)) {
                key = (MinecartRegistry.MinecartKey)entry.getValue();
                break;
            }
        }

        return key != null ? key.type : -1;
    }

    public static Set<ItemStack> getAllCartItems() {
        Set<ItemStack> ret = new HashSet();

        for(ItemStack item : minecartForItem.keySet()) {
            ret.add(item.copy());
        }

        return ret;
    }

    static {
        registerMinecart(AbstractMinecartEntity.class, 0, new ItemStack(Item.MINECART));
        registerMinecart(AbstractMinecartEntity.class, 1, new ItemStack(Item.MINECART_CHEST));
        registerMinecart(AbstractMinecartEntity.class, 2, new ItemStack(Item.MINECART_FURNACE));
    }

    public static class MinecartKey {
        public final Class<? extends AbstractMinecartEntity> minecart;
        public final int type;

        public MinecartKey(Class<? extends AbstractMinecartEntity> cls, int typtID) {
            this.minecart = cls;
            this.type = typtID;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (this.getClass() != obj.getClass()) {
                return false;
            } else {
                MinecartRegistry.MinecartKey other = (MinecartRegistry.MinecartKey)obj;
                if (this.minecart == other.minecart || this.minecart != null && this.minecart.equals(other.minecart)) {
                    return this.type == other.type;
                } else {
                    return false;
                }
            }
        }

        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + (this.minecart != null ? this.minecart.hashCode() : 0);
            return 59 * hash + this.type;
        }
    }
}
