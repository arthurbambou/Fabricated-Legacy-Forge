package cpw.mods.fml.common.network;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.network.FMLPacket.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.Connection;
import net.minecraft.network.PendingConnection;
import net.minecraft.network.class_690;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatMessage_S2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdate_S2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPacketListener;
import net.minecraft.world.World;

public class NetworkRegistry {
    private static final NetworkRegistry INSTANCE = new NetworkRegistry();
    private Multimap<Player, String> activeChannels = ArrayListMultimap.create();
    private Multimap<String, IPacketHandler> universalPacketHandlers = ArrayListMultimap.create();
    private Multimap<String, IPacketHandler> clientPacketHandlers = ArrayListMultimap.create();
    private Multimap<String, IPacketHandler> serverPacketHandlers = ArrayListMultimap.create();
    private Set<IConnectionHandler> connectionHandlers = Sets.newLinkedHashSet();
    private Map<ModContainer, IGuiHandler> serverGuiHandlers = Maps.newHashMap();
    private Map<ModContainer, IGuiHandler> clientGuiHandlers = Maps.newHashMap();
    private List<IChatListener> chatListeners = Lists.newArrayList();

    public NetworkRegistry() {
    }

    public static NetworkRegistry instance() {
        return INSTANCE;
    }

    byte[] getPacketRegistry(Side side) {
        return Joiner.on('\u0000')
                .join(
                        Iterables.concat(
                                Arrays.asList("FML"),
                                this.universalPacketHandlers.keySet(),
                                side.isClient() ? this.clientPacketHandlers.keySet() : this.serverPacketHandlers.keySet()
                        )
                )
                .getBytes(Charsets.UTF_8);
    }

    public boolean isChannelActive(String channel, Player player) {
        return this.activeChannels.containsEntry(player, channel);
    }

    public void registerChannel(IPacketHandler handler, String channelName) {
        if (Strings.isNullOrEmpty(channelName) || channelName != null && channelName.length() > 16) {
            FMLLog.severe(
                    "Invalid channel name '%s' : %s",
                    new Object[]{channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)"}
            );
            throw new RuntimeException("Channel name is invalid");
        } else {
            this.universalPacketHandlers.put(channelName, handler);
        }
    }

    public void registerChannel(IPacketHandler handler, String channelName, Side side) {
        if (side == null) {
            this.registerChannel(handler, channelName);
        } else if (Strings.isNullOrEmpty(channelName) || channelName != null && channelName.length() > 16) {
            FMLLog.severe(
                    "Invalid channel name '%s' : %s",
                    new Object[]{channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)"}
            );
            throw new RuntimeException("Channel name is invalid");
        } else {
            if (side.isClient()) {
                this.clientPacketHandlers.put(channelName, handler);
            } else {
                this.serverPacketHandlers.put(channelName, handler);
            }

        }
    }

    void activateChannel(Player player, String channel) {
        this.activeChannels.put(player, channel);
    }

    void deactivateChannel(Player player, String channel) {
        this.activeChannels.remove(player, channel);
    }

    public void registerConnectionHandler(IConnectionHandler handler) {
        this.connectionHandlers.add(handler);
    }

    public void registerChatListener(IChatListener listener) {
        this.chatListeners.add(listener);
    }

    void playerLoggedIn(ServerPlayerEntity player, ServerPacketListener netHandler, Connection manager) {
        this.generateChannelRegistration(player, netHandler, manager);

        for(IConnectionHandler handler : this.connectionHandlers) {
            handler.playerLoggedIn((Player)player, netHandler, manager);
        }

    }

    String connectionReceived(PendingConnection netHandler, Connection manager) {
        for(IConnectionHandler handler : this.connectionHandlers) {
            String kick = handler.connectionReceived(netHandler, manager);
            if (!Strings.isNullOrEmpty(kick)) {
                return kick;
            }
        }

        return null;
    }

    void connectionOpened(PacketListener netClientHandler, String server, int port, Connection networkManager) {
        for(IConnectionHandler handler : this.connectionHandlers) {
            handler.connectionOpened(netClientHandler, server, port, networkManager);
        }

    }

    void connectionOpened(PacketListener netClientHandler, MinecraftServer server, Connection networkManager) {
        for(IConnectionHandler handler : this.connectionHandlers) {
            handler.connectionOpened(netClientHandler, server, networkManager);
        }

    }

    void clientLoggedIn(PacketListener clientHandler, Connection manager, class_690 login) {
        this.generateChannelRegistration(clientHandler.getPlayer(), clientHandler, manager);

        for(IConnectionHandler handler : this.connectionHandlers) {
            handler.clientLoggedIn(clientHandler, manager, login);
        }

    }

    void connectionClosed(Connection manager, PlayerEntity player) {
        for(IConnectionHandler handler : this.connectionHandlers) {
            handler.connectionClosed(manager);
        }

        this.activeChannels.removeAll(player);
    }

