package cpw.mods.fml.client.registry;

import net.minecraft.block.Block;
import net.minecraft.client.class_535;
import net.minecraft.world.BlockView;

public interface ISimpleBlockRenderingHandler {
    void renderInventoryBlock(Block arg, int i, int j, class_535 arg2);

    boolean renderWorldBlock(BlockView arg, int i, int j, int k, Block arg2, int l, class_535 arg3);

    boolean shouldRender3DInInventory();

    int getRenderId();
}
