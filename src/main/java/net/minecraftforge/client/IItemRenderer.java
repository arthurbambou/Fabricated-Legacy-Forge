package net.minecraftforge.client;

import net.minecraft.item.ItemStack;

public interface IItemRenderer {
    boolean handleRenderType(ItemStack arg, IItemRenderer.ItemRenderType itemRenderType);

    boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType itemRenderType, ItemStack arg, IItemRenderer.ItemRendererHelper itemRendererHelper);

    void renderItem(IItemRenderer.ItemRenderType itemRenderType, ItemStack arg, Object... objects);

    public static enum ItemRenderType {
        ENTITY,
        EQUIPPED,
        INVENTORY,
        FIRST_PERSON_MAP;

        private ItemRenderType() {
        }
    }

    public static enum ItemRendererHelper {
        ENTITY_ROTATION,
        ENTITY_BOBBING,
        EQUIPPED_BLOCK,
        BLOCK_3D,
        INVENTORY_BLOCK;

        private ItemRendererHelper() {
        }
    }
}
