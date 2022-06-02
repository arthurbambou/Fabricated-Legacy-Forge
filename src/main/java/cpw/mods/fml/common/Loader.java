package cpw.mods.fml.common;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multisets;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.google.common.collect.Multiset.Entry;
import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import cpw.mods.fml.common.event.FMLLoadEvent;
import cpw.mods.fml.common.functions.ModIdFunction;
import cpw.mods.fml.common.toposort.ModSorter;
import cpw.mods.fml.common.toposort.ModSortingException;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.util.crash.provider.MinecraftVersionProvider;

public class Loader {
    private static final Splitter DEPENDENCYPARTSPLITTER = Splitter.on(":").omitEmptyStrings().trimResults();
    private static final Splitter DEPENDENCYSPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();
    private static Loader instance;
    private static String major;
    private static String minor;
    private static String rev;
    private static String build;
    private static String mccversion;
    private static String mcsversion;
    private ModClassLoader modClassLoader = new ModClassLoader(this.getClass().getClassLoader());
    private List<ModContainer> mods;
    private Map<String, ModContainer> namedMods;
    private File canonicalConfigDir;
    private File canonicalMinecraftDir;
    private Exception capturedError;
    private File canonicalModsDir;
    private LoadController modController;
    private MinecraftDummyContainer minecraft;
    private static File minecraftDir;
    private static List<String> injectedContainers;

    public static Loader instance() {
        if (instance == null) {
            instance = new Loader();
        }

        return instance;
    }

    public static void injectData(Object... data) {
        major = (String)data[0];
        minor = (String)data[1];
        rev = (String)data[2];
        build = (String)data[3];
        mccversion = (String)data[4];
        mcsversion = (String)data[5];
        minecraftDir = (File)data[6];
        injectedContainers = (List)data[7];
    }

    private Loader() {
        String actualMCVersion = new MinecraftVersionProvider(null).call();
        if (!mccversion.equals(actualMCVersion)) {
            FMLLog.severe(
                    "This version of FML is built for Minecraft %s, we have detected Minecraft %s in your minecraft jar file",
                    new Object[]{mccversion, actualMCVersion}
            );
            throw new LoaderException();
        } else {
            this.minecraft = new MinecraftDummyContainer(actualMCVersion);
        }
    }

