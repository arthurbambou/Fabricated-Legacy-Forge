package cpw.mods.fml.client.modloader;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.BaseMod;
import net.minecraft.block.Block;
import net.minecraft.client.class_535;
import net.minecraft.world.BlockView;

public class ModLoaderBlockRendererHandler implements ISimpleBlockRenderingHandler {
    private int renderId;
    private boolean render3dInInventory;
    private BaseMod mod;

    public ModLoaderBlockRendererHandler(int renderId, boolean render3dInInventory, BaseMod mod) {
        this.renderId = renderId;
        this.render3dInInventory = render3dInInventory;
        this.mod = mod;
    }

    public int getRenderId() {
        return this.renderId;
    }

    public boolean shouldRender3DInInventory() {
        return this.render3dInInventory;
    }

    public boolean renderWorldBlock(BlockView world, int x, int y, int z, Block block, int modelId, class_535 renderer) {
        return this.mod.renderWorldBlock(renderer, world, x, y, z, block, modelId);
    }

    public void renderInventoryBlock(Block block, int metadata, int modelID, class_535 renderer) {
        this.mod.renderInvBlock(renderer, block, metadata, modelID);
    }
}
