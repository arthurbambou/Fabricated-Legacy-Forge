package cpw.mods.fml.common;

import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;

public interface WorldAccessContainer {
    NbtCompound getDataForWriting(WorldSaveHandler arg, LevelProperties arg2);

    void readData(WorldSaveHandler arg, LevelProperties arg2, Map<String, NbtElement> map, NbtCompound arg3);
}
