package fr.catcore.fabricatedforge.mixininterface;

import net.minecraft.network.Connection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdate_S2CPacket;

public interface IBlockEntity {

    boolean canUpdate();

    void onDataPacket(Connection net, BlockEntityUpdate_S2CPacket pkt);

    void onChunkUnload();
}
