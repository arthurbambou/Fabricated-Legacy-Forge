package net.minecraft;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.FMLTextureFX;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import net.minecraft.client.class_534;
import org.lwjgl.opengl.GL11;

public class ModTextureStatic extends FMLTextureFX {
    private boolean oldanaglyph;
    private int[] pixels;
    private String targetTex;
    private int storedSize;
    private BufferedImage overrideData;
    private int needApply;

    public ModTextureStatic(int icon, int target, BufferedImage image) {
        this(icon, 1, target, image);
    }

    public ModTextureStatic(int icon, int size, int target, BufferedImage image) {
        this(icon, size, target == 0 ? "/terrain.png" : "/gui/items.png", image);
    }

    public ModTextureStatic(int icon, int size, String target, BufferedImage image) {
        super(icon);
        this.oldanaglyph = false;
        this.pixels = null;
        this.targetTex = null;
        this.overrideData = null;
        this.needApply = 2;
        class_534 re = FMLClientHandler.instance().getClient().field_3813;
        this.targetTex = target;
        this.storedSize = size;
        this.field_2156 = size;
        this.field_2157 = re.getTextureFromPath(target);
        this.overrideData = image;
    }

    public void setup() {
        super.setup();
        int sWidth = this.overrideData.getWidth();
        int sHeight = this.overrideData.getHeight();
        this.pixels = new int[this.tileSizeSquare];
        if (this.tileSizeBase == sWidth && this.tileSizeBase == sHeight) {
            this.overrideData.getRGB(0, 0, sWidth, sHeight, this.pixels, 0, sWidth);
        } else {
            BufferedImage tmp = new BufferedImage(this.tileSizeBase, this.tileSizeBase, 6);
            Graphics2D gfx = tmp.createGraphics();
            gfx.drawImage(this.overrideData, 0, 0, this.tileSizeBase, this.tileSizeBase, 0, 0, sWidth, sHeight, (ImageObserver)null);
            tmp.getRGB(0, 0, this.tileSizeBase, this.tileSizeBase, this.pixels, 0, this.tileSizeBase);
            gfx.dispose();
        }

        this.update();
    }

    public void method_1613() {
        if (this.oldanaglyph != this.field_2154) {
            this.update();
        }

        this.field_2156 = this.needApply == 0 ? 0 : this.storedSize;
        if (this.needApply > 0) {
            --this.needApply;
        }

    }

    public void method_1614(class_534 par1RenderEngine) {
        GL11.glBindTexture(3553, par1RenderEngine.getTextureFromPath(this.targetTex));
    }

    public void update() {
        this.needApply = 2;

        for(int idx = 0; idx < this.pixels.length; ++idx) {
            int i = idx * 4;
            int a = this.pixels[idx] >> 24 & 255;
            int r = this.pixels[idx] >> 16 & 255;
            int g = this.pixels[idx] >> 8 & 255;
            int b = this.pixels[idx] >> 0 & 255;
            if (this.field_2154) {
                r = g = b = (r + g + b) / 3;
            }

            this.field_2152[i + 0] = (byte)r;
            this.field_2152[i + 1] = (byte)g;
            this.field_2152[i + 2] = (byte)b;
            this.field_2152[i + 3] = (byte)a;
        }

        this.oldanaglyph = this.field_2154;
    }

    public static BufferedImage scale2x(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage tmp = new BufferedImage(w * 2, h * 2, 2);

        for(int x = 0; x < h; ++x) {
            int x2 = x * 2;

            for(int y = 0; y < w; ++y) {
                int y2 = y * 2;
                int E = image.getRGB(y, x);
                int D = x == 0 ? E : image.getRGB(y, x - 1);
                int B = y == 0 ? E : image.getRGB(y - 1, x);
                int H = y >= w - 1 ? E : image.getRGB(y + 1, x);
                int F = x >= h - 1 ? E : image.getRGB(y, x + 1);
                int e0;
                int e1;
                int e2;
                int e3;
                if (B != H && D != F) {
                    e0 = D == B ? D : E;
                    e1 = B == F ? F : E;
                    e2 = D == H ? D : E;
                    e3 = H == F ? F : E;
                } else {
                    e3 = E;
                    e2 = E;
                    e1 = E;
                    e0 = E;
                }

                tmp.setRGB(y2, x2, e0);
                tmp.setRGB(y2 + 1, x2, e1);
                tmp.setRGB(y2, x2 + 1, e2);
                tmp.setRGB(y2 + 1, x2 + 1, e3);
            }
        }

        return tmp;
    }

    public String toString() {
        return String.format("ModTextureStatic %s @ %d", this.targetTex, this.field_2153);
    }
}
