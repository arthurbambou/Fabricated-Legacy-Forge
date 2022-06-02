package cpw.mods.fml.common;

public interface IScheduledTickHandler extends ITickHandler {
    int nextTickSpacing();
}
