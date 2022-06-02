package cpw.mods.fml.common.discovery;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.asm.ASMModParser;
import java.io.File;
import java.util.List;

public class ModCandidate {
    private File classPathRoot;
    private File modContainer;
    private ContainerType sourceType;
    private boolean classpath;
    private List<String> baseModTypes = Lists.newArrayList();
    private boolean isMinecraft;
    private List<ASMModParser> baseModCandidateTypes = Lists.newArrayListWithCapacity(1);

    public ModCandidate(File classPathRoot, File modContainer, ContainerType sourceType) {
        this(classPathRoot, modContainer, sourceType, false, false);
    }

    public ModCandidate(File classPathRoot, File modContainer, ContainerType sourceType, boolean isMinecraft, boolean classpath) {
        this.classPathRoot = classPathRoot;
        this.modContainer = modContainer;
        this.sourceType = sourceType;
        this.isMinecraft = isMinecraft;
        this.classpath = classpath;
    }

    public File getClassPathRoot() {
        return this.classPathRoot;
    }

    public File getModContainer() {
        return this.modContainer;
    }

    public ContainerType getSourceType() {
        return this.sourceType;
    }

    public List<ModContainer> explore(ASMDataTable table) {
        List<ModContainer> mods = this.sourceType.findMods(this, table);
        if (!this.baseModCandidateTypes.isEmpty()) {
            FMLLog.info("Attempting to reparse the mod container %s", new Object[]{this.getModContainer().getName()});
            return this.sourceType.findMods(this, table);
        } else {
            return mods;
        }
    }

    public boolean isClasspath() {
        return this.classpath;
    }

    public void rememberBaseModType(String className) {
        this.baseModTypes.add(className);
    }

    public List<String> getRememberedBaseMods() {
        return this.baseModTypes;
    }

    public boolean isMinecraftJar() {
        return this.isMinecraft;
    }

    public void rememberModCandidateType(ASMModParser modParser) {
        this.baseModCandidateTypes.add(modParser);
    }
}
