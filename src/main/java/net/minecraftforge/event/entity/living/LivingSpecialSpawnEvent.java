package net.minecraftforge.event.entity.living;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingSpecialSpawnEvent extends LivingEvent {
    public final World world;
    public final float x;
    public final float y;
    public final float z;
    private boolean handeled = false;

    public LivingSpecialSpawnEvent(MobEntity entity, World world, float x, float y, float z) {
        super(entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
