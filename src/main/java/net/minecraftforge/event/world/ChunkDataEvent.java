package net.minecraftforge.event.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.Chunk;

public class ChunkDataEvent extends ChunkEvent {
    private final NbtCompound data;

    public ChunkDataEvent(Chunk chunk, NbtCompound data) {
        super(chunk);
        this.data = data;
    }

    public NbtCompound getData() {
        return this.data;
    }

    public static class Load extends ChunkDataEvent {
        public Load(Chunk chunk, NbtCompound data) {
            super(chunk, data);
        }
    }

    public static class Save extends ChunkDataEvent {
        public Save(Chunk chunk, NbtCompound data) {
            super(chunk, data);
        }
    }
}
