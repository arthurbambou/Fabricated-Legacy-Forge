package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.IDispenserHandler;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ModLoaderDispenseHelper implements IDispenserHandler {
    private BaseModProxy mod;

    public ModLoaderDispenseHelper(BaseModProxy mod) {
        this.mod = mod;
    }

    public int dispense(int x, int y, int z, int xVelocity, int zVelocity, World world, ItemStack item, Random random, double entX, double entY, double entZ) {
        int ret = this.mod.dispenseEntity(world, item, random, x, y, z, xVelocity, zVelocity, entX, entY, entZ);
        return ret == 0 ? -1 : ret;
    }
}
