package cpw.mods.fml.relauncher;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class RelaunchLibraryManager {
    private static String[] rootPlugins = new String[]{"cpw.mods.fml.relauncher.FMLCorePlugin", "net.minecraftforge.classloading.FMLForgePlugin"};
    private static List<String> loadedLibraries = new ArrayList();
    private static Map<IFMLLoadingPlugin, File> pluginLocations;
    private static List<IFMLLoadingPlugin> loadPlugins;
    private static List<ILibrarySet> libraries;
    private static final String HEXES = "0123456789abcdef";
    private static ByteBuffer downloadBuffer = ByteBuffer.allocateDirect(4194304);
    static IDownloadDisplay downloadMonitor;

    public RelaunchLibraryManager() {
    }

    // $FF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
    // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
    public static void handleLaunch(File mcDir, RelaunchClassLoader actualClassLoader) {
        pluginLocations = new HashMap();
        loadPlugins = new ArrayList();
        libraries = new ArrayList();

        for(String s : rootPlugins) {
            try {
                IFMLLoadingPlugin plugin = (IFMLLoadingPlugin)Class.forName(s, true, actualClassLoader).newInstance();
                loadPlugins.add(plugin);

                for(String libName : plugin.getLibraryRequestClass()) {
                    libraries.add((ILibrarySet)Class.forName(libName, true, actualClassLoader).newInstance());
                }
            } catch (Exception var42) {
            }
        }

        if (loadPlugins.isEmpty()) {
            throw new RuntimeException("A fatal error has occured - no valid fml load plugin was found - this is a completely corrupt FML installation.");
        } else {
            downloadMonitor.updateProgressString("All core mods are successfully located", new Object[0]);
            String commandLineCoremods = System.getProperty("fml.coreMods.load", "");

            for(String s : commandLineCoremods.split(",")) {
                if (!s.isEmpty()) {
                    FMLRelaunchLog.info("Found a command line coremod : %s", new Object[]{s});

                    try {
                        actualClassLoader.addTransformerExclusion(s);
                        Class<?> coreModClass = Class.forName(s, true, actualClassLoader);
                        TransformerExclusions trExclusions = (TransformerExclusions)coreModClass.getAnnotation(TransformerExclusions.class);
                        if (trExclusions != null) {
                            for(String st : trExclusions.value()) {
                                actualClassLoader.addTransformerExclusion(st);
                            }
                        }

                        IFMLLoadingPlugin plugin = (IFMLLoadingPlugin)coreModClass.newInstance();
                        loadPlugins.add(plugin);
                        if (plugin.getLibraryRequestClass() != null) {
                            for(String libName : plugin.getLibraryRequestClass()) {
                                libraries.add((ILibrarySet)Class.forName(libName, true, actualClassLoader).newInstance());
                            }
                        }
                    } catch (Throwable var41) {
                        FMLRelaunchLog.log(Level.SEVERE, var41, "Exception occured trying to load coremod %s", new Object[]{s});
                        throw new RuntimeException(var41);
                    }
                }
            }

            discoverCoreMods(mcDir, actualClassLoader, loadPlugins, libraries);
            List<Throwable> caughtErrors = new ArrayList();
            boolean var33 = false;

            label672: {
                try {
                    label673: {
                        File var47;
                        try {
                            var33 = true;
                            var47 = setupLibDir(mcDir);
                        } catch (Exception var39) {
                            caughtErrors.add(var39);
                            var33 = false;
                            break label673;
                        }

                        for(ILibrarySet lib : libraries) {
                            for(int i = 0; i < lib.getLibraries().length; ++i) {
                                boolean download = false;
                                String libName = lib.getLibraries()[i];
                                String targFileName = libName.lastIndexOf(47) >= 0 ? libName.substring(libName.lastIndexOf(47)) : libName;
                                String checksum = lib.getHashes()[i];
                                File libFile = new File(var47, targFileName);
                                if (!libFile.exists()) {
                                    try {
                                        downloadFile(libFile, lib.getRootURL(), libName, checksum);
                                        download = true;
                                    } catch (Throwable var38) {
                                        caughtErrors.add(var38);
                                        continue;
                                    }
                                }

                                if (libFile.exists() && !libFile.isFile()) {
                                    caughtErrors.add(
                                            new RuntimeException(
                                                    String.format("Found a file %s that is not a normal file - you should clear this out of the way", libName)
                                            )
                                    );
                                } else {
                                    if (!download) {
                                        try {
                                            FileInputStream fis = new FileInputStream(libFile);
                                            FileChannel chan = fis.getChannel();
                                            MappedByteBuffer mappedFile = chan.map(MapMode.READ_ONLY, 0L, libFile.length());
                                            String fileChecksum = generateChecksum(mappedFile);
                                            fis.close();
                                            if (!checksum.equals(fileChecksum)) {
                                                caughtErrors.add(
                                                        new RuntimeException(
                                                                String.format(
                                                                        "The file %s was found in your lib directory and has an invalid checksum %s (expecting %s) - it is unlikely to be the correct download, please move it out of the way and try again.",
                                                                        libName,
                                                                        fileChecksum,
                                                                        checksum
                                                                )
                                                        )
                                                );
                                                continue;
                                            }
                                        } catch (Exception var37) {
                                            FMLRelaunchLog.log(
                                                    Level.SEVERE, var37, "The library file %s could not be validated", new Object[]{libFile.getName()}
                                            );
                                            caughtErrors.add(
                                                    new RuntimeException(String.format("The library file %s could not be validated", libFile.getName()), var37)
                                            );
                                            continue;
                                        }
                                    }

                                    if (!download) {
                                        downloadMonitor.updateProgressString("Found library file %s present and correct in lib dir\n", new Object[]{libName});
                                    } else {
                                        downloadMonitor.updateProgressString(
                                                "Library file %s was downloaded and verified successfully\n", new Object[]{libName}
                                        );
                                    }

                                    try {
                                        actualClassLoader.addURL(libFile.toURI().toURL());
                                        loadedLibraries.add(libName);
                                    } catch (MalformedURLException var36) {
                                        caughtErrors.add(
                                                new RuntimeException(
                                                        String.format(
                                                                "Should never happen - %s is broken - probably a somehow corrupted download. Delete it and try again.",
                                                                libFile.getName()
                                                        ),
                                                        var36
                                                )
                                        );
                                    }
                                }
                            }
                        }

                        var33 = false;
                        break label672;
                    }
                } finally {
                    if (var33) {
                        if (downloadMonitor.shouldStopIt()) {
                            return;
                        }

                        if (!caughtErrors.isEmpty()) {
                            FMLRelaunchLog.severe(
                                    "There were errors during initial FML setup. Some files failed to download or were otherwise corrupted. You will need to manually obtain the following files from these download links and ensure your lib directory is clean. ",
                                    new Object[0]
                            );

                            for(ILibrarySet set : libraries) {
                                for(String file : set.getLibraries()) {
                                    FMLRelaunchLog.severe("*** Download " + set.getRootURL(), new Object[]{file});
                                }
                            }

                            FMLRelaunchLog.severe("<===========>", new Object[0]);
                            FMLRelaunchLog.severe(
                                    "The following is the errors that caused the setup to fail. They may help you diagnose and resolve the issue", new Object[0]
                            );

                            for(Throwable t : caughtErrors) {
                                if (t.getMessage() != null) {
                                    FMLRelaunchLog.severe(t.getMessage(), new Object[0]);
                                }
                            }

                            FMLRelaunchLog.severe("<<< ==== >>>", new Object[0]);
                            FMLRelaunchLog.severe("The following is diagnostic information for developers to review.", new Object[0]);

                            for(Throwable t : caughtErrors) {
                                FMLRelaunchLog.log(Level.SEVERE, t, "Error details", new Object[0]);
                            }

                            throw new RuntimeException("A fatal error occured and FML cannot continue");
                        }

                    }
                }

                if (downloadMonitor.shouldStopIt()) {
                    return;
                }

                if (caughtErrors.isEmpty()) {
                    return;
                }

                FMLRelaunchLog.severe(
                        "There were errors during initial FML setup. Some files failed to download or were otherwise corrupted. You will need to manually obtain the following files from these download links and ensure your lib directory is clean. ",
                        new Object[0]
                );

                for(ILibrarySet set : libraries) {
                    for(String file : set.getLibraries()) {
                        FMLRelaunchLog.severe("*** Download " + set.getRootURL(), new Object[]{file});
                    }
                }

                FMLRelaunchLog.severe("<===========>", new Object[0]);
                FMLRelaunchLog.severe(
                        "The following is the errors that caused the setup to fail. They may help you diagnose and resolve the issue", new Object[0]
                );

                for(Throwable t : caughtErrors) {
                    if (t.getMessage() != null) {
                        FMLRelaunchLog.severe(t.getMessage(), new Object[0]);
                    }
                }

                FMLRelaunchLog.severe("<<< ==== >>>", new Object[0]);
                FMLRelaunchLog.severe("The following is diagnostic information for developers to review.", new Object[0]);

                for(Throwable t : caughtErrors) {
                    FMLRelaunchLog.log(Level.SEVERE, t, "Error details", new Object[0]);
                }

                throw new RuntimeException("A fatal error occured and FML cannot continue");
            }

            if (!downloadMonitor.shouldStopIt()) {
                if (!caughtErrors.isEmpty()) {
                    FMLRelaunchLog.severe(
                            "There were errors during initial FML setup. Some files failed to download or were otherwise corrupted. You will need to manually obtain the following files from these download links and ensure your lib directory is clean. ",
                            new Object[0]
                    );

                    for(ILibrarySet set : libraries) {
                        for(String file : set.getLibraries()) {
                            FMLRelaunchLog.severe("*** Download " + set.getRootURL(), new Object[]{file});
                        }
                    }

                    FMLRelaunchLog.severe("<===========>", new Object[0]);
                    FMLRelaunchLog.severe(
                            "The following is the errors that caused the setup to fail. They may help you diagnose and resolve the issue", new Object[0]
                    );

                    for(Throwable t : caughtErrors) {
                        if (t.getMessage() != null) {
                            FMLRelaunchLog.severe(t.getMessage(), new Object[0]);
                        }
                    }

                    FMLRelaunchLog.severe("<<< ==== >>>", new Object[0]);
                    FMLRelaunchLog.severe("The following is diagnostic information for developers to review.", new Object[0]);

                    for(Throwable t : caughtErrors) {
                        FMLRelaunchLog.log(Level.SEVERE, t, "Error details", new Object[0]);
                    }

                    throw new RuntimeException("A fatal error occured and FML cannot continue");
                } else {
                    for(IFMLLoadingPlugin plug : loadPlugins) {
                        if (plug.getASMTransformerClass() != null) {
                            for(String xformClass : plug.getASMTransformerClass()) {
                                actualClassLoader.registerTransformer(xformClass);
                            }
                        }
                    }

                    downloadMonitor.updateProgressString("Running coremod plugins", new Object[0]);
                    Map<String, Object> data = new HashMap();
                    data.put("mcLocation", mcDir);
                    data.put("coremodList", loadPlugins);

                    for(IFMLLoadingPlugin plugin : loadPlugins) {
                        downloadMonitor.updateProgressString("Running coremod plugin %s", new Object[]{plugin.getClass().getSimpleName()});
                        data.put("coremodLocation", pluginLocations.get(plugin));
                        plugin.injectData(data);
                        String setupClass = plugin.getSetupClass();
                        if (setupClass != null) {
                            try {
                                IFMLCallHook call = (IFMLCallHook)Class.forName(setupClass, true, actualClassLoader).newInstance();
                                Map<String, Object> callData = new HashMap();
                                callData.put("classLoader", actualClassLoader);
                                call.injectData(callData);
                                call.call();
                            } catch (Exception var35) {
                                throw new RuntimeException(var35);
                            }
                        }

                        downloadMonitor.updateProgressString("Coremod plugin %s run successfully", new Object[]{plugin.getClass().getSimpleName()});
                        String modContainer = plugin.getModContainerClass();
                        if (modContainer != null) {
                            FMLInjectionData.containers.add(modContainer);
                        }
                    }

                    try {
                        downloadMonitor.updateProgressString("Validating minecraft", new Object[0]);
                        Class<?> loaderClazz = Class.forName("cpw.mods.fml.common.Loader", true, actualClassLoader);
                        Method m = loaderClazz.getMethod("injectData", Object[].class);
                        m.invoke(null, (Object)FMLInjectionData.data());
                        m = loaderClazz.getMethod("instance");
                        m.invoke(null);
                        downloadMonitor.updateProgressString("Minecraft validated, launching...", new Object[0]);
                        downloadBuffer = null;
                    } catch (Exception var34) {
                        System.out.println("A CRITICAL PROBLEM OCCURED INITIALIZING MINECRAFT - LIKELY YOU HAVE AN INCORRECT VERSION FOR THIS FML");
                        throw new RuntimeException(var34);
                    }
                }
            }
        }
    }

    private static void discoverCoreMods(File mcDir, RelaunchClassLoader classLoader, List<IFMLLoadingPlugin> loadPlugins, List<ILibrarySet> libraries) {
        downloadMonitor.updateProgressString("Discovering coremods", new Object[0]);
        File coreMods = setupCoreModDir(mcDir);
        FilenameFilter ff = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        };
        File[] coreModList = coreMods.listFiles(ff);
        Arrays.sort(coreModList);

        for(File coreMod : coreModList) {
            downloadMonitor.updateProgressString("Found a candidate coremod %s", new Object[]{coreMod.getName()});

            Attributes mfAttributes;
            try {
                JarFile jar = new JarFile(coreMod);
                mfAttributes = jar.getManifest().getMainAttributes();
            } catch (IOException var26) {
                FMLRelaunchLog.log(Level.SEVERE, var26, "Unable to read the coremod jar file %s - ignoring", new Object[]{coreMod.getName()});
                continue;
            }

            String fmlCorePlugin = mfAttributes.getValue("FMLCorePlugin");
            if (fmlCorePlugin == null) {
                FMLRelaunchLog.severe("The coremod %s does not contain a valid jar manifest- it will be ignored", new Object[]{coreMod.getName()});
            } else {
                try {
                    classLoader.addURL(coreMod.toURI().toURL());
                } catch (MalformedURLException var25) {
                    FMLRelaunchLog.log(Level.SEVERE, var25, "Unable to convert file into a URL. weird", new Object[0]);
                    continue;
                }

                try {
                    downloadMonitor.updateProgressString("Loading coremod %s", new Object[]{coreMod.getName()});
                    classLoader.addTransformerExclusion(fmlCorePlugin);
                    Class<?> coreModClass = Class.forName(fmlCorePlugin, true, classLoader);
                    TransformerExclusions trExclusions = (TransformerExclusions)coreModClass.getAnnotation(TransformerExclusions.class);
                    if (trExclusions != null) {
                        for(String st : trExclusions.value()) {
                            classLoader.addTransformerExclusion(st);
                        }
                    }

                    IFMLLoadingPlugin plugin = (IFMLLoadingPlugin)coreModClass.newInstance();
                    loadPlugins.add(plugin);
                    pluginLocations.put(plugin, coreMod);
                    if (plugin.getLibraryRequestClass() != null) {
                        for(String libName : plugin.getLibraryRequestClass()) {
                            libraries.add((ILibrarySet)Class.forName(libName, true, classLoader).newInstance());
                        }
                    }

                    downloadMonitor.updateProgressString("Loaded coremod %s", new Object[]{coreMod.getName()});
                } catch (ClassNotFoundException var21) {
                    FMLRelaunchLog.log(Level.SEVERE, var21, "Coremod %s: Unable to class load the plugin %s", new Object[]{coreMod.getName(), fmlCorePlugin});
                } catch (ClassCastException var22) {
                    FMLRelaunchLog.log(
                            Level.SEVERE,
                            var22,
                            "Coremod %s: The plugin %s is not an implementor of IFMLLoadingPlugin",
                            new Object[]{coreMod.getName(), fmlCorePlugin}
                    );
                } catch (InstantiationException var23) {
                    FMLRelaunchLog.log(
                            Level.SEVERE, var23, "Coremod %s: The plugin class %s was not instantiable", new Object[]{coreMod.getName(), fmlCorePlugin}
                    );
                } catch (IllegalAccessException var24) {
                    FMLRelaunchLog.log(
                            Level.SEVERE, var24, "Coremod %s: The plugin class %s was not accessible", new Object[]{coreMod.getName(), fmlCorePlugin}
                    );
                }
            }
        }

    }

    private static File setupLibDir(File mcDir) {
        File libDir = new File(mcDir, "lib");

        try {
            libDir = libDir.getCanonicalFile();
        } catch (IOException var3) {
            throw new RuntimeException(String.format("Unable to canonicalize the lib dir at %s", mcDir.getName()), var3);
        }

        if (!libDir.exists()) {
            libDir.mkdir();
        } else if (libDir.exists() && !libDir.isDirectory()) {
            throw new RuntimeException(String.format("Found a lib file in %s that's not a directory", mcDir.getName()));
        }

        return libDir;
    }

    private static File setupCoreModDir(File mcDir) {
        File coreModDir = new File(mcDir, "coremods");

        try {
            coreModDir = coreModDir.getCanonicalFile();
        } catch (IOException var3) {
            throw new RuntimeException(String.format("Unable to canonicalize the coremod dir at %s", mcDir.getName()), var3);
        }

        if (!coreModDir.exists()) {
            coreModDir.mkdir();
        } else if (coreModDir.exists() && !coreModDir.isDirectory()) {
            throw new RuntimeException(String.format("Found a coremod file in %s that's not a directory", mcDir.getName()));
        }

        return coreModDir;
    }

    private static void downloadFile(File libFile, String rootUrl, String realFilePath, String hash) {
        try {
            URL libDownload = new URL(String.format(rootUrl, realFilePath));
            System.out.println("Downloading file: " + libDownload.getHost() + libDownload.getPath());
            System.out.println("The libFile's path is " + libFile.getAbsolutePath());
            String infoString = String.format("Downloading file %s", libDownload.toString());
            downloadMonitor.updateProgressString(infoString, new Object[0]);
            FMLRelaunchLog.info(infoString, new Object[0]);
            URLConnection connection = libDownload.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "FML Relaunch Downloader");
            int sizeGuess = connection.getContentLength();
            performDownload(connection.getInputStream(), sizeGuess, hash, libFile);
            downloadMonitor.updateProgressString("Download complete", new Object[0]);
            FMLRelaunchLog.info("Download complete", new Object[0]);
        } catch (Exception var8) {
            if (downloadMonitor.shouldStopIt()) {
                FMLRelaunchLog.warning("You have stopped the downloading operation before it could complete", new Object[0]);
            } else if (var8 instanceof RuntimeException) {
                throw (RuntimeException)var8;
            } else {
                FMLRelaunchLog.severe(
                        "There was a problem downloading the file %s automatically. Perhaps you have an environment without internet access. You will need to download the file manually or restart and let it try again\n",
                        new Object[]{libFile.getName()}
                );
                libFile.delete();
                throw new RuntimeException("A download error occured", var8);
            }
        }
    }

    public static List<String> getLibraries() {
        return loadedLibraries;
    }

    private static void performDownload(InputStream is, int sizeGuess, String validationHash, File target) {
        if (sizeGuess > downloadBuffer.capacity()) {
            throw new RuntimeException(String.format("The file %s is too large to be downloaded by FML - the coremod is invalid", target.getName()));
        } else {
            downloadBuffer.clear();
            int fullLength = 0;
            downloadMonitor.resetProgress(sizeGuess);

            try {
                downloadMonitor.setPokeThread(Thread.currentThread());
                byte[] smallBuffer = new byte[1024];

                int bytesRead;
                while((bytesRead = is.read(smallBuffer)) >= 0) {
                    downloadBuffer.put(smallBuffer, 0, bytesRead);
                    fullLength += bytesRead;
                    if (downloadMonitor.shouldStopIt()) {
                        break;
                    }

                    downloadMonitor.updateProgress(fullLength);
                }

                is.close();
                downloadMonitor.setPokeThread(null);
                downloadBuffer.limit(fullLength);
                downloadBuffer.position(0);
            } catch (InterruptedIOException var9) {
                Thread.interrupted();
                return;
            } catch (IOException var10) {
                throw new RuntimeException(var10);
            }

            try {
                String cksum = generateChecksum(downloadBuffer);
                if (cksum.equals(validationHash)) {
                    downloadBuffer.position(0);
                    FileOutputStream fos = new FileOutputStream(target);
                    fos.getChannel().write(downloadBuffer);
                    fos.close();
                } else {
                    throw new RuntimeException(
                            String.format(
                                    "The downloaded file %s has an invalid checksum %s (expecting %s). The download did not succeed correctly and the file has been deleted. Please try launching again.",
                                    target.getName(),
                                    cksum,
                                    validationHash
                            )
                    );
                }
            } catch (Exception var8) {
                if (var8 instanceof RuntimeException) {
                    throw (RuntimeException)var8;
                } else {
                    throw new RuntimeException(var8);
                }
            }
        }
    }

    private static String generateChecksum(ByteBuffer buffer) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(buffer);
            byte[] chksum = digest.digest();
            StringBuilder hex = new StringBuilder(2 * chksum.length);

            for(byte b : chksum) {
                hex.append("0123456789abcdef".charAt((b & 240) >> 4)).append("0123456789abcdef".charAt(b & 15));
            }

            return hex.toString();
        } catch (Exception var8) {
            return null;
        }
    }
}
