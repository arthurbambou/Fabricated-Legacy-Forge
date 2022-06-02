package cpw.mods.fml.common.modloader;

import com.google.common.base.Function;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraft.entity.Entity;

public class ModLoaderEntitySpawnCallback implements Function<EntitySpawnPacket, Entity> {
    private BaseModProxy mod;
    private EntityRegistration registration;
    private boolean isAnimal;

    public ModLoaderEntitySpawnCallback(BaseModProxy mod, EntityRegistration er) {
        this.mod = mod;
        this.registration = er;
    }

    public Entity apply(EntitySpawnPacket input) {
        return ModLoaderHelper.sidedHelper.spawnEntity(this.mod, input, this.registration);
    }
}
