package cpw.mods.fml.relauncher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class RelaunchClassLoader extends URLClassLoader {
    private static String[] excludedPackages = new String[0];
    private static String[] transformerExclusions = new String[0];
    private List<URL> sources;
    private ClassLoader parent;
    private List<IClassTransformer> transformers;
    private Map<String, Class> cachedClasses;
    private Set<String> classLoaderExceptions = new HashSet();
    private Set<String> transformerExceptions = new HashSet();

    public RelaunchClassLoader(URL[] sources) {
        super(sources, null);
        this.sources = new ArrayList(Arrays.asList(sources));
        this.parent = this.getClass().getClassLoader();
        this.cachedClasses = new HashMap(1000);
        this.transformers = new ArrayList(2);
        Thread.currentThread().setContextClassLoader(this);
        this.addClassLoaderExclusion("java.");
        this.addClassLoaderExclusion("sun.");
        this.addClassLoaderExclusion("cpw.mods.fml.relauncher.");
        this.addClassLoaderExclusion("net.minecraftforge.classloading.");
        this.addTransformerExclusion("javax.");
        this.addTransformerExclusion("org.objectweb.asm.");
        this.addTransformerExclusion("com.google.common.");
        this.addTransformerExclusion("cpw.mods.fml.common.asm.SideOnly");
        this.addTransformerExclusion("cpw.mods.fml.common.Side");
    }

    public void registerTransformer(String transformerClassName) {
        try {
            this.transformers.add((IClassTransformer)this.loadClass(transformerClassName).newInstance());
        } catch (Exception var3) {
            FMLRelaunchLog.log(Level.SEVERE, var3, "A critical problem occured registering the ASM transformer class %s", new Object[]{transformerClassName});
        }

    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (excludedPackages.length != 0) {
            this.classLoaderExceptions.addAll(Arrays.asList(excludedPackages));
            excludedPackages = new String[0];
        }

        if (transformerExclusions.length != 0) {
            this.transformerExceptions.addAll(Arrays.asList(transformerExclusions));
            transformerExclusions = new String[0];
        }

        for(String st : this.classLoaderExceptions) {
            if (name.startsWith(st)) {
                return this.parent.loadClass(name);
            }
        }

        if (this.cachedClasses.containsKey(name)) {
            return (Class<?>)this.cachedClasses.get(name);
        } else {
            for(String st : this.transformerExceptions) {
                if (name.startsWith(st)) {
                    Class<?> cl = super.findClass(name);
                    this.cachedClasses.put(name, cl);
                    return cl;
                }
            }

            try {
                int lastDot = name.lastIndexOf(46);
                if (lastDot > -1) {
                    String pkgname = name.substring(0, lastDot);
                    if (this.getPackage(pkgname) == null) {
                        this.definePackage(pkgname, null, null, null, null, null, null, null);
                    }
                }

                byte[] basicClass = this.getClassBytes(name);
                byte[] transformedClass = this.runTransformers(name, basicClass);
                Class<?> cl = this.defineClass(name, transformedClass, 0, transformedClass.length);
                this.cachedClasses.put(name, cl);
                return cl;
            } catch (Throwable var6) {
                throw new ClassNotFoundException(name, var6);
            }
        }
    }

    public byte[] getClassBytes(String name) throws IOException {
        InputStream classStream = null;

        Object var4;
        try {
            URL classResource = this.findResource(name.replace('.', '/').concat(".class"));
            if (classResource != null) {
                classStream = classResource.openStream();
                return this.readFully(classStream);
            }

            var4 = null;
        } finally {
            if (classStream != null) {
                try {
                    classStream.close();
                } catch (IOException var12) {
                }
            }

        }

        return (byte[])var4;
    }

    private byte[] runTransformers(String name, byte[] basicClass) {
        for(IClassTransformer transformer : this.transformers) {
            basicClass = transformer.transform(name, basicClass);
        }

        return basicClass;
    }

    public void addURL(URL url) {
        super.addURL(url);
        this.sources.add(url);
    }

    public List<URL> getSources() {
        return this.sources;
    }

    private byte[] readFully(InputStream stream) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(stream.available());

            int r;
            while((r = stream.read()) != -1) {
                bos.write(r);
            }

            return bos.toByteArray();
        } catch (Throwable var4) {
            return new byte[0];
        }
    }

    public List<IClassTransformer> getTransformers() {
        return Collections.unmodifiableList(this.transformers);
    }

    private void addClassLoaderExclusion(String toExclude) {
        this.classLoaderExceptions.add(toExclude);
    }

    void addTransformerExclusion(String toExclude) {
        this.transformerExceptions.add(toExclude);
    }
}