    private void sortModList() {
        FMLLog.fine("Verifying mod requirements are satisfied", new Object[0]);

        try {
            BiMap<String, ArtifactVersion> modVersions = HashBiMap.create();

            for(ModContainer mod : this.getActiveModList()) {
                modVersions.put(mod.getModId(), mod.getProcessedVersion());
            }

            for(ModContainer mod : this.getActiveModList()) {
                if (!mod.acceptableMinecraftVersionRange().containsVersion(this.minecraft.getProcessedVersion())) {
                    FMLLog.severe(
                            "The mod %s does not wish to run in Minecraft version %s. You will have to remove it to play.",
                            new Object[]{mod.getModId(), this.getMCVersionString()}
                    );
                    throw new WrongMinecraftVersionException(mod);
                }

                Map<String, ArtifactVersion> names = Maps.uniqueIndex(mod.getRequirements(), new Function<ArtifactVersion, String>() {
                    public String apply(ArtifactVersion v) {
                        return v.getLabel();
                    }
                });
                Set<ArtifactVersion> versionMissingMods = Sets.newHashSet();
                Set<String> missingMods = Sets.difference(names.keySet(), modVersions.keySet());
                if (!missingMods.isEmpty()) {
                    FMLLog.severe("The mod %s (%s) requires mods %s to be available", new Object[]{mod.getModId(), mod.getName(), missingMods});

                    for(String modid : missingMods) {
                        versionMissingMods.add(names.get(modid));
                    }

                    throw new MissingModsException(versionMissingMods);
                }

                for(ArtifactVersion v : ImmutableList.builder().addAll(mod.getDependants()).addAll(mod.getDependencies()).build()) {
                    if (modVersions.containsKey(v.getLabel()) && !v.containsVersion((ArtifactVersion)modVersions.get(v.getLabel()))) {
                        versionMissingMods.add(v);
                    }
                }

                if (!versionMissingMods.isEmpty()) {
                    FMLLog.severe("The mod %s (%s) requires mod versions %s to be available", new Object[]{mod.getModId(), mod.getName(), versionMissingMods});
                    throw new MissingModsException(versionMissingMods);
                }
            }

            FMLLog.fine("All mod requirements are satisfied", new Object[0]);
            ModSorter sorter = new ModSorter(this.getActiveModList(), this.namedMods);

            try {
                FMLLog.fine("Sorting mods into an ordered list", new Object[0]);
                List<ModContainer> sortedMods = sorter.sort();
                this.modController.getActiveModList().clear();
                this.modController.getActiveModList().addAll(sortedMods);
                this.mods.removeAll(sortedMods);
                sortedMods.addAll(this.mods);
                this.mods = sortedMods;
                FMLLog.fine("Mod sorting completed successfully", new Object[0]);
            } catch (ModSortingException var15) {
                FMLLog.severe("A dependency cycle was detected in the input mod set so an ordering cannot be determined", new Object[0]);
                FMLLog.severe("The visited mod list is %s", new Object[]{var15.getExceptionData().getVisitedNodes()});
                FMLLog.severe("The first mod in the cycle is %s", new Object[]{var15.getExceptionData().getFirstBadNode()});
                FMLLog.log(Level.SEVERE, var15, "The full error", new Object[0]);
                throw new LoaderException(var15);
            }
        } finally {
            FMLLog.fine("Mod sorting data:", new Object[0]);

            for(ModContainer mod : this.getActiveModList()) {
                if (!mod.isImmutable()) {
                    FMLLog.fine(
                            "\t%s(%s:%s): %s (%s)", new Object[]{mod.getModId(), mod.getName(), mod.getVersion(), mod.getSource().getName(), mod.getSortingRules()}
                    );
                }
            }

            if (this.mods.size() == 0) {
                FMLLog.fine("No mods found to sort", new Object[0]);
            }

        }

    }

    private ModDiscoverer identifyMods() {
        FMLLog.fine("Building injected Mod Containers %s", new Object[]{injectedContainers});
        File coremod = new File(minecraftDir, "coremods");

        for(String cont : injectedContainers) {
            ModContainer mc;
            try {
                mc = (ModContainer)Class.forName(cont, true, this.modClassLoader).newInstance();
            } catch (Exception var6) {
                FMLLog.log(Level.SEVERE, var6, "A problem occured instantiating the injected mod container %s", new Object[]{cont});
                throw new LoaderException(var6);
            }

            this.mods.add(new InjectedModContainer(mc, coremod));
        }

        ModDiscoverer discoverer = new ModDiscoverer();
        FMLLog.fine("Attempting to load mods contained in the minecraft jar file and associated classes", new Object[0]);
        discoverer.findClasspathMods(this.modClassLoader);
        FMLLog.fine("Minecraft jar mods loaded successfully", new Object[0]);
        FMLLog.info("Searching %s for mods", new Object[]{this.canonicalModsDir.getAbsolutePath()});
        discoverer.findModDirMods(this.canonicalModsDir);
        this.mods.addAll(discoverer.identifyMods());
        this.identifyDuplicates(this.mods);
        this.namedMods = Maps.uniqueIndex(this.mods, new ModIdFunction());
        FMLLog.info("Forge Mod Loader has identified %d mod%s to load", new Object[]{this.mods.size(), this.mods.size() != 1 ? "s" : ""});
        return discoverer;
    }

