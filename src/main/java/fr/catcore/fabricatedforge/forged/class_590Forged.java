package fr.catcore.fabricatedforge.forged;

import cpw.mods.fml.client.FMLTextureFX;
import net.minecraft.block.Block;

public class class_590Forged extends FMLTextureFX {
    protected float[] field_2176 = new float[256];
    protected float[] field_2177 = new float[256];
    protected float[] field_2178 = new float[256];
    protected float[] field_2179 = new float[256];
    private int field_2180 = 0;

    public class_590Forged() {
        super(Block.field_334.field_439);
        this.setup();
    }

    public void setup() {
        super.setup();
        this.field_2176 = new float[this.tileSizeSquare];
        this.field_2177 = new float[this.tileSizeSquare];
        this.field_2178 = new float[this.tileSizeSquare];
        this.field_2179 = new float[this.tileSizeSquare];
        this.field_2180 = 0;
    }

    public void method_1613() {
        ++this.field_2180;

        for(int var1 = 0; var1 < this.tileSizeBase; ++var1) {
            for(int var2 = 0; var2 < this.tileSizeBase; ++var2) {
                float var3 = 0.0F;

                for(int var4 = var1 - 1; var4 <= var1 + 1; ++var4) {
                    int var5 = var4 & this.tileSizeMask;
                    int var6 = var2 & this.tileSizeMask;
                    var3 += this.field_2176[var5 + var6 * this.tileSizeBase];
                }

                this.field_2177[var1 + var2 * this.tileSizeBase] = var3 / 3.3F + this.field_2178[var1 + var2 * this.tileSizeBase] * 0.8F;
            }
        }

        for(int var131 = 0; var131 < this.tileSizeBase; ++var131) {
            for(int var2 = 0; var2 < this.tileSizeBase; ++var2) {
                this.field_2178[var131 + var2 * this.tileSizeBase] += this.field_2179[var131 + var2 * this.tileSizeBase] * 0.05F;
                if (this.field_2178[var131 + var2 * this.tileSizeBase] < 0.0F) {
                    this.field_2178[var131 + var2 * this.tileSizeBase] = 0.0F;
                }

                this.field_2179[var131 + var2 * this.tileSizeBase] -= 0.1F;
                if (Math.random() < 0.05) {
                    this.field_2179[var131 + var2 * this.tileSizeBase] = 0.5F;
                }
            }
        }

        float[] var12 = this.field_2177;
        this.field_2177 = this.field_2176;
        this.field_2176 = var12;

        for(int var2 = 0; var2 < this.tileSizeSquare; ++var2) {
            float var3 = this.field_2176[var2];
            if (var3 > 1.0F) {
                var3 = 1.0F;
            }

            if (var3 < 0.0F) {
                var3 = 0.0F;
            }

            float var13 = var3 * var3;
            int var5 = (int)(32.0F + var13 * 32.0F);
            int var6 = (int)(50.0F + var13 * 64.0F);
            int var7 = 255;
            int var8 = (int)(146.0F + var13 * 50.0F);
            if (this.field_2154) {
                int var9 = (var5 * 30 + var6 * 59 + var7 * 11) / 100;
                int var10 = (var5 * 30 + var6 * 70) / 100;
                int var11 = (var5 * 30 + var7 * 70) / 100;
                var5 = var9;
                var6 = var10;
                var7 = var11;
            }

            this.field_2152[var2 * 4 + 0] = (byte)var5;
            this.field_2152[var2 * 4 + 1] = (byte)var6;
            this.field_2152[var2 * 4 + 2] = (byte)var7;
            this.field_2152[var2 * 4 + 3] = (byte)var8;
        }

    }
}
