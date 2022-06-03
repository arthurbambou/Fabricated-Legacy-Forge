package fr.catcore.fabricatedforge.mixin.forgefml.block.entity;

import fr.catcore.fabricatedforge.mixininterface.IBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Connection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdate_S2CPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements IBlockEntity {

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void onDataPacket(Connection net, BlockEntityUpdate_S2CPacket pkt) {
    }

    @Override
    public void onChunkUnload() {
    }
}
