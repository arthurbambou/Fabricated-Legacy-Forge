package cpw.mods.fml.common.discovery;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.ModClassLoader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.RelaunchLibraryManager;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModDiscoverer {
    private static Pattern zipJar = Pattern.compile("(.+).(zip|jar)$");
    private List<ModCandidate> candidates = Lists.newArrayList();
    private ASMDataTable dataTable = new ASMDataTable();
    private List<File> nonModLibs = Lists.newArrayList();

    public ModDiscoverer() {
    }

    public void findClasspathMods(ModClassLoader modClassLoader) {
        List<String> knownLibraries = ImmutableList.builder()
                .addAll(modClassLoader.getDefaultLibraries())
                .addAll(RelaunchLibraryManager.getLibraries())
                .build();
        File[] minecraftSources = modClassLoader.getParentSources();
        if (minecraftSources.length == 1 && minecraftSources[0].isFile()) {
            FMLLog.fine("Minecraft is a file at %s, loading", new Object[]{minecraftSources[0].getAbsolutePath()});
            this.candidates.add(new ModCandidate(minecraftSources[0], minecraftSources[0], ContainerType.JAR, true, true));
        } else {
            for(int i = 0; i < minecraftSources.length; ++i) {
                if (minecraftSources[i].isFile()) {
                    if (knownLibraries.contains(minecraftSources[i].getName())) {
                        FMLLog.fine("Skipping known library file %s", new Object[]{minecraftSources[i].getAbsolutePath()});
                    } else {
                        FMLLog.fine("Found a minecraft related file at %s, examining for mod candidates", new Object[]{minecraftSources[i].getAbsolutePath()});
                        this.candidates.add(new ModCandidate(minecraftSources[i], minecraftSources[i], ContainerType.JAR, i == 0, true));
                    }
                } else if (minecraftSources[i].isDirectory()) {
                    FMLLog.fine("Found a minecraft related directory at %s, examining for mod candidates", new Object[]{minecraftSources[i].getAbsolutePath()});
                    this.candidates.add(new ModCandidate(minecraftSources[i], minecraftSources[i], ContainerType.DIR, i == 0, true));
                }
            }
        }

    }

    public void findModDirMods(File modsDir) {
        File[] modList = modsDir.listFiles();
        Arrays.sort(modList);

        for(File modFile : modList) {
            if (modFile.isDirectory()) {
                FMLLog.fine("Found a candidate mod directory %s", new Object[]{modFile.getName()});
                this.candidates.add(new ModCandidate(modFile, modFile, ContainerType.DIR));
            } else {
                Matcher matcher = zipJar.matcher(modFile.getName());
                if (matcher.matches()) {
                    FMLLog.fine("Found a candidate zip or jar file %s", new Object[]{matcher.group(0)});
                    this.candidates.add(new ModCandidate(modFile, modFile, ContainerType.JAR));
                } else {
                    FMLLog.fine("Ignoring unknown file %s in mods directory", new Object[]{modFile.getName()});
                }
            }
        }

    }

    public List<ModContainer> identifyMods() {
        List<ModContainer> modList = Lists.newArrayList();

        for(ModCandidate candidate : this.candidates) {
            try {
                List<ModContainer> mods = candidate.explore(this.dataTable);
                if (mods.isEmpty() && !candidate.isClasspath()) {
                    this.nonModLibs.add(candidate.getModContainer());
                } else {
                    modList.addAll(mods);
                }
            } catch (LoaderException var5) {
                FMLLog.log(
                        Level.WARNING, var5, "Identified a problem with the mod candidate %s, ignoring this source", new Object[]{candidate.getModContainer()}
                );
            } catch (Throwable var6) {
                Throwables.propagate(var6);
            }
        }

        return modList;
    }

    public ASMDataTable getASMTable() {
        return this.dataTable;
    }

    public List<File> getNonModLibs() {
        return this.nonModLibs;
    }
}
