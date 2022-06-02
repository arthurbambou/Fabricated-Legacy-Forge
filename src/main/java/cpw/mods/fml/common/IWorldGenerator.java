package cpw.mods.fml.common;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkProvider;

public interface IWorldGenerator {
    void generate(Random random, int i, int j, World arg, ChunkProvider arg2, ChunkProvider arg3);
}
