package net.minecraftforge.classloading;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

public class FMLForgePlugin implements IFMLLoadingPlugin {
    public FMLForgePlugin() {
    }

    public String[] getLibraryRequestClass() {
        return null;
    }

    public String[] getASMTransformerClass() {
        return new String[]{"net.minecraftforge.transformers.ForgeAccessTransformer", "net.minecraftforge.transformers.EventTransformer"};
    }

    public String getModContainerClass() {
        return "net.minecraftforge.common.ForgeDummyContainer";
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
    }
}
