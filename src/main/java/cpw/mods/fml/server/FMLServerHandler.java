package cpw.mods.fml.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IFMLSidedHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.network.ModMissingPacket;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.MapUpdate_S2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class FMLServerHandler implements IFMLSidedHandler {
    private static final FMLServerHandler INSTANCE = new FMLServerHandler();
    private MinecraftServer server;

    private FMLServerHandler() {
        FMLCommonHandler.instance().beginLoading(this);
    }

    public void beginServerLoading(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        ObfuscationReflectionHelper.detectObfuscation(World.class);
        Loader.instance().loadMods();
    }

    public void finishServerLoading() {
        Loader.instance().initializeMods();
        LanguageRegistry.reloadLanguageTable();
    }

    public void haltGame(String message, Throwable exception) {
        throw new RuntimeException(message, exception);
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public static FMLServerHandler instance() {
        return INSTANCE;
    }

    public List<String> getAdditionalBrandingInformation() {
        return null;
    }

    public Side getSide() {
        return Side.SERVER;
    }

    public void showGuiScreen(Object clientGuiElement) {
    }

    public Entity spawnEntityIntoClientWorld(EntityRegistration er, EntitySpawnPacket packet) {
        return null;
    }

    public void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket entitySpawnAdjustmentPacket) {
    }

    public void sendPacket(Packet packet) {
        throw new RuntimeException("You cannot send a bare packet without a target on the server!");
    }

    public void displayMissingMods(ModMissingPacket modMissingPacket) {
    }

    public void handleTinyPacket(PacketListener handler, MapUpdate_S2CPacket mapData) {
    }

    public void setClientCompatibilityLevel(byte compatibilityLevel) {
    }

    public byte getClientCompatibilityLevel() {
        return 0;
    }
}
