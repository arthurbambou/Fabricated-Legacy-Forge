package cpw.mods.fml.common;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IDispenseHandler {
    @Deprecated
    int dispense(double d, double e, double f, int i, int j, World arg, ItemStack arg2, Random random, double g, double h, double k);
}
