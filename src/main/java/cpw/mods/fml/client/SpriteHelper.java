package cpw.mods.fml.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import java.util.BitSet;
import java.util.HashMap;
import java.util.logging.Level;

public class SpriteHelper {
    private static HashMap<String, BitSet> spriteInfo = new HashMap();

    public SpriteHelper() {
    }

    private static void initMCSpriteMaps() {
        BitSet slots = toBitSet(
                "0000000000000000000000000011000000000000001000000000000001100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111110000000000000000000000000111110000000000011110000000000000000000"
        );
        spriteInfo.put("/terrain.png", slots);
        slots = toBitSet(
                "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111100000000000111110001000000000001011100000000111000000000000110000110000111100000000000011110000000000000000"
        );
        spriteInfo.put("/gui/items.png", slots);
    }

    public static void registerSpriteMapForFile(String file, String spriteMap) {
        if (spriteInfo.size() == 0) {
            initMCSpriteMaps();
        }

        if (spriteInfo.containsKey(file)) {
            FMLCommonHandler.instance().getFMLLogger().finer(String.format("Duplicate attempt to register a sprite file %s for overriding -- ignoring", file));
        } else {
            spriteInfo.put(file, toBitSet(spriteMap));
        }
    }

    public static int getUniqueSpriteIndex(String path) {
        if (!spriteInfo.containsKey("/terrain.png")) {
            initMCSpriteMaps();
        }

        BitSet slots = (BitSet)spriteInfo.get(path);
        if (slots == null) {
            Exception ex = new Exception(String.format("Invalid getUniqueSpriteIndex call for texture: %s", path));
            FMLLog.log(Level.SEVERE, ex, "A critical error has been detected with sprite overrides", new Object[0]);
            FMLCommonHandler.instance().raiseException(ex, "Invalid request to getUniqueSpriteIndex", true);
        }

        int ret = getFreeSlot(slots);
        if (ret == -1) {
            Exception ex = new Exception(String.format("No more sprite indicies left for: %s", path));
            FMLLog.log(Level.SEVERE, ex, "There are no sprite indicies left for %s", new Object[]{path});
            FMLCommonHandler.instance().raiseException(ex, "No more sprite indicies left", true);
        }

        return ret;
    }

    public static BitSet toBitSet(String data) {
        BitSet ret = new BitSet(data.length());

        for(int x = 0; x < data.length(); ++x) {
            ret.set(x, data.charAt(x) == '1');
        }

        return ret;
    }

    public static int getFreeSlot(BitSet slots) {
        int next = slots.nextSetBit(0);
        slots.clear(next);
        return next;
    }

    public static int freeSlotCount(String textureToOverride) {
        return ((BitSet)spriteInfo.get(textureToOverride)).cardinality();
    }
}
