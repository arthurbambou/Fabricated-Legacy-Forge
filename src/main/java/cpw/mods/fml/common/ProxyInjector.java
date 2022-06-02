package cpw.mods.fml.common;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.logging.Level;

public class ProxyInjector {
    public ProxyInjector() {
    }

    public static void inject(ModContainer mod, ASMDataTable data, Side side) {
        FMLLog.fine("Attempting to inject @SidedProxy classes into %s", new Object[]{mod.getModId()});
        Set<ASMData> targets = data.getAnnotationsFor(mod).get(SidedProxy.class.getName());
        ClassLoader mcl = Loader.instance().getModClassLoader();

        for(ASMData targ : targets) {
            try {
                Class<?> proxyTarget = Class.forName(targ.getClassName(), true, mcl);
                Field target = proxyTarget.getDeclaredField(targ.getObjectName());
                if (target == null) {
                    FMLLog.severe(
                            "Attempted to load a proxy type into %s.%s but the field was not found", new Object[]{targ.getClassName(), targ.getObjectName()}
                    );
                    throw new LoaderException();
                }

                String targetType = side.isClient()
                        ? ((SidedProxy)target.getAnnotation(SidedProxy.class)).clientSide()
                        : ((SidedProxy)target.getAnnotation(SidedProxy.class)).serverSide();
                Object proxy = Class.forName(targetType, true, mcl).newInstance();
                if ((target.getModifiers() & 8) == 0) {
                    FMLLog.severe(
                            "Attempted to load a proxy type %s into %s.%s, but the field is not static",
                            new Object[]{targetType, targ.getClassName(), targ.getObjectName()}
                    );
                    throw new LoaderException();
                }

                if (!target.getType().isAssignableFrom(proxy.getClass())) {
                    FMLLog.severe(
                            "Attempted to load a proxy type %s into %s.%s, but the types don't match",
                            new Object[]{targetType, targ.getClassName(), targ.getObjectName()}
                    );
                    throw new LoaderException();
                }

                target.set(null, proxy);
            } catch (Exception var11) {
                FMLLog.log(
                        Level.SEVERE,
                        var11,
                        "An error occured trying to load a proxy into %s.%s",
                        new Object[]{targ.getAnnotationInfo(), targ.getClassName(), targ.getObjectName()}
                );
                throw new LoaderException(var11);
            }
        }

    }
}
