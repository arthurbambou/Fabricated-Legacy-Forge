package cpw.mods.fml.common;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;
import java.io.File;
import java.util.List;
import java.util.Set;

public interface ModContainer {
    String getModId();

    String getName();

    String getVersion();

    File getSource();

    ModMetadata getMetadata();

    void bindMetadata(MetadataCollection metadataCollection);

    void setEnabledState(boolean bl);

    Set<ArtifactVersion> getRequirements();

    List<ArtifactVersion> getDependencies();

    List<ArtifactVersion> getDependants();

    String getSortingRules();

    boolean registerBus(EventBus eventBus, LoadController loadController);

    boolean matches(Object object);

    Object getMod();

    ArtifactVersion getProcessedVersion();

    boolean isImmutable();

    boolean isNetworkMod();

    String getDisplayVersion();

    VersionRange acceptableMinecraftVersionRange();
}
