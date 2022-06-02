package fr.catcore.fabricatedforge.mixininterface;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface IRailBlock {
    public void setRenderType(int value);
    public boolean isFlexibleRail(World world, int y, int x, int z);
    public boolean canMakeSlopes(World world, int x, int y, int z);
    public int getBasicRailMetadata(BlockView world, AbstractMinecartEntity cart, int x, int y, int z);
    public float getRailMaxSpeed(World world, AbstractMinecartEntity cart, int y, int x, int z);
    public void onMinecartPass(World world, AbstractMinecartEntity cart, int y, int x, int z);
    public boolean hasPowerBit(World world, int x, int y, int z);

    boolean getField_304();
}
