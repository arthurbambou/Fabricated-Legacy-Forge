package cpw.mods.fml.common;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;
import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FMLModContainer implements ModContainer {
    private Mod modDescriptor;
    private Object modInstance;
    private File source;
    private ModMetadata modMetadata;
    private String className;
    private Map<String, Object> descriptor;
    private boolean enabled = true;
    private String internalVersion;
    private boolean overridesMetadata;
    private EventBus eventBus;
    private LoadController controller;
    private Multimap<Class<? extends Annotation>, Object> annotations;
    private DefaultArtifactVersion processedVersion;
    private boolean isNetworkMod;
    private static final BiMap<Class<? extends FMLStateEvent>, Class<? extends Annotation>> modAnnotationTypes = ImmutableBiMap.builder()
            .put(FMLPreInitializationEvent.class, PreInit.class)
            .put(FMLInitializationEvent.class, Init.class)
            .put(FMLPostInitializationEvent.class, PostInit.class)
            .put(FMLServerStartingEvent.class, ServerStarting.class)
            .put(FMLServerStartedEvent.class, ServerStarted.class)
            .put(FMLServerStoppingEvent.class, ServerStopping.class)
            .build();
    private static final BiMap<Class<? extends Annotation>, Class<? extends FMLStateEvent>> modTypeAnnotations = modAnnotationTypes.inverse();
    private String annotationDependencies;
    private VersionRange minecraftAccepted;

    public FMLModContainer(String className, File modSource, Map<String, Object> modDescriptor) {
        this.className = className;
        this.source = modSource;
        this.descriptor = modDescriptor;
    }

    public String getModId() {
        return (String)this.descriptor.get("modid");
    }

    public String getName() {
        return this.modMetadata.name;
    }

    public String getVersion() {
        return this.internalVersion;
    }

    public File getSource() {
        return this.source;
    }

    public ModMetadata getMetadata() {
        return this.modMetadata;
    }

    public void bindMetadata(MetadataCollection mc) {
        this.modMetadata = mc.getMetadataForId(this.getModId(), this.descriptor);
        if (this.descriptor.containsKey("useMetadata")) {
            this.overridesMetadata = !this.descriptor.get("useMetadata");
        }

        if (!this.overridesMetadata && this.modMetadata.useDependencyInformation) {
            FMLLog.finest(
                    "Using mcmod dependency info : %s %s %s",
                    new Object[]{this.modMetadata.requiredMods, this.modMetadata.dependencies, this.modMetadata.dependants}
            );
        } else {
            Set<ArtifactVersion> requirements = Sets.newHashSet();
            List<ArtifactVersion> dependencies = Lists.newArrayList();
            List<ArtifactVersion> dependants = Lists.newArrayList();
            this.annotationDependencies = (String)this.descriptor.get("dependencies");
            Loader.instance().computeDependencies(this.annotationDependencies, requirements, dependencies, dependants);
            this.modMetadata.requiredMods = requirements;
            this.modMetadata.dependencies = dependencies;
            this.modMetadata.dependants = dependants;
            FMLLog.finest("Parsed dependency info : %s %s %s", new Object[]{requirements, dependencies, dependants});
        }

        if (Strings.isNullOrEmpty(this.modMetadata.name)) {
            FMLLog.info("Mod %s is missing the required element 'name'. Substituting %s", new Object[]{this.getModId(), this.getModId()});
            this.modMetadata.name = this.getModId();
        }

        this.internalVersion = (String)this.descriptor.get("version");
        if (Strings.isNullOrEmpty(this.internalVersion)) {
            Properties versionProps = this.searchForVersionProperties();
            if (versionProps != null) {
                this.internalVersion = versionProps.getProperty(this.getModId() + ".version");
                FMLLog.fine("Found version %s for mod %s in version.properties, using", new Object[]{this.internalVersion, this.getModId()});
            }
        }

        if (Strings.isNullOrEmpty(this.internalVersion) && !Strings.isNullOrEmpty(this.modMetadata.version)) {
            FMLLog.warning(
                    "Mod %s is missing the required element 'version' and a version.properties file could not be found. Falling back to metadata version %s",
                    new Object[]{this.getModId(), this.modMetadata.version}
            );
            this.internalVersion = this.modMetadata.version;
        }

        if (Strings.isNullOrEmpty(this.internalVersion)) {
            FMLLog.warning("Mod %s is missing the required element 'version' and no fallback can be found. Substituting '1.0'.", new Object[]{this.getModId()});
            this.modMetadata.version = this.internalVersion = "1.0";
        }

        String mcVersionString = (String)this.descriptor.get("acceptedMinecraftVersions");
        if (!Strings.isNullOrEmpty(mcVersionString)) {
            this.minecraftAccepted = VersionParser.parseRange(mcVersionString);
        } else {
            this.minecraftAccepted = Loader.instance().getMinecraftModContainer().getStaticVersionRange();
        }

    }

    public Properties searchForVersionProperties() {
        try {
            FMLLog.fine(
                    "Attempting to load the file version.properties from %s to locate a version number for %s",
                    new Object[]{this.getSource().getName(), this.getModId()}
            );
            Properties version = null;
            if (this.getSource().isFile()) {
                ZipFile source = new ZipFile(this.getSource());
                ZipEntry versionFile = source.getEntry("version.properties");
                if (versionFile != null) {
                    version = new Properties();
                    version.load(source.getInputStream(versionFile));
                }

                source.close();
            } else if (this.getSource().isDirectory()) {
                File propsFile = new File(this.getSource(), "version.properties");
                if (propsFile.exists() && propsFile.isFile()) {
                    version = new Properties();
                    FileInputStream fis = new FileInputStream(propsFile);
                    version.load(fis);
                    fis.close();
                }
            }

            return version;
        } catch (Exception var4) {
            Throwables.propagateIfPossible(var4);
            FMLLog.fine("Failed to find a usable version.properties file", new Object[0]);
            return null;
        }
    }

    public void setEnabledState(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<ArtifactVersion> getRequirements() {
        return this.modMetadata.requiredMods;
    }

    public List<ArtifactVersion> getDependencies() {
        return this.modMetadata.dependencies;
    }

    public List<ArtifactVersion> getDependants() {
        return this.modMetadata.dependants;
    }

    public String getSortingRules() {
        return !this.overridesMetadata && this.modMetadata.useDependencyInformation
                ? this.modMetadata.printableSortingRules()
                : Strings.nullToEmpty(this.annotationDependencies);
    }

    public boolean matches(Object mod) {
        return mod == this.modInstance;
    }

    public Object getMod() {
        return this.modInstance;
    }

    public boolean registerBus(EventBus bus, LoadController controller) {
        if (this.enabled) {
            FMLLog.fine("Enabling mod %s", new Object[]{this.getModId()});
            this.eventBus = bus;
            this.controller = controller;
            this.eventBus.register(this);
            return true;
        } else {
            return false;
        }
    }

    private Multimap<Class<? extends Annotation>, Object> gatherAnnotations(Class<?> clazz) throws Exception {
        Multimap<Class<? extends Annotation>, Object> anns = ArrayListMultimap.create();

        for(Method m : clazz.getDeclaredMethods()) {
            for(Annotation a : m.getAnnotations()) {
                if (modTypeAnnotations.containsKey(a.annotationType())) {
                    Class<?>[] paramTypes = new Class[]{(Class)modTypeAnnotations.get(a.annotationType())};
                    if (Arrays.equals(m.getParameterTypes(), paramTypes)) {
                        m.setAccessible(true);
                        anns.put(a.annotationType(), m);
                    } else {
                        FMLLog.severe(
                                "The mod %s appears to have an invalid method annotation %s. This annotation can only apply to methods with argument types %s -it will not be called",
                                new Object[]{this.getModId(), a.annotationType().getSimpleName(), Arrays.toString(paramTypes)}
                        );
                    }
                }
            }
        }

        return anns;
    }

    private void processFieldAnnotations(ASMDataTable asmDataTable) throws Exception {
        SetMultimap<String, ASMData> annotations = asmDataTable.getAnnotationsFor(this);
        this.parseSimpleFieldAnnotation(annotations, Instance.class.getName(), new Function<ModContainer, Object>() {
            public Object apply(ModContainer mc) {
                return mc.getMod();
            }
        });
        this.parseSimpleFieldAnnotation(annotations, Metadata.class.getName(), new Function<ModContainer, Object>() {
            public Object apply(ModContainer mc) {
                return mc.getMetadata();
            }
        });
    }

    private void parseSimpleFieldAnnotation(SetMultimap<String, ASMData> annotations, String annotationClassName, Function<ModContainer, Object> retreiver) throws IllegalAccessException {
        String[] annName = annotationClassName.split("\\.");
        String annotationName = annName[annName.length - 1];

        for(ASMData targets : annotations.get(annotationClassName)) {
            String targetMod = (String)targets.getAnnotationInfo().get("value");
            Field f = null;
            Object injectedMod = null;
            ModContainer mc = this;
            boolean isStatic = false;
            Class<?> clz = this.modInstance.getClass();
            if (!Strings.isNullOrEmpty(targetMod)) {
                if (Loader.isModLoaded(targetMod)) {
                    mc = (ModContainer)Loader.instance().getIndexedModList().get(targetMod);
                } else {
                    mc = null;
                }
            }

            if (mc != null) {
                try {
                    clz = Class.forName(targets.getClassName(), true, Loader.instance().getModClassLoader());
                    f = clz.getDeclaredField(targets.getObjectName());
                    f.setAccessible(true);
                    isStatic = Modifier.isStatic(f.getModifiers());
                    injectedMod = retreiver.apply(mc);
                } catch (Exception var15) {
                    Throwables.propagateIfPossible(var15);
                    FMLLog.log(
                            Level.WARNING,
                            var15,
                            "Attempting to load @%s in class %s for %s and failing",
                            new Object[]{annotationName, targets.getClassName(), mc.getModId()}
                    );
                }
            }

            if (f != null) {
                Object target = null;
                if (!isStatic) {
                    target = this.modInstance;
                    if (!this.modInstance.getClass().equals(clz)) {
                        FMLLog.warning(
                                "Unable to inject @%s in non-static field %s.%s for %s as it is NOT the primary mod instance",
                                new Object[]{annotationName, targets.getClassName(), targets.getObjectName(), mc.getModId()}
                        );
                        continue;
                    }
                }

                f.set(target, injectedMod);
            }
        }

    }

    @Subscribe
    public void constructMod(FMLConstructionEvent event) {
        try {
            ModClassLoader modClassLoader = event.getModClassLoader();
            modClassLoader.addFile(this.source);
            Class<?> clazz = Class.forName(this.className, true, modClassLoader);
            ASMDataTable asmHarvestedAnnotations = event.getASMHarvestedData();
            asmHarvestedAnnotations.getAnnotationsFor(this);
            this.annotations = this.gatherAnnotations(clazz);
            this.isNetworkMod = FMLNetworkHandler.instance().registerNetworkMod(this, clazz, event.getASMHarvestedData());
            this.modInstance = clazz.newInstance();
            ProxyInjector.inject(this, event.getASMHarvestedData(), FMLCommonHandler.instance().getSide());
            this.processFieldAnnotations(event.getASMHarvestedData());
        } catch (Throwable var5) {
            this.controller.errorOccurred(this, var5);
            Throwables.propagateIfPossible(var5);
        }

    }

    @Subscribe
    public void handleModStateEvent(FMLStateEvent event) {
        Class<? extends Annotation> annotation = (Class)modAnnotationTypes.get(event.getClass());
        if (annotation != null) {
            try {
                for(Object o : this.annotations.get(annotation)) {
                    Method m = (Method)o;
                    m.invoke(this.modInstance, event);
                }
            } catch (Throwable var6) {
                this.controller.errorOccurred(this, var6);
                Throwables.propagateIfPossible(var6);
            }

        }
    }

    public ArtifactVersion getProcessedVersion() {
        if (this.processedVersion == null) {
            this.processedVersion = new DefaultArtifactVersion(this.getModId(), this.getVersion());
        }

        return this.processedVersion;
    }

    public boolean isImmutable() {
        return false;
    }

    public boolean isNetworkMod() {
        return this.isNetworkMod;
    }

    public String getDisplayVersion() {
        return this.modMetadata.version;
    }

    public VersionRange acceptableMinecraftVersionRange() {
        return this.minecraftAccepted;
    }
}
