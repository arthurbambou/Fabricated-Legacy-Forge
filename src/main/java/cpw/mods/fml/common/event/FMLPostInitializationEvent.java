package cpw.mods.fml.common.event;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState.ModState;

public class FMLPostInitializationEvent extends FMLStateEvent {
    public FMLPostInitializationEvent(Object... data) {
        super(data);
    }

    public ModState getModState() {
        return ModState.POSTINITIALIZED;
    }

    public Object buildSoftDependProxy(String modId, String className) {
        if (Loader.isModLoaded(modId)) {
            try {
                Class<?> clz = Class.forName(className, true, Loader.instance().getModClassLoader());
                return clz.newInstance();
            } catch (Exception var4) {
                Throwables.propagateIfPossible(var4);
                return null;
            }
        } else {
            return null;
        }
    }
}
