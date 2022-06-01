package cpw.mods.fml.relauncher;

public class CoreFMLLibraries implements ILibrarySet {
    private static String[] libraries = new String[]{"argo-2.25.jar", "guava-12.0.1.jar", "asm-all-4.0.jar"};
    private static String[] checksums = new String[]{
            "bb672829fde76cb163004752b86b0484bd0a7f4b", "b8e78b9af7bf45900e14c6f958486b6ca682195f", "98308890597acb64047f7e896638e0d98753ae82"
    };

    public CoreFMLLibraries() {
    }

    public String[] getLibraries() {
        return new String[0];
    }

    public String[] getHashes() {
        return new String[0];
    }

    public String getRootURL() {
        return "http://files.minecraftforge.net/fmllibs/%s";
    }
}
