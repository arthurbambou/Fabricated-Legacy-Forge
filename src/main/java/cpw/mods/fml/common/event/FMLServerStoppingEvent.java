package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState.ModState;

public class FMLServerStoppingEvent extends FMLStateEvent {
    public FMLServerStoppingEvent(Object... data) {
        super(data);
    }

    public ModState getModState() {
        return ModState.AVAILABLE;
    }
}