    void generateChannelRegistration(PlayerEntity player, PacketListener netHandler, Connection manager) {
        CustomPayloadC2SPacket pkt = new CustomPayloadC2SPacket();
        pkt.channel = "REGISTER";
        pkt.field_2455 = this.getPacketRegistry(player instanceof ServerPlayerEntity ? Side.SERVER : Side.CLIENT);
        pkt.field_2454 = pkt.field_2455.length;
        manager.send(pkt);
    }

    void handleCustomPacket(CustomPayloadC2SPacket packet, Connection network, PacketListener handler) {
        if ("REGISTER".equals(packet.channel)) {
            this.handleRegistrationPacket(packet, (Player)handler.getPlayer());
        } else if ("UNREGISTER".equals(packet.channel)) {
            this.handleUnregistrationPacket(packet, (Player)handler.getPlayer());
        } else {
            this.handlePacket(packet, network, (Player)handler.getPlayer());
        }

    }

    private void handlePacket(CustomPayloadC2SPacket packet, Connection network, Player player) {
        String channel = packet.channel;

        for(IPacketHandler handler : Iterables.concat(
                this.universalPacketHandlers.get(channel),
                player instanceof ServerPlayerEntity ? this.serverPacketHandlers.get(channel) : this.clientPacketHandlers.get(channel)
        )) {
            handler.onPacketData(network, packet, player);
        }

    }

    private void handleRegistrationPacket(CustomPayloadC2SPacket packet, Player player) {
        for(String channel : this.extractChannelList(packet)) {
            this.activateChannel(player, channel);
        }

    }

    private void handleUnregistrationPacket(CustomPayloadC2SPacket packet, Player player) {
        for(String channel : this.extractChannelList(packet)) {
            this.deactivateChannel(player, channel);
        }

    }

    private List<String> extractChannelList(CustomPayloadC2SPacket packet) {
        String request = new String(packet.field_2455, Charsets.UTF_8);
        List<String> channels = Lists.newArrayList(Splitter.on('\u0000').split(request));
        return channels;
    }

    public void registerGuiHandler(Object mod, IGuiHandler handler) {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (mc == null) {
            mc = Loader.instance().activeModContainer();
            FMLLog.log(Level.WARNING, "Mod %s attempted to register a gui network handler during a construction phase", new Object[]{mc.getModId()});
        }

        NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);
        if (nmh == null) {
            FMLLog.log(Level.FINE, "The mod %s needs to be a @NetworkMod to register a Networked Gui Handler", new Object[]{mc.getModId()});
        } else {
            this.serverGuiHandlers.put(mc, handler);
        }

        this.clientGuiHandlers.put(mc, handler);
    }

    void openRemoteGui(ModContainer mc, ServerPlayerEntity player, int modGuiId, World world, int x, int y, int z) {
        IGuiHandler handler = (IGuiHandler)this.serverGuiHandlers.get(mc);
        NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);
        if (handler != null && nmh != null) {
            ScreenHandler container = (ScreenHandler)handler.getServerGuiElement(modGuiId, player, world, x, y, z);
            if (container != null) {
                player.incrementSyncId();
                player.closeOpenedScreenHandler();
                int windowId = player.screenHandlerSyncId;
                CustomPayloadC2SPacket pkt = new CustomPayloadC2SPacket();
                pkt.channel = "FML";
                pkt.field_2455 = FMLPacket.makePacket(Type.GUIOPEN, new Object[]{windowId, nmh.getNetworkId(), modGuiId, x, y, z});
                pkt.field_2454 = pkt.field_2455.length;
                player.field_2823.sendPacket(pkt);
                player.openScreenHandler = container;
                player.openScreenHandler.syncId = windowId;
                player.openScreenHandler.addListener(player);
            }
        }

    }

    void openLocalGui(ModContainer mc, PlayerEntity player, int modGuiId, World world, int x, int y, int z) {
        IGuiHandler handler = (IGuiHandler)this.clientGuiHandlers.get(mc);
        FMLCommonHandler.instance().showGuiScreen(handler.getClientGuiElement(modGuiId, player, world, x, y, z));
    }

    public ChatMessage_S2CPacket handleChat(PacketListener handler, ChatMessage_S2CPacket chat) {
        Side s = Side.CLIENT;
        if (handler instanceof ServerPacketListener) {
            s = Side.SERVER;
        }

        for(IChatListener listener : this.chatListeners) {
            chat = s.isClient() ? listener.clientChat(handler, chat) : listener.serverChat(handler, chat);
        }

        return chat;
    }

    public void handleTinyPacket(PacketListener handler, MapUpdate_S2CPacket mapData) {
        NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(Integer.valueOf(mapData.item));
        if (nmh == null) {
            FMLLog.info("Received a tiny packet for network id %d that is not recognised here", new Object[]{mapData.item});
        } else {
            if (nmh.hasTinyPacketHandler()) {
                nmh.getTinyPacketHandler().handle(handler, mapData);
            } else {
                FMLLog.info("Received a tiny packet for a network mod that does not accept tiny packets %s", new Object[]{nmh.getContainer().getModId()});
            }

        }
    }
}
