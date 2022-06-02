package fr.catcore.fabricatedforge.mixininterface;

import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public interface IFireBlock {
    boolean canBlockCatchFire(BlockView world, int x, int y, int z, ForgeDirection face);
    int getChanceToEncourageFire(World world, int x, int y, int z, int oldChance, ForgeDirection face);
}
