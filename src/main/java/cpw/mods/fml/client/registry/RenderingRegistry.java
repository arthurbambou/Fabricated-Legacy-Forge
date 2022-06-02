package cpw.mods.fml.client.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import cpw.mods.fml.client.SpriteHelper;
import cpw.mods.fml.client.TextureFXManager;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.class_535;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;

public class RenderingRegistry {
    private static final RenderingRegistry INSTANCE = new RenderingRegistry();
    private int nextRenderId = 36;
    private Map<Integer, ISimpleBlockRenderingHandler> blockRenderers = Maps.newHashMap();
    private List<RenderingRegistry.EntityRendererInfo> entityRenderers = Lists.newArrayList();

    public RenderingRegistry() {
    }

    public static int addNewArmourRendererPrefix(String armor) {
        PlayerEntityRenderer.field_2136 = (String[])ObjectArrays.concat(PlayerEntityRenderer.field_2136, armor);
        BipedEntityRenderer.field_5195 = PlayerEntityRenderer.field_2136;
        return PlayerEntityRenderer.field_2136.length - 1;
    }

    public static void registerEntityRenderingHandler(Class<? extends Entity> entityClass, EntityRenderer renderer) {
        instance().entityRenderers.add(new RenderingRegistry.EntityRendererInfo(entityClass, renderer));
    }

    public static void registerBlockHandler(ISimpleBlockRenderingHandler handler) {
        instance().blockRenderers.put(handler.getRenderId(), handler);
    }

    public static void registerBlockHandler(int renderId, ISimpleBlockRenderingHandler handler) {
        instance().blockRenderers.put(renderId, handler);
    }

    public static int getNextAvailableRenderId() {
        RenderingRegistry var10000 = instance();
        int var0 = var10000.nextRenderId;
        var10000.nextRenderId += 1;
        return var0;
    }

    public static int addTextureOverride(String fileToOverride, String fileToAdd) {
        int idx = SpriteHelper.getUniqueSpriteIndex(fileToOverride);
        addTextureOverride(fileToOverride, fileToAdd, idx);
        return idx;
    }

    public static void addTextureOverride(String path, String overlayPath, int index) {
        TextureFXManager.instance().addNewTextureOverride(path, overlayPath, index);
    }

    public static int getUniqueTextureIndex(String path) {
        return SpriteHelper.getUniqueSpriteIndex(path);
    }

    @Deprecated
    public static RenderingRegistry instance() {
        return INSTANCE;
    }

    public boolean renderWorldBlock(class_535 renderer, BlockView world, int x, int y, int z, Block block, int modelId) {
        if (!this.blockRenderers.containsKey(modelId)) {
            return false;
        } else {
            ISimpleBlockRenderingHandler bri = (ISimpleBlockRenderingHandler)this.blockRenderers.get(modelId);
            return bri.renderWorldBlock(world, x, y, z, block, modelId, renderer);
        }
    }

    public void renderInventoryBlock(class_535 renderer, Block block, int metadata, int modelID) {
        if (this.blockRenderers.containsKey(modelID)) {
            ISimpleBlockRenderingHandler bri = (ISimpleBlockRenderingHandler)this.blockRenderers.get(modelID);
            bri.renderInventoryBlock(block, metadata, modelID, renderer);
        }
    }

    public boolean renderItemAsFull3DBlock(int modelId) {
        ISimpleBlockRenderingHandler bri = (ISimpleBlockRenderingHandler)this.blockRenderers.get(modelId);
        return bri != null && bri.shouldRender3DInInventory();
    }

    public void loadEntityRenderers(Map<Class<? extends Entity>, EntityRenderer> rendererMap) {
        for(RenderingRegistry.EntityRendererInfo info : this.entityRenderers) {
            rendererMap.put(info.target, info.renderer);
            info.renderer.method_1528(EntityRenderDispatcher.field_2094);
        }

    }

    private static class EntityRendererInfo {
        private Class<? extends Entity> target;
        private EntityRenderer renderer;

        public EntityRendererInfo(Class<? extends Entity> target, EntityRenderer renderer) {
            this.target = target;
            this.renderer = renderer;
        }
    }
}
