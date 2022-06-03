package fr.catcore.fabricatedforge.mixin.forgefml.client;

import cpw.mods.fml.common.network.FMLNetworkHandler;
import fr.catcore.fabricatedforge.mixininterface.Iclass_469;
import net.minecraft.block.entity.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_469;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.VillagerTradingScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.network.Connection;
import net.minecraft.network.Packet;
import net.minecraft.network.class_690;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdate_S2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessage_S2CPacket;
import net.minecraft.network.packet.s2c.play.Disconnect_S2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdate_S2CPacket;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Language;
import net.minecraft.village.TraderOfferList;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Mixin(class_469.class)
public abstract class class_469Mixin extends PacketListener implements Iclass_469 {

    @Shadow private Connection connection;

    @Shadow public abstract void sendPacket(Packet packet);

    @Shadow private boolean disconnected;
    @Shadow private Minecraft field_1623;

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/lang/String;I)V", at = @At("RETURN"))
    private void FMLOnClientConnectionToRemoteServer(Minecraft par1Minecraft, String par2Str, int par3, CallbackInfo ci) {
        FMLNetworkHandler.onClientConnectionToRemoteServer((class_469)(Object)this, par2Str, par3, this.connection);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/server/integrated/IntegratedServer;)V", at = @At("RETURN"))
    private void FMLOnClientConnectionToIntegratedServer(Minecraft par1Minecraft, IntegratedServer par2IntegratedServer, CallbackInfo ci) {
        FMLNetworkHandler.onClientConnectionToIntegratedServer((class_469)(Object)this, par2IntegratedServer, this.connection);
    }

    @Inject(method = "onKey", at = @At("HEAD"))
    private void FMLFakeLoginPacket(LoginKeyC2SPacket par1, CallbackInfo ci) {
        this.sendPacket(FMLNetworkHandler.getFMLFakeLoginPacket());
    }

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;onPlayerModelPartChange()V"))
    private void FMLOnConnectionEstablishedToServer(class_690 par1Packet1Login, CallbackInfo ci) {
        FMLNetworkHandler.onConnectionEstablishedToServer((class_469)(Object)this, this.connection, par1Packet1Login);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void onDisconnect(Disconnect_S2CPacket par1Packet255KickDisconnect) {
        this.connection.disconnect("disconnect.kicked", par1Packet255KickDisconnect.reason);
        this.disconnected = true;
        this.field_1623.connect((ClientWorld)null);
        this.field_1623
                .openScreen(new DisconnectedScreen("disconnect.disconnected", "disconnect.genericReason", new Object[]{par1Packet255KickDisconnect.reason}));
    }

    @Inject(method = "sendPacketAndDisconnect", at = @At(value = "RETURN"))
    private void FMLOnConnectionClosed(Packet par1, CallbackInfo ci) {
        if (!this.disconnected) FMLNetworkHandler.onConnectionClosed(this.connection, this.getPlayer());
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void onChatMessage(ChatMessage_S2CPacket par1Packet3Chat) {
        par1Packet3Chat = FMLNetworkHandler.handleChatMessage(this, par1Packet3Chat);
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(par1Packet3Chat.message);
        if (!MinecraftForge.EVENT_BUS.post(event) && event.message != null) {
            this.field_1623.inGameHud.getChatHud().method_898(par1Packet3Chat.message);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void onBlockEntityUpdate(BlockEntityUpdate_S2CPacket par1Packet132TileEntityData) {
        if (this.field_1623.world.isPosLoaded(par1Packet132TileEntityData.x, par1Packet132TileEntityData.y, par1Packet132TileEntityData.z)) {
            BlockEntity var2 = this.field_1623.world.method_3781(par1Packet132TileEntityData.x, par1Packet132TileEntityData.y, par1Packet132TileEntityData.z);
            if (var2 != null) {
                if (par1Packet132TileEntityData.type == 1 && var2 instanceof MobSpawnerBlockEntity) {
                    var2.fromNbt(par1Packet132TileEntityData.nbt);
                } else if (par1Packet132TileEntityData.type == 2 && var2 instanceof CommandBlockBlockEntity) {
                    var2.fromNbt(par1Packet132TileEntityData.nbt);
                } else if (par1Packet132TileEntityData.type == 3 && var2 instanceof BeaconBlockEntity) {
                    var2.fromNbt(par1Packet132TileEntityData.nbt);
                } else if (par1Packet132TileEntityData.type == 4 && var2 instanceof SkullBlockEntity) {
                    var2.fromNbt(par1Packet132TileEntityData.nbt);
                } else {
                    var2.onDataPacket(this.connection, par1Packet132TileEntityData);
                }
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void onMapUpdate(MapUpdate_S2CPacket par1Packet131MapData) {
        FMLNetworkHandler.handlePacket131Packet(this, par1Packet131MapData);
    }

    @Override
    public void fmlPacket131Callback(MapUpdate_S2CPacket par1Packet131MapData) {
        if (par1Packet131MapData.item == Item.MAP.id) {
            FilledMapItem.method_3455(par1Packet131MapData.id, this.field_1623.world).method_185(par1Packet131MapData.data);
        } else {
            System.out.println("Unknown itemid: " + par1Packet131MapData.id);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void onCustomPayload(CustomPayloadC2SPacket par1Packet250CustomPayload) {
        FMLNetworkHandler.handlePacket250Packet(par1Packet250CustomPayload, this.connection, this);
    }

    @Override
    public void handleVanilla250Packet(CustomPayloadC2SPacket par1Packet250CustomPayload) {
        if ("MC|TPack".equals(par1Packet250CustomPayload.channel)) {
            String[] var2 = new String(par1Packet250CustomPayload.field_2455).split("\u0000");
            String var3 = var2[0];
            if (var2[1].equals("16")) {
                if (this.field_1623.texturePackManager.method_1691()) {
                    this.field_1623.texturePackManager.method_1683(var3);
                } else if (this.field_1623.texturePackManager.method_1690()) {
                    this.field_1623
                            .openScreen(
                                    new ConfirmScreen(
                                            class_470Accessor.newInstance((class_469)(Object) this, var3),
                                            Language.getInstance().translate("multiplayer.texturePrompt.line1"),
                                            Language.getInstance().translate("multiplayer.texturePrompt.line2"),
                                            0
                                    )
                            );
                }
            }
        } else if ("MC|TrList".equals(par1Packet250CustomPayload.channel)) {
            DataInputStream var8 = new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.field_2455));

            try {
                int var9 = var8.readInt();
                Screen var4 = this.field_1623.currentScreen;
                if (var4 != null && var4 instanceof VillagerTradingScreen && var9 == this.field_1623.playerEntity.openScreenHandler.syncId) {
                    Trader var5 = ((VillagerTradingScreen)var4).getTrader();
                    TraderOfferList var6 = TraderOfferList.method_3559(var8);
                    var5.setTraderOfferList(var6);
                }
            } catch (IOException var71) {
                var71.printStackTrace();
            }
        }

    }

    @Override
    public PlayerEntity getPlayer() {
        return this.field_1623.playerEntity;
    }
}
