package cpw.mods.fml.client;

import cpw.mods.fml.common.FMLLog;
import java.awt.Dimension;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.client.class_534;
import net.minecraft.client.class_584;
import net.minecraft.client.texture.ITexturePack;

public class FMLTextureFX extends class_584 implements ITextureFX {
    public int tileSizeBase = 16;
    public int tileSizeSquare = 256;
    public int tileSizeMask = 15;
    public int tileSizeSquareMask = 255;
    public boolean errored = false;
    protected Logger log = FMLLog.getLogger();

    public FMLTextureFX(int icon) {
        super(icon);
    }

    public void setErrored(boolean err) {
        this.errored = err;
    }

    public boolean getErrored() {
        return this.errored;
    }

    public void onTexturePackChanged(class_534 engine, ITexturePack texturepack, Dimension dimensions) {
        this.onTextureDimensionsUpdate(dimensions.width, dimensions.height);
    }

    public void onTextureDimensionsUpdate(int width, int height) {
        this.tileSizeBase = width >> 4;
        this.tileSizeSquare = this.tileSizeBase * this.tileSizeBase;
        this.tileSizeMask = this.tileSizeBase - 1;
        this.tileSizeSquareMask = this.tileSizeSquare - 1;
        this.setErrored(false);
        this.setup();
    }

    protected void setup() {
        this.field_2152 = new byte[this.tileSizeSquare << 2];
    }

    public boolean unregister(class_534 engine, List<class_584> effects) {
        effects.remove(this);
        return true;
    }
}