    private void identifyDuplicates(List<ModContainer> mods) {
        boolean foundDupe = false;
        TreeMultimap<ModContainer, File> dupsearch = TreeMultimap.create(new Loader.ModIdComparator(), Ordering.arbitrary());

        for(ModContainer mc : mods) {
            if (mc.getSource() != null) {
                dupsearch.put(mc, mc.getSource());
            }
        }

        ImmutableMultiset<ModContainer> duplist = Multisets.copyHighestCountFirst(dupsearch.keys());

        for(Entry<ModContainer> e : duplist.entrySet()) {
            if (e.getCount() > 1) {
                FMLLog.severe("Found a duplicate mod %s at %s", new Object[]{((ModContainer)e.getElement()).getModId(), dupsearch.get(e.getElement())});
                foundDupe = true;
            }
        }

        if (foundDupe) {
            throw new LoaderException();
        }
    }

    private void initializeLoader() {
        File modsDir = new File(minecraftDir, "mods");
        File configDir = new File(minecraftDir, "config");

        String canonicalModsPath;
        String canonicalConfigPath;
        try {
            this.canonicalMinecraftDir = minecraftDir.getCanonicalFile();
            canonicalModsPath = modsDir.getCanonicalPath();
            canonicalConfigPath = configDir.getCanonicalPath();
            this.canonicalConfigDir = configDir.getCanonicalFile();
            this.canonicalModsDir = modsDir.getCanonicalFile();
        } catch (IOException var6) {
            FMLLog.log(
                    Level.SEVERE,
                    var6,
                    "Failed to resolve loader directories: mods : %s ; config %s",
                    new Object[]{this.canonicalModsDir.getAbsolutePath(), configDir.getAbsolutePath()}
            );
            throw new LoaderException(var6);
        }

        if (!this.canonicalModsDir.exists()) {
            FMLLog.info("No mod directory found, creating one: %s", new Object[]{canonicalModsPath});
            boolean dirMade = this.canonicalModsDir.mkdir();
            if (!dirMade) {
                FMLLog.severe("Unable to create the mod directory %s", new Object[]{canonicalModsPath});
                throw new LoaderException();
            }

            FMLLog.info("Mod directory created successfully", new Object[0]);
        }

        if (!this.canonicalConfigDir.exists()) {
            FMLLog.fine("No config directory found, creating one: %s", new Object[]{canonicalConfigPath});
            boolean dirMade = this.canonicalConfigDir.mkdir();
            if (!dirMade) {
                FMLLog.severe("Unable to create the config directory %s", new Object[]{canonicalConfigPath});
                throw new LoaderException();
            }

            FMLLog.info("Config directory created successfully", new Object[0]);
        }

        if (!this.canonicalModsDir.isDirectory()) {
            FMLLog.severe("Attempting to load mods from %s, which is not a directory", new Object[]{canonicalModsPath});
            throw new LoaderException();
        } else if (!configDir.isDirectory()) {
            FMLLog.severe("Attempting to load configuration from %s, which is not a directory", new Object[]{canonicalConfigPath});
            throw new LoaderException();
        }
    }

    public List<ModContainer> getModList() {
        return instance().mods != null ? ImmutableList.copyOf(instance().mods) : ImmutableList.of();
    }

    public void loadMods() {
        this.initializeLoader();
        this.mods = Lists.newArrayList();
        this.namedMods = Maps.newHashMap();
        this.modController = new LoadController(this);
        this.modController.transition(LoaderState.LOADING);
        ModDiscoverer disc = this.identifyMods();
        this.disableRequestedMods();
        this.modController.distributeStateMessage(FMLLoadEvent.class);
        this.sortModList();
        this.mods = ImmutableList.copyOf(this.mods);

        for(File nonMod : disc.getNonModLibs()) {
            if (nonMod.isFile()) {
                FMLLog.severe(
                        "FML has found a non-mod file %s in your mods directory. It will now be injected into your classpath. This could severe stability issues, it should be removed if possible.",
                        new Object[]{nonMod.getName()}
                );

                try {
                    this.modClassLoader.addFile(nonMod);
                } catch (MalformedURLException var5) {
                    FMLLog.log(Level.SEVERE, var5, "Encountered a weird problem with non-mod file injection : %s", new Object[]{nonMod.getName()});
                }
            }
        }

        this.modController.transition(LoaderState.CONSTRUCTING);
        this.modController.distributeStateMessage(LoaderState.CONSTRUCTING, new Object[]{this.modClassLoader, disc.getASMTable()});
        this.modController.transition(LoaderState.PREINITIALIZATION);
        this.modController.distributeStateMessage(LoaderState.PREINITIALIZATION, new Object[]{disc.getASMTable(), this.canonicalConfigDir});
        this.modController.transition(LoaderState.INITIALIZATION);
    }

