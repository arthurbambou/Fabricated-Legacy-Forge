package cpw.mods.fml.common.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLPacket.Type;
import net.minecraft.network.Connection;
import net.minecraft.network.listener.PacketListener;

public class EntitySpawnAdjustmentPacket extends FMLPacket {
    public int entityId;
    public int serverX;
    public int serverY;
    public int serverZ;

    public EntitySpawnAdjustmentPacket() {
        super(Type.ENTITYSPAWNADJUSTMENT);
    }

    public byte[] generatePacket(Object... data) {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        dat.writeInt(data[0]);
        dat.writeInt(data[1]);
        dat.writeInt(data[2]);
        dat.writeInt(data[3]);
        return dat.toByteArray();
    }

    public FMLPacket consumePacket(byte[] data) {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        this.entityId = dat.readInt();
        this.serverX = dat.readInt();
        this.serverY = dat.readInt();
        this.serverZ = dat.readInt();
        return this;
    }

    public void execute(Connection network, FMLNetworkHandler handler, PacketListener netHandler, String userName) {
        FMLCommonHandler.instance().adjustEntityLocationOnClient(this);
    }
}
