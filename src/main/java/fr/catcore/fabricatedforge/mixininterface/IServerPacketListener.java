package fr.catcore.fabricatedforge.mixininterface;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface IServerPacketListener {
    ServerPlayerEntity getPlayer();
}
