package cpw.mods.fml.common;

import com.google.common.eventbus.EventBus;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;

public class FMLDummyContainer extends DummyModContainer implements WorldAccessContainer {
    public FMLDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "FML";
        meta.name = "Forge Mod Loader";
        meta.version = Loader.instance().getFMLVersionString();
        meta.credits = "Made possible with help from many people";
        meta.authorList = Arrays.asList("cpw, LexManos");
        meta.description = "The Forge Mod Loader provides the ability for systems to load mods from the file system. It also provides key capabilities for mods to be able to cooperate and provide a good modding environment. The mod loading system is compatible with ModLoader, all your ModLoader mods should work.";
        meta.url = "https://github.com/cpw/FML/wiki";
        meta.updateUrl = "https://github.com/cpw/FML/wiki";
        meta.screenshots = new String[0];
        meta.logoFile = "";
    }

    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }

    public NbtCompound getDataForWriting(WorldSaveHandler handler, LevelProperties info) {
        NbtCompound fmlData = new NbtCompound();
        NbtList list = new NbtList();

        for(ModContainer mc : Loader.instance().getActiveModList()) {
            NbtCompound mod = new NbtCompound();
            mod.putString("ModId", mc.getModId());
            mod.putString("ModVersion", mc.getVersion());
            list.method_1217(mod);
        }

        fmlData.put("ModList", list);
        return fmlData;
    }

    public void readData(WorldSaveHandler handler, LevelProperties info, Map<String, NbtElement> propertyMap, NbtCompound tag) {
        if (tag.contains("ModList")) {
            NbtList modList = tag.getList("ModList");

            for(int i = 0; i < modList.size(); ++i) {
                NbtCompound mod = (NbtCompound)modList.method_1218(i);
                String modId = mod.getString("ModId");
                String modVersion = mod.getString("ModVersion");
                ModContainer container = (ModContainer)Loader.instance().getIndexedModList().get(modId);
                if (container == null) {
                    FMLLog.severe("This world was saved with mod %s which appears to be missing, things may not work well", new Object[]{modId});
                } else if (!modVersion.equals(container.getVersion())) {
                    FMLLog.info(
                            "This world was saved with mod %s version %s and it is now at version %s, things may not work well",
                            new Object[]{modId, modVersion, container.getVersion()}
                    );
                }
            }
        }

    }
}