    private void disableRequestedMods() {
        String forcedModList = System.getProperty("fml.modStates", "");
        FMLLog.fine("Received a system property request '%s'", new Object[]{forcedModList});
        Map<String, String> sysPropertyStateList = Splitter.on(CharMatcher.anyOf(";:"))
                .omitEmptyStrings()
                .trimResults()
                .withKeyValueSeparator("=")
                .split(forcedModList);
        FMLLog.fine("System property request managing the state of %d mods", new Object[]{sysPropertyStateList.size()});
        Map<String, String> modStates = Maps.newHashMap();
        File forcedModFile = new File(this.canonicalConfigDir, "fmlModState.properties");
        Properties forcedModListProperties = new Properties();
        if (forcedModFile.exists() && forcedModFile.isFile()) {
            FMLLog.fine("Found a mod state file %s", new Object[]{forcedModFile.getName()});

            try {
                forcedModListProperties.load(new FileReader(forcedModFile));
                FMLLog.fine("Loaded states for %d mods from file", new Object[]{forcedModListProperties.size()});
            } catch (Exception var9) {
                FMLLog.log(Level.INFO, var9, "An error occurred reading the fmlModState.properties file", new Object[0]);
            }
        }

        modStates.putAll(Maps.fromProperties(forcedModListProperties));
        modStates.putAll(sysPropertyStateList);
        FMLLog.fine("After merging, found state information for %d mods", new Object[]{modStates.size()});
        Map<String, Boolean> isEnabled = Maps.transformValues(modStates, new Function<String, Boolean>() {
            public Boolean apply(String input) {
                return Boolean.parseBoolean(input);
            }
        });

        for(java.util.Map.Entry<String, Boolean> entry : isEnabled.entrySet()) {
            if (this.namedMods.containsKey(entry.getKey())) {
                FMLLog.info("Setting mod %s to enabled state %b", new Object[]{entry.getKey(), entry.getValue()});
                ((ModContainer)this.namedMods.get(entry.getKey())).setEnabledState(entry.getValue());
            }
        }

    }

    public static boolean isModLoaded(String modname) {
        return instance().namedMods.containsKey(modname)
                && instance().modController.getModState((ModContainer)instance.namedMods.get(modname)) != ModState.DISABLED;
    }

    public File getConfigDir() {
        return this.canonicalConfigDir;
    }

    public String getCrashInformation() {
        StringBuilder ret = new StringBuilder();
        List<String> branding = FMLCommonHandler.instance().getBrandings();
        Joiner.on(' ').skipNulls().appendTo(ret, branding.subList(1, branding.size()));
        if (this.modController != null) {
            this.modController.printModStates(ret);
        }

        return ret.toString();
    }

    public String getFMLVersionString() {
        return String.format("%s.%s.%s.%s", major, minor, rev, build);
    }

    public ClassLoader getModClassLoader() {
        return this.modClassLoader;
    }

