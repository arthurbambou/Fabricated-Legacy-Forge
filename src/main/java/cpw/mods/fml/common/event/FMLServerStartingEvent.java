package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState.ModState;
import net.minecraft.command.Command;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandRegistry;

public class FMLServerStartingEvent extends FMLStateEvent {
    private MinecraftServer server;

    public FMLServerStartingEvent(Object... data) {
        super(data);
        this.server = (MinecraftServer)data[0];
    }

    public ModState getModState() {
        return ModState.AVAILABLE;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public void registerServerCommand(Command command) {
        CommandRegistry ch = (CommandRegistry)this.getServer().getCommandManager();
        ch.registerCommand(command);
    }
}
