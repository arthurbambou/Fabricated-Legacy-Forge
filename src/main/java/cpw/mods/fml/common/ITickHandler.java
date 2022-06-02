package cpw.mods.fml.common;

import java.util.EnumSet;

public interface ITickHandler {
    void tickStart(EnumSet<TickType> enumSet, Object... objects);

    void tickEnd(EnumSet<TickType> enumSet, Object... objects);

    EnumSet<TickType> ticks();

    String getLabel();
}
