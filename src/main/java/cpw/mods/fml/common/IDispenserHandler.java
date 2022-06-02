package cpw.mods.fml.common;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IDispenserHandler {
    int dispense(int i, int j, int k, int l, int m, World arg, ItemStack arg2, Random random, double d, double e, double f);
}
