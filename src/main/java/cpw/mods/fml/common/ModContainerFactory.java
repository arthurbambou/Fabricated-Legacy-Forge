package cpw.mods.fml.common;

import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.discovery.asm.ASMModParser;
import cpw.mods.fml.common.discovery.asm.ModAnnotation;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import java.io.File;
import java.util.regex.Pattern;
import org.objectweb.asm.Type;

public class ModContainerFactory {
    private static Pattern modClass = Pattern.compile(".*(\\.|)(mod\\_[^\\s$]+)$");
    private static ModContainerFactory INSTANCE = new ModContainerFactory();

    public ModContainerFactory() {
    }

    public static ModContainerFactory instance() {
        return INSTANCE;
    }

    public ModContainer build(ASMModParser modParser, File modSource, ModCandidate container) {
        String className = modParser.getASMType().getClassName();
        if (modParser.isBaseMod(container.getRememberedBaseMods()) && modClass.matcher(className).find()) {
            FMLLog.fine("Identified a BaseMod type mod %s", new Object[]{className});
            return new ModLoaderModContainer(className, modSource, modParser.getBaseModProperties());
        } else {
            if (modClass.matcher(className).find()) {
                FMLLog.fine(
                        "Identified a class %s following modloader naming convention but not directly a BaseMod or currently seen subclass",
                        new Object[]{className}
                );
                container.rememberModCandidateType(modParser);
            } else if (modParser.isBaseMod(container.getRememberedBaseMods())) {
                FMLLog.fine("Found a basemod %s of non-standard naming format", new Object[]{className});
                container.rememberBaseModType(className);
            }

            if (className.startsWith("net.minecraft.src.") && container.isClasspath() && !container.isMinecraftJar()) {
                FMLLog.severe(
                        "FML has detected a mod that is using a package name based on 'net.minecraft.src' : %s. This is generally a severe programming error.  There should be no mod code in the minecraft namespace. MOVE YOUR MOD! If you're in eclipse, select your source code and 'refactor' it into a new package. Go on. DO IT NOW!",
                        new Object[]{className}
                );
            }

            for(ModAnnotation ann : modParser.getAnnotations()) {
                if (ann.getASMType().equals(Type.getType(Mod.class))) {
                    FMLLog.fine("Identified an FMLMod type mod %s", new Object[]{className});
                    return new FMLModContainer(className, modSource, ann.getValues());
                }
            }

            return null;
        }
    }
}
