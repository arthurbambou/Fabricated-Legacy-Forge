package cpw.mods.fml.relauncher;

import java.util.Map;

public class FMLCorePlugin implements IFMLLoadingPlugin {
    public FMLCorePlugin() {
    }

    public String[] getLibraryRequestClass() {
        return new String[]{"cpw.mods.fml.relauncher.CoreFMLLibraries"};
    }

    public String[] getASMTransformerClass() {
        return new String[]{
                "cpw.mods.fml.common.asm.transformers.AccessTransformer",
                "cpw.mods.fml.common.asm.transformers.MarkerTransformer",
                "cpw.mods.fml.common.asm.transformers.SideTransformer"
        };
    }

    public String getModContainerClass() {
        return "cpw.mods.fml.common.FMLDummyContainer";
    }

    public String getSetupClass() {
        return "cpw.mods.fml.common.asm.FMLSanityChecker";
    }

    public void injectData(Map<String, Object> data) {
    }
}
