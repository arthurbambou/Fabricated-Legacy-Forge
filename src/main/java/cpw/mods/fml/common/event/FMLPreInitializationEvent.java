package cpw.mods.fml.common.event;

import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.discovery.ASMDataTable;
import java.io.File;
import java.util.Properties;

public class FMLPreInitializationEvent extends FMLStateEvent {
    private ModMetadata modMetadata;
    private File sourceFile;
    private File configurationDir;
    private File suggestedConfigFile;
    private ASMDataTable asmData;
    private ModContainer modContainer;

    public FMLPreInitializationEvent(Object... data) {
        super(data);
        this.asmData = (ASMDataTable)data[0];
        this.configurationDir = (File)data[1];
    }

    public ModState getModState() {
        return ModState.PREINITIALIZED;
    }

    public void applyModContainer(ModContainer activeContainer) {
        this.modContainer = activeContainer;
        this.modMetadata = activeContainer.getMetadata();
        this.sourceFile = activeContainer.getSource();
        this.suggestedConfigFile = new File(this.configurationDir, activeContainer.getModId() + ".cfg");
    }

    public File getSourceFile() {
        return this.sourceFile;
    }

    public ModMetadata getModMetadata() {
        return this.modMetadata;
    }

    public File getModConfigurationDirectory() {
        return this.configurationDir;
    }

    public File getSuggestedConfigurationFile() {
        return this.suggestedConfigFile;
    }

    public ASMDataTable getAsmData() {
        return this.asmData;
    }

    public Properties getVersionProperties() {
        return this.modContainer instanceof FMLModContainer ? ((FMLModContainer)this.modContainer).searchForVersionProperties() : null;
    }
}
