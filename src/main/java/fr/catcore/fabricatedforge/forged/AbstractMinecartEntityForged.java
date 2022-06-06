package fr.catcore.fabricatedforge.forged;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;

public class AbstractMinecartEntityForged extends AbstractMinecartEntity {
    public AbstractMinecartEntityForged(World world, int type) {
        this(world);
        this.field_3897 = type;
    }

    public AbstractMinecartEntityForged(World world) {
        super(world);
    }

    public AbstractMinecartEntityForged(World world, double d, double e, double f, int i) {
        super(world, d, e, f, i);
    }
}
