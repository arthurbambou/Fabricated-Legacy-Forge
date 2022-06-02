package net.minecraftforge.transformers;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import java.io.IOException;

public class ForgeAccessTransformer extends AccessTransformer {
    public ForgeAccessTransformer() throws IOException {
        super("forge_at.cfg");
    }
}
