package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.TickType;
import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Connection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.ServerPacketListener;
import net.minecraft.world.World;

public interface BaseModProxy {
    void modsLoaded();

    void load();

    String getName();

    String getPriorities();

    String getVersion();

    boolean doTickInGUI(TickType tickType, boolean bl, Object... objects);

    boolean doTickInGame(TickType tickType, boolean bl, Object... objects);

    void generateSurface(World arg, Random random, int i, int j);

    void generateNether(World arg, Random random, int i, int j);

    int addFuel(int i, int j);

    void takenFromCrafting(PlayerEntity arg, ItemStack arg2, Inventory arg3);

    void takenFromFurnace(PlayerEntity arg, ItemStack arg2);

    void onClientLogout(Connection arg);

    void onClientLogin(PlayerEntity arg);

    void serverDisconnect();

    void serverConnect(PacketListener arg);

    void receiveCustomPacket(CustomPayloadC2SPacket arg);

    void clientChat(String string);

    void onItemPickup(PlayerEntity arg, ItemStack arg2);

    int dispenseEntity(World arg, ItemStack arg2, Random random, int i, int j, int k, int l, int m, double d, double e, double f);

    void serverCustomPayload(ServerPacketListener arg, CustomPayloadC2SPacket arg2);

    void serverChat(ServerPacketListener arg, String string);
}
