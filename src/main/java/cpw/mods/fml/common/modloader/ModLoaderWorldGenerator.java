package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.IWorldGenerator;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.NetherChunkGenerator;
import net.minecraft.world.chunk.SurfaceChunkGenerator;

public class ModLoaderWorldGenerator implements IWorldGenerator {
    private BaseModProxy mod;

    public ModLoaderWorldGenerator(BaseModProxy mod) {
        this.mod = mod;
    }

    public void generate(Random random, int chunkX, int chunkZ, World world, ChunkProvider chunkGenerator, ChunkProvider chunkProvider) {
        if (chunkGenerator instanceof SurfaceChunkGenerator) {
            this.mod.generateSurface(world, random, chunkX << 4, chunkZ << 4);
        } else if (chunkGenerator instanceof NetherChunkGenerator) {
            this.mod.generateNether(world, random, chunkX << 4, chunkZ << 4);
        }

    }
}
