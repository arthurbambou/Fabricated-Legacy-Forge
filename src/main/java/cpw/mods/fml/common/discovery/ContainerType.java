package cpw.mods.fml.common.discovery;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.ModContainer;
import java.util.List;

public enum ContainerType {
    JAR(JarDiscoverer.class),
    DIR(DirectoryDiscoverer.class);

    private ITypeDiscoverer discoverer;

    private ContainerType(Class<? extends ITypeDiscoverer> discovererClass) {
        try {
            this.discoverer = (ITypeDiscoverer)discovererClass.newInstance();
        } catch (Exception var5) {
            throw Throwables.propagate(var5);
        }
    }

    public List<ModContainer> findMods(ModCandidate candidate, ASMDataTable table) {
        return this.discoverer.discover(candidate, table);
    }
}
