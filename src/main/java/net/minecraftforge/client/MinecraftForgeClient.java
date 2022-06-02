package net.minecraftforge.client;

import net.minecraft.block.Block;
import net.minecraft.client.class_535;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

public class MinecraftForgeClient {
    private static IItemRenderer[] customItemRenderers = new IItemRenderer[Item.ITEMS.length];

    public MinecraftForgeClient() {
    }

    public static void registerRenderContextHandler(String texture, int subid, IRenderContextHandler handler) {
        ForgeHooksClient.registerRenderContextHandler(texture, subid, handler);
    }

    public static void preloadTexture(String texture) {
        ForgeHooksClient.engine().getTextureFromPath(texture);
    }

    public static void renderBlock(class_535 render, Block block, int x, int y, int z) {
        ForgeHooksClient.beforeBlockRender(block, render);
        render.method_1458(block, x, y, z);
        ForgeHooksClient.afterBlockRender(block, render);
    }

    public static int getRenderPass() {
        return ForgeHooksClient.renderPass;
    }

    public static void registerItemRenderer(int itemID, IItemRenderer renderer) {
        customItemRenderers[itemID] = renderer;
    }

    public static IItemRenderer getItemRenderer(ItemStack item, ItemRenderType type) {
        IItemRenderer renderer = customItemRenderers[item.id];
        return renderer != null && renderer.handleRenderType(item, type) ? customItemRenderers[item.id] : null;
    }
}
