package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.network.NetworkModHandler;

public class ModLoaderNetworkHandler extends NetworkModHandler {
    private BaseModProxy baseMod;

    public ModLoaderNetworkHandler(ModLoaderModContainer mlmc) {
        super(mlmc, null);
    }

    public void setBaseMod(BaseModProxy baseMod) {
        this.baseMod = baseMod;
    }

    public boolean requiresClientSide() {
        return false;
    }

    public boolean requiresServerSide() {
        return false;
    }

    public boolean acceptVersion(String version) {
        return this.baseMod.getVersion().equals(version);
    }

    public boolean isNetworkMod() {
        return true;
    }
}
