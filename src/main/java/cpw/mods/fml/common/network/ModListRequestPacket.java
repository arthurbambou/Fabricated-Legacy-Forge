package cpw.mods.fml.common.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLPacket.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.network.Connection;
import net.minecraft.network.listener.PacketListener;

public class ModListRequestPacket extends FMLPacket {
    private List<String> sentModList;
    private byte compatibilityLevel;

    public ModListRequestPacket() {
        super(Type.MOD_LIST_REQUEST);
    }

    public byte[] generatePacket(Object... data) {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        Set<ModContainer> activeMods = FMLNetworkHandler.instance().getNetworkModList();
        dat.writeInt(activeMods.size());

        for(ModContainer mc : activeMods) {
            dat.writeUTF(mc.getModId());
        }

        dat.writeByte(FMLNetworkHandler.getCompatibilityLevel());
        return dat.toByteArray();
    }

    public FMLPacket consumePacket(byte[] data) {
        this.sentModList = Lists.newArrayList();
        ByteArrayDataInput in = ByteStreams.newDataInput(data);
        int listSize = in.readInt();

        for(int i = 0; i < listSize; ++i) {
            this.sentModList.add(in.readUTF());
        }

        try {
            this.compatibilityLevel = in.readByte();
        } catch (IllegalStateException var5) {
            FMLLog.fine("No compatibility byte found - the server is too old", new Object[0]);
        }

        return this;
    }

    public void execute(Connection mgr, FMLNetworkHandler handler, PacketListener netHandler, String userName) {
        List<String> missingMods = Lists.newArrayList();
        Map<String, String> modVersions = Maps.newHashMap();
        Map<String, ModContainer> indexedModList = Maps.newHashMap(Loader.instance().getIndexedModList());

        for(String m : this.sentModList) {
            ModContainer mc = (ModContainer)indexedModList.get(m);
            if (mc == null) {
                missingMods.add(m);
            } else {
                indexedModList.remove(m);
                modVersions.put(m, mc.getVersion());
            }
        }

        if (indexedModList.size() > 0) {
            for(Entry<String, ModContainer> e : indexedModList.entrySet()) {
                if (((ModContainer)e.getValue()).isNetworkMod()) {
                    NetworkModHandler missingHandler = FMLNetworkHandler.instance().findNetworkModHandler(e.getValue());
                    if (missingHandler.requiresServerSide()) {
                        FMLLog.warning(
                                "The mod %s was not found on the server you connected to, but requested that the server side be present", new Object[]{e.getKey()}
                        );
                    }
                }
            }
        }

        FMLLog.fine("The server has compatibility level %d", new Object[]{this.compatibilityLevel});
        FMLCommonHandler.instance().getSidedDelegate().setClientCompatibilityLevel(this.compatibilityLevel);
        mgr.send(PacketDispatcher.getPacket("FML", FMLPacket.makePacket(Type.MOD_LIST_RESPONSE, new Object[]{modVersions, missingMods})));
    }
}