    public void computeDependencies(
            String dependencyString, Set<ArtifactVersion> requirements, List<ArtifactVersion> dependencies, List<ArtifactVersion> dependants
    ) {
        if (dependencyString != null && dependencyString.length() != 0) {
            boolean parseFailure = false;

            for(String dep : DEPENDENCYSPLITTER.split(dependencyString)) {
                List<String> depparts = Lists.newArrayList(DEPENDENCYPARTSPLITTER.split(dep));
                if (depparts.size() != 2) {
                    parseFailure = true;
                } else {
                    String instruction = (String)depparts.get(0);
                    String target = (String)depparts.get(1);
                    boolean targetIsAll = target.startsWith("*");
                    if (targetIsAll && target.length() > 1) {
                        parseFailure = true;
                    } else {
                        if ("required-before".equals(instruction) || "required-after".equals(instruction)) {
                            if (targetIsAll) {
                                parseFailure = true;
                                continue;
                            }

                            requirements.add(VersionParser.parseVersionReference(target));
                        }

                        if (targetIsAll && target.indexOf(64) > -1) {
                            parseFailure = true;
                        } else if ("required-before".equals(instruction) || "before".equals(instruction)) {
                            dependants.add(VersionParser.parseVersionReference(target));
                        } else if (!"required-after".equals(instruction) && !"after".equals(instruction)) {
                            parseFailure = true;
                        } else {
                            dependencies.add(VersionParser.parseVersionReference(target));
                        }
                    }
                }
            }

            if (parseFailure) {
                FMLLog.log(Level.WARNING, "Unable to parse dependency string %s", new Object[]{dependencyString});
                throw new LoaderException();
            }
        }
    }

    public Map<String, ModContainer> getIndexedModList() {
        return ImmutableMap.copyOf(this.namedMods);
    }

    public void initializeMods() {
        this.modController.distributeStateMessage(LoaderState.INITIALIZATION, new Object[0]);
        this.modController.transition(LoaderState.POSTINITIALIZATION);
        this.modController.distributeStateMessage(LoaderState.POSTINITIALIZATION, new Object[0]);
        this.modController.transition(LoaderState.AVAILABLE);
        this.modController.distributeStateMessage(LoaderState.AVAILABLE, new Object[0]);
        FMLLog.info("Forge Mod Loader has successfully loaded %d mod%s", new Object[]{this.mods.size(), this.mods.size() == 1 ? "" : "s"});
    }

    public ICrashCallable getCallableCrashInformation() {
        return new ICrashCallable() {
            public String call() throws Exception {
                return Loader.this.getCrashInformation();
            }

            public String getLabel() {
                return "FML";
            }
        };
    }

    public List<ModContainer> getActiveModList() {
        return (List<ModContainer>)(this.modController != null ? this.modController.getActiveModList() : ImmutableList.of());
    }

    public ModState getModState(ModContainer selectedMod) {
        return this.modController.getModState(selectedMod);
    }

    public String getMCVersionString() {
        return "Minecraft " + mccversion;
    }

    public void serverStarting(Object server) {
        this.modController.distributeStateMessage(LoaderState.SERVER_STARTING, new Object[]{server});
        this.modController.transition(LoaderState.SERVER_STARTING);
    }

    public void serverStarted() {
        this.modController.distributeStateMessage(LoaderState.SERVER_STARTED, new Object[0]);
        this.modController.transition(LoaderState.SERVER_STARTED);
    }

    public void serverStopping() {
        this.modController.distributeStateMessage(LoaderState.SERVER_STOPPING, new Object[0]);
        this.modController.transition(LoaderState.SERVER_STOPPING);
        this.modController.transition(LoaderState.AVAILABLE);
    }

    public BiMap<ModContainer, Object> getModObjectList() {
        return this.modController.getModObjectList();
    }

    public BiMap<Object, ModContainer> getReversedModObjectList() {
        return this.getModObjectList().inverse();
    }

    public ModContainer activeModContainer() {
        return this.modController.activeContainer();
    }

    public boolean isInState(LoaderState state) {
        return this.modController.isInState(state);
    }

    public MinecraftDummyContainer getMinecraftModContainer() {
        return this.minecraft;
    }

    private class ModIdComparator implements Comparator<ModContainer> {
        private ModIdComparator() {
        }

        public int compare(ModContainer o1, ModContainer o2) {
            return o1.getModId().compareTo(o2.getModId());
        }
    }
}
