package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Connection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

public interface IModLoaderSidedHelper {
    void finishModLoading(ModLoaderModContainer modLoaderModContainer);

    Object getClientGui(BaseModProxy baseModProxy, PlayerEntity arg, int i, int j, int k, int l);

    Entity spawnEntity(BaseModProxy baseModProxy, EntitySpawnPacket entitySpawnPacket, EntityRegistration entityRegistration);

    void sendClientPacket(BaseModProxy baseModProxy, CustomPayloadC2SPacket arg);

    void clientConnectionOpened(PacketListener arg, Connection arg2, BaseModProxy baseModProxy);

    boolean clientConnectionClosed(Connection arg, BaseModProxy baseModProxy);
}
