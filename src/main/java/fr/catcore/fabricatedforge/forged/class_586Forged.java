package fr.catcore.fabricatedforge.forged;

import cpw.mods.fml.client.FMLTextureFX;
import net.minecraft.block.Block;
import net.minecraft.util.math.MathHelper;

public class class_586Forged extends FMLTextureFX {
    protected float[] field_2160 = new float[256];
    protected float[] field_2161 = new float[256];
    protected float[] field_2162 = new float[256];
    protected float[] field_2163 = new float[256];
    int field_2164 = 0;

    public class_586Forged() {
        super(Block.field_336.field_439 + 1);
        this.field_2156 = 2;
        this.setup();
    }

    public void setup() {
        super.setup();
        this.field_2160 = new float[this.tileSizeSquare];
        this.field_2161 = new float[this.tileSizeSquare];
        this.field_2162 = new float[this.tileSizeSquare];
        this.field_2163 = new float[this.tileSizeSquare];
        this.field_2164 = 0;
    }

    public void method_1613() {
        ++this.field_2164;

        for(int var1 = 0; var1 < this.tileSizeBase; ++var1) {
            for(int var2 = 0; var2 < this.tileSizeBase; ++var2) {
                float var3 = 0.0F;
                int var4 = (int)(MathHelper.sin((float)var2 * (float) Math.PI * 2.0F / 16.0F) * 1.2F);
                int var5 = (int)(MathHelper.sin((float)var1 * (float) Math.PI * 2.0F / 16.0F) * 1.2F);

                for(int var6 = var1 - 1; var6 <= var1 + 1; ++var6) {
                    for(int var7 = var2 - 1; var7 <= var2 + 1; ++var7) {
                        int var8 = var6 + var4 & this.tileSizeMask;
                        int var9 = var7 + var5 & this.tileSizeMask;
                        var3 += this.field_2160[var8 + var9 * this.tileSizeBase];
                    }
                }

                this.field_2161[var1 + var2 * this.tileSizeBase] = var3 / 10.0F
                        + (
                        this.field_2162[(var1 + 0 & this.tileSizeMask) + (var2 + 0 & this.tileSizeMask) * this.tileSizeBase]
                                + this.field_2162[(var1 + 1 & this.tileSizeMask) + (var2 + 0 & this.tileSizeMask) * this.tileSizeBase]
                                + this.field_2162[(var1 + 1 & this.tileSizeMask) + (var2 + 1 & this.tileSizeMask) * this.tileSizeBase]
                                + this.field_2162[(var1 + 0 & this.tileSizeMask) + (var2 + 1 & this.tileSizeMask) * this.tileSizeBase]
                )
                        / 4.0F
                        * 0.8F;
                this.field_2162[var1 + var2 * this.tileSizeBase] += this.field_2163[var1 + var2 * this.tileSizeBase] * 0.01F;
                if (this.field_2162[var1 + var2 * this.tileSizeBase] < 0.0F) {
                    this.field_2162[var1 + var2 * this.tileSizeBase] = 0.0F;
                }

                this.field_2163[var1 + var2 * this.tileSizeBase] -= 0.06F;
                if (Math.random() < 0.005) {
                    this.field_2163[var1 + var2 * this.tileSizeBase] = 1.5F;
                }
            }
        }

        float[] var11 = this.field_2161;
        this.field_2161 = this.field_2160;
        this.field_2160 = var11;

        for(int var2 = 0; var2 < this.tileSizeSquare; ++var2) {
            float var3 = this.field_2160[var2 - this.field_2164 / 3 * this.tileSizeBase & this.tileSizeSquareMask] * 2.0F;
            if (var3 > 1.0F) {
                var3 = 1.0F;
            }

            if (var3 < 0.0F) {
                var3 = 0.0F;
            }

            int var5 = (int)(var3 * 100.0F + 155.0F);
            int var6 = (int)(var3 * var3 * 255.0F);
            int var7 = (int)(var3 * var3 * var3 * var3 * 128.0F);
            if (this.field_2154) {
                int var8 = (var5 * 30 + var6 * 59 + var7 * 11) / 100;
                int var9 = (var5 * 30 + var6 * 70) / 100;
                int var10 = (var5 * 30 + var7 * 70) / 100;
                var5 = var8;
                var6 = var9;
                var7 = var10;
            }

            this.field_2152[var2 * 4 + 0] = (byte)var5;
            this.field_2152[var2 * 4 + 1] = (byte)var6;
            this.field_2152[var2 * 4 + 2] = (byte)var7;
            this.field_2152[var2 * 4 + 3] = -1;
        }

    }
}
