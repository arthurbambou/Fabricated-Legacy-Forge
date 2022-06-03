package fr.catcore.fabricatedforge.forged;

import cpw.mods.fml.client.FMLTextureFX;
import net.minecraft.block.Block;

public class class_589Forged extends FMLTextureFX {
    protected float[] field_2171 = new float[256];
    protected float[] field_2172 = new float[256];
    protected float[] field_2173 = new float[256];
    protected float[] field_2174 = new float[256];
    private int field_2175 = 0;

    public class_589Forged() {
        super(Block.field_334.field_439 + 1);
        this.field_2156 = 2;
        this.setup();
    }

    public void setup() {
        super.setup();
        this.field_2171 = new float[this.tileSizeSquare];
        this.field_2172 = new float[this.tileSizeSquare];
        this.field_2173 = new float[this.tileSizeSquare];
        this.field_2174 = new float[this.tileSizeSquare];
        this.field_2175 = 0;
    }

    public void method_1613() {
        ++this.field_2175;

        for(int var1 = 0; var1 < this.tileSizeBase; ++var1) {
            for(int var2 = 0; var2 < this.tileSizeBase; ++var2) {
                float var3 = 0.0F;

                for(int var4 = var2 - 2; var4 <= var2; ++var4) {
                    int var5 = var1 & this.tileSizeMask;
                    int var6 = var4 & this.tileSizeMask;
                    var3 += this.field_2171[var5 + var6 * this.tileSizeBase];
                }

                this.field_2172[var1 + var2 * this.tileSizeBase] = var3 / 3.2F + this.field_2173[var1 + var2 * this.tileSizeBase] * 0.8F;
            }
        }

        for(int var131 = 0; var131 < this.tileSizeBase; ++var131) {
            for(int var2 = 0; var2 < this.tileSizeBase; ++var2) {
                this.field_2173[var131 + var2 * this.tileSizeBase] += this.field_2174[var131 + var2 * this.tileSizeBase] * 0.05F;
                if (this.field_2173[var131 + var2 * this.tileSizeBase] < 0.0F) {
                    this.field_2173[var131 + var2 * this.tileSizeBase] = 0.0F;
                }

                this.field_2174[var131 + var2 * this.tileSizeBase] -= 0.3F;
                if (Math.random() < 0.2) {
                    this.field_2174[var131 + var2 * this.tileSizeBase] = 0.5F;
                }
            }
        }

        float[] var12 = this.field_2172;
        this.field_2172 = this.field_2171;
        this.field_2171 = var12;

        for(int var2 = 0; var2 < this.tileSizeSquare; ++var2) {
            float var3 = this.field_2171[var2 - this.field_2175 * this.tileSizeBase & this.tileSizeSquareMask];
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
