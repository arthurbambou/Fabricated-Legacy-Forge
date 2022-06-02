package cpw.mods.fml.common.discovery;

import cpw.mods.fml.common.ModContainer;
import java.util.List;
import java.util.regex.Pattern;

public interface ITypeDiscoverer {
    Pattern classFile = Pattern.compile("([^\\s$]+).class$");

    List<ModContainer> discover(ModCandidate modCandidate, ASMDataTable aSMDataTable);
}
