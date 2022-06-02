package cpw.mods.fml.common.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLPacket.Type;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;
import java.util.List;
import net.minecraft.network.Connection;
import net.minecraft.network.listener.PacketListener;

public class ModMissingPacket extends FMLPacket {
    private List<ModMissingPacket.ModData> missing;
    private List<ModMissingPacket.ModData> badVersion;

    public ModMissingPacket() {
        super(Type.MOD_MISSING);
    }

    public byte[] generatePacket(Object... data) {
        ByteArrayDataOutput dat = ByteStreams.newDataOutput();
        List<String> missing = (List)data[0];
        List<String> badVersion = (List)data[1];
        dat.writeInt(missing.size());

        for(String missed : missing) {
            ModContainer mc = (ModContainer)Loader.instance().getIndexedModList().get(missed);
            dat.writeUTF(missed);
            dat.writeUTF(mc.getVersion());
        }

        dat.writeInt(badVersion.size());

        for(String bad : badVersion) {
            ModContainer mc = (ModContainer)Loader.instance().getIndexedModList().get(bad);
            dat.writeUTF(bad);
            dat.writeUTF(mc.getVersion());
        }

        return dat.toByteArray();
    }

    public FMLPacket consumePacket(byte[] data) {
        ByteArrayDataInput dat = ByteStreams.newDataInput(data);
        int missingLen = dat.readInt();
        this.missing = Lists.newArrayListWithCapacity(missingLen);

        for(int i = 0; i < missingLen; ++i) {
            ModMissingPacket.ModData md = new ModMissingPacket.ModData();
            md.modId = dat.readUTF();
            md.modVersion = dat.readUTF();
            this.missing.add(md);
        }

        int badVerLength = dat.readInt();
        this.badVersion = Lists.newArrayListWithCapacity(badVerLength);

        for(int i = 0; i < badVerLength; ++i) {
            ModMissingPacket.ModData md = new ModMissingPacket.ModData();
            md.modId = dat.readUTF();
            md.modVersion = dat.readUTF();
            this.badVersion.add(md);
        }

        return this;
    }

    public void execute(Connection network, FMLNetworkHandler handler, PacketListener netHandler, String userName) {
        FMLCommonHandler.instance().getSidedDelegate().displayMissingMods(this);
    }

    public List<ArtifactVersion> getModList() {
        Builder<ArtifactVersion> builder = ImmutableList.builder();

        for(ModMissingPacket.ModData md : this.missing) {
            builder.add(new DefaultArtifactVersion(md.modId, VersionRange.createFromVersion(md.modVersion, null)));
        }

        for(ModMissingPacket.ModData md : this.badVersion) {
            builder.add(new DefaultArtifactVersion(md.modId, VersionRange.createFromVersion(md.modVersion, null)));
        }

        return builder.build();
    }

    private static class ModData {
        String modId;
        String modVersion;

        private ModData() {
        }
    }
}
