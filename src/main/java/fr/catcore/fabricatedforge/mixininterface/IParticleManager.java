package fr.catcore.fabricatedforge.mixininterface;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.hit.BlockHitResult;

public interface IParticleManager {

    public void addEffect(Particle effect, Object obj);

    public void addBlockHitEffects(int x, int y, int z, BlockHitResult target);
}
