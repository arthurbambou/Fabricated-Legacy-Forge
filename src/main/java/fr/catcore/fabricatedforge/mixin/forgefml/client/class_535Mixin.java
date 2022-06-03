package fr.catcore.fabricatedforge.mixin.forgefml.client;

import net.minecraft.FMLRenderAccessLibrary;
import net.minecraft.block.*;
import net.minecraft.client.class_523;
import net.minecraft.client.class_535;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.Axis;
import net.minecraft.world.BlockView;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(class_535.class)
public abstract class class_535Mixin {

    @Shadow public abstract boolean method_1482(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1477(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1462(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1474(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1479(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1486(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1475(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1441(RailBlock railBlock, int i, int j, int k);

    @Shadow public abstract boolean method_1440(FenceBlock fenceBlock, int i, int j, int k);

    @Shadow public abstract boolean method_1469(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1484(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1489(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1459(Block block, int i, int j, int k, boolean bl);

    @Shadow public abstract boolean method_1464(Block block, int i, int j, int k, boolean bl);

    @Shadow public abstract boolean method_1478(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1476(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1439(FenceGateBlock fenceGateBlock, int i, int j, int k);

    @Shadow public abstract boolean method_1480(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1436(CauldronBlock cauldronBlock, int i, int j, int k);

    @Shadow public abstract boolean method_1435(BrewingStandBlock brewingStandBlock, int i, int j, int k);

    @Shadow public abstract boolean method_1487(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1438(DragonEggBlock dragonEggBlock, int i, int j, int k);

    @Shadow public abstract boolean method_1437(CocoaBlock cocoaBlock, int i, int j, int k);

    @Shadow public abstract boolean method_1471(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1472(Block block, int i, int j, int k);

    @Shadow public abstract boolean method_1483(Block block, int i, int j, int k);

    @Shadow public int field_2049;

    @Shadow public boolean field_2051;

    @Shadow public boolean field_2050;

    @Shadow public abstract void method_1461(Block block, double d, double e, double f, int i);

    @Shadow public abstract void method_1465(Block block, double d, double e, double f, int i);

    @Shadow public abstract void method_1468(Block block, double d, double e, double f, int i);

    @Shadow public abstract void method_1470(Block block, double d, double e, double f, int i);

    @Shadow public abstract float method_1434(int i, int j, int k, Material material);

    @Shadow public abstract void method_1444(Block block, double d, double e, double f, int i);

    @Shadow public boolean field_2058;

    @Shadow public float field_2059;

    @Shadow public float field_2060;

    @Shadow public float field_2061;

    @Shadow public float field_2062;

    @Shadow public float field_2063;

    @Shadow public float field_2064;

    @Shadow public float field_2065;

    @Shadow public boolean field_2009;

    @Shadow public boolean field_2018;

    @Shadow public boolean field_2013;

    @Shadow public boolean field_2015;

    @Shadow public boolean field_2010;

    @Shadow public boolean field_2019;

    @Shadow public boolean field_2012;

    @Shadow public boolean field_2014;

    @Shadow public boolean field_2011;

    @Shadow public boolean field_2008;

    @Shadow public boolean field_2020;

    @Shadow public boolean field_2016;

    @Shadow public int field_2033;

    @Shadow public int field_1998;

    @Shadow public int field_2000;

    @Shadow public int field_2001;

    @Shadow public int field_2003;

    @Shadow public float field_2067;

    @Shadow public float field_2069;

    @Shadow public float field_2070;

    @Shadow public float field_1983;

    @Shadow public float field_2066;

    @Shadow public int field_1997;

    @Shadow public float field_2068;

    @Shadow public int field_1999;

    @Shadow public float field_2071;

    @Shadow public int field_2002;

    @Shadow public float field_1984;

    @Shadow public int field_2004;

    @Shadow public int field_2034;

    @Shadow public int field_2037;

    @Shadow public int field_2036;

    @Shadow public int field_2035;

    @Shadow public abstract int method_1433(int i, int j, int k, int l);

    @Shadow public float field_2039;

    @Shadow public float field_2040;

    @Shadow public float field_2041;

    @Shadow public float field_2038;

    @Shadow public float field_2042;

    @Shadow public float field_2043;

    @Shadow public float field_2044;

    @Shadow public float field_2045;

    @Shadow public float field_2046;

    @Shadow public float field_2005;

    @Shadow public float field_2006;

    @Shadow public float field_2007;

    @Shadow public int field_2022;

    @Shadow public int field_2026;

    @Shadow public int field_2024;

    @Shadow public int field_2027;

    @Shadow public float field_1986;

    @Shadow public float field_1990;

    @Shadow public float field_1988;

    @Shadow public float field_1991;

    @Shadow public float field_1985;

    @Shadow public int field_2021;

    @Shadow public float field_1989;

    @Shadow public int field_2025;

    @Shadow public float field_1987;

    @Shadow public int field_2023;

    @Shadow public float field_1992;

    @Shadow public int field_2028;

    @Shadow public abstract void method_1456(Block block, double d, double e, double f, int i);

    @Shadow public float field_1993;

    @Shadow public float field_1994;

    @Shadow public int field_2029;

    @Shadow public int field_2030;

    @Shadow public static boolean field_2047;

    @Shadow public float field_1995;

    @Shadow public float field_1996;

    @Shadow public int field_2031;

    @Shadow public int field_2032;

    @Shadow public boolean field_2048;

    @Shadow public abstract void method_1446(Block block, int i, double d, double e, double f, double g);

    @Shadow public abstract void method_1457(Block block, int i, double d, double e, double f);

    @Shadow public abstract void method_1443(Block block, double d, double e, double f, double g, double h);

    @Shadow public abstract boolean method_4321(WallBlock wallBlock, int i, int j, int k);

    @Shadow public abstract boolean method_4318(FlowerPotBlock flowerPotBlock, int i, int j, int k);

    @Shadow public abstract boolean method_4317(BeaconBlock beaconBlock, int i, int j, int k);

    @Shadow public abstract boolean method_4314(AnvilBlock anvilBlock, int i, int j, int k);

    @Shadow public BlockView field_2017;

    @Shadow public abstract boolean method_4319(StairsBlock stairsBlock, int i, int j, int k);

    @Shadow public abstract boolean method_1442(PaneBlock paneBlock, int i, int j, int k);

    @Shadow public abstract void method_1445(Block block, int i, double d, double e, double f, float g);

    @Shadow public abstract boolean method_4316(AnvilBlock anvilBlock, int i, int j, int k, int l, boolean bl);

    @Shadow public abstract void method_4312(int i);

    @Shadow public abstract void method_1431();

    @Shadow public abstract boolean method_1481(Block block, int i, int j, int k);

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_1458(Block par1Block, int par2, int par3, int par4) {
        int var5 = par1Block.getBlockType();
        par1Block.method_425(this.field_2017, par2, par3, par4);
        switch(var5) {
            case 0:
                return this.method_1482(par1Block, par2, par3, par4);
            case 1:
                return this.method_1477(par1Block, par2, par3, par4);
            case 2:
                return this.method_1462(par1Block, par2, par3, par4);
            case 3:
                return this.method_1473(par1Block, par2, par3, par4);
            case 4:
                return this.method_1481(par1Block, par2, par3, par4);
            case 5:
                return this.method_1474(par1Block, par2, par3, par4);
            case 6:
                return this.method_1479(par1Block, par2, par3, par4);
            case 7:
                return this.method_1486(par1Block, par2, par3, par4);
            case 8:
                return this.method_1475(par1Block, par2, par3, par4);
            case 9:
                return this.method_1441((RailBlock)par1Block, par2, par3, par4);
            case 10:
                return this.method_4319((StairsBlock)par1Block, par2, par3, par4);
            case 11:
                return this.method_1440((FenceBlock)par1Block, par2, par3, par4);
            case 12:
                return this.method_1469(par1Block, par2, par3, par4);
            case 13:
                return this.method_1484(par1Block, par2, par3, par4);
            case 14:
                return this.method_1488(par1Block, par2, par3, par4);
            case 15:
                return this.method_1489(par1Block, par2, par3, par4);
            case 16:
                return this.method_1459(par1Block, par2, par3, par4, false);
            case 17:
                return this.method_1464(par1Block, par2, par3, par4, true);
            case 18:
                return this.method_1442((PaneBlock)par1Block, par2, par3, par4);
            case 19:
                return this.method_1478(par1Block, par2, par3, par4);
            case 20:
                return this.method_1476(par1Block, par2, par3, par4);
            case 21:
                return this.method_1439((FenceGateBlock)par1Block, par2, par3, par4);
            case 22:
            default:
                return FMLRenderAccessLibrary.renderWorldBlock((class_535)(Object) this, this.field_2017, par2, par3, par4, par1Block, var5);
            case 23:
                return this.method_1480(par1Block, par2, par3, par4);
            case 24:
                return this.method_1436((CauldronBlock)par1Block, par2, par3, par4);
            case 25:
                return this.method_1435((BrewingStandBlock)par1Block, par2, par3, par4);
            case 26:
                return this.method_1487(par1Block, par2, par3, par4);
            case 27:
                return this.method_1438((DragonEggBlock)par1Block, par2, par3, par4);
            case 28:
                return this.method_1437((CocoaBlock)par1Block, par2, par3, par4);
            case 29:
                return this.method_1471(par1Block, par2, par3, par4);
            case 30:
                return this.method_1472(par1Block, par2, par3, par4);
            case 31:
                return this.method_1483(par1Block, par2, par3, par4);
            case 32:
                return this.method_4321((WallBlock)par1Block, par2, par3, par4);
            case 33:
                return this.method_4318((FlowerPotBlock)par1Block, par2, par3, par4);
            case 34:
                return this.method_4317((BeaconBlock)par1Block, par2, par3, par4);
            case 35:
                return this.method_4314((AnvilBlock)par1Block, par2, par3, par4);
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_1488(Block par1Block, int par2, int par3, int par4) {
        Tessellator var5 = Tessellator.INSTANCE;
        int var7 = par1Block.getBedDirection(this.field_2017, par2, par3, par4);
        boolean var8 = par1Block.isBedFoot(this.field_2017, par2, par3, par4);
        float var9 = 0.5F;
        float var10 = 1.0F;
        float var11 = 0.8F;
        float var12 = 0.6F;
        int var25 = par1Block.method_455(this.field_2017, par2, par3, par4);
        var5.method_1411(var25);
        var5.method_1400(var9, var9, var9);
        int var27 = par1Block.method_439(this.field_2017, par2, par3, par4, 0);
        if (this.field_2049 >= 0) {
            var27 = this.field_2049;
        }

        int var28 = (var27 & 15) << 4;
        int var29 = var27 & 240;
        double var30 = (double)((float)var28 / 256.0F);
        double var32 = ((double)(var28 + 16) - 0.01) / 256.0;
        double var34 = (double)((float)var29 / 256.0F);
        double var36 = ((double)(var29 + 16) - 0.01) / 256.0;
        double var38 = (double)par2 + par1Block.boundingBoxMinX;
        double var40 = (double)par2 + par1Block.boundingBoxMaxX;
        double var42 = (double)par3 + par1Block.boundingBoxMinY + 0.1875;
        double var44 = (double)par4 + par1Block.boundingBoxMinZ;
        double var46 = (double)par4 + par1Block.boundingBoxMaxZ;
        var5.method_1399(var38, var42, var46, var30, var36);
        var5.method_1399(var38, var42, var44, var30, var34);
        var5.method_1399(var40, var42, var44, var32, var34);
        var5.method_1399(var40, var42, var46, var32, var36);
        var5.method_1411(par1Block.method_455(this.field_2017, par2, par3 + 1, par4));
        var5.method_1400(var10, var10, var10);
        var27 = par1Block.method_439(this.field_2017, par2, par3, par4, 1);
        if (this.field_2049 >= 0) {
            var27 = this.field_2049;
        }

        var28 = (var27 & 15) << 4;
        var29 = var27 & 240;
        var30 = (double)((float)var28 / 256.0F);
        var32 = ((double)(var28 + 16) - 0.01) / 256.0;
        var34 = (double)((float)var29 / 256.0F);
        var36 = ((double)(var29 + 16) - 0.01) / 256.0;
        var38 = var30;
        var40 = var32;
        var42 = var34;
        var44 = var34;
        var46 = var30;
        double var48 = var32;
        double var50 = var36;
        double var52 = var36;
        if (var7 == 0) {
            var40 = var30;
            var42 = var36;
            var46 = var32;
            var52 = var34;
        } else if (var7 == 2) {
            var38 = var32;
            var44 = var36;
            var48 = var30;
            var50 = var34;
        } else if (var7 == 3) {
            var38 = var32;
            var44 = var36;
            var48 = var30;
            var50 = var34;
            var40 = var30;
            var42 = var36;
            var46 = var32;
            var52 = var34;
        }

        double var54 = (double)par2 + par1Block.boundingBoxMinX;
        double var56 = (double)par2 + par1Block.boundingBoxMaxX;
        double var58 = (double)par3 + par1Block.boundingBoxMaxY;
        double var60 = (double)par4 + par1Block.boundingBoxMinZ;
        double var62 = (double)par4 + par1Block.boundingBoxMaxZ;
        var5.method_1399(var56, var58, var62, var46, var50);
        var5.method_1399(var56, var58, var60, var38, var42);
        var5.method_1399(var54, var58, var60, var40, var44);
        var5.method_1399(var54, var58, var62, var48, var52);
        int var64 = Axis.AXIS_TO_DIRECTION[var7];
        if (var8) {
            var64 = Axis.AXIS_TO_DIRECTION[Axis.OPPOSITE[var7]];
        }

        byte var65 = 4;
        switch(var7) {
            case 0:
                var65 = 5;
                break;
            case 1:
                var65 = 3;
            case 2:
            default:
                break;
            case 3:
                var65 = 2;
        }

        if (var64 != 2 && (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3, par4 - 1, 2))) {
            var5.method_1411(par1Block.boundingBoxMinZ > 0.0 ? var25 : par1Block.method_455(this.field_2017, par2, par3, par4 - 1));
            var5.method_1400(var11, var11, var11);
            this.field_2050 = var65 == 2;
            this.method_1461(par1Block, (double)par2, (double)par3, (double)par4, par1Block.method_439(this.field_2017, par2, par3, par4, 2));
        }

        if (var64 != 3 && (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3, par4 + 1, 3))) {
            var5.method_1411(par1Block.boundingBoxMaxZ < 1.0 ? var25 : par1Block.method_455(this.field_2017, par2, par3, par4 + 1));
            var5.method_1400(var11, var11, var11);
            this.field_2050 = var65 == 3;
            this.method_1465(par1Block, (double)par2, (double)par3, (double)par4, par1Block.method_439(this.field_2017, par2, par3, par4, 3));
        }

        if (var64 != 4 && (this.field_2051 || par1Block.method_447(this.field_2017, par2 - 1, par3, par4, 4))) {
            var5.method_1411(par1Block.boundingBoxMinZ > 0.0 ? var25 : par1Block.method_455(this.field_2017, par2 - 1, par3, par4));
            var5.method_1400(var12, var12, var12);
            this.field_2050 = var65 == 4;
            this.method_1468(par1Block, (double)par2, (double)par3, (double)par4, par1Block.method_439(this.field_2017, par2, par3, par4, 4));
        }

        if (var64 != 5 && (this.field_2051 || par1Block.method_447(this.field_2017, par2 + 1, par3, par4, 5))) {
            var5.method_1411(par1Block.boundingBoxMaxZ < 1.0 ? var25 : par1Block.method_455(this.field_2017, par2 + 1, par3, par4));
            var5.method_1400(var12, var12, var12);
            this.field_2050 = var65 == 5;
            this.method_1470(par1Block, (double)par2, (double)par3, (double)par4, par1Block.method_439(this.field_2017, par2, par3, par4, 5));
        }

        this.field_2050 = false;
        return true;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_1473(Block par1Block, int par2, int par3, int par4) {
        Tessellator var5 = Tessellator.INSTANCE;
        int var6 = par1Block.method_395(0);
        if (this.field_2049 >= 0) {
            var6 = this.field_2049;
        }

        var5.method_1400(1.0F, 1.0F, 1.0F);
        var5.method_1411(par1Block.method_455(this.field_2017, par2, par3, par4));
        int var7 = (var6 & 15) << 4;
        int var8 = var6 & 240;
        double var9 = (double)((float)var7 / 256.0F);
        double var11 = (double)(((float)var7 + 15.99F) / 256.0F);
        double var13 = (double)((float)var8 / 256.0F);
        double var15 = (double)(((float)var8 + 15.99F) / 256.0F);
        float var17 = 1.4F;
        if (!this.field_2017.method_3784(par2, par3 - 1, par4) && !Block.FIRE.canBlockCatchFire(this.field_2017, par2, par3 - 1, par4, ForgeDirection.UP)) {
            float var36 = 0.2F;
            float var19 = 0.0625F;
            if ((par2 + par3 + par4 & 1) == 1) {
                var9 = (double)((float)var7 / 256.0F);
                var11 = (double)(((float)var7 + 15.99F) / 256.0F);
                var13 = (double)((float)(var8 + 16) / 256.0F);
                var15 = (double)(((float)var8 + 15.99F + 16.0F) / 256.0F);
            }

            if ((par2 / 2 + par3 / 2 + par4 / 2 & 1) == 1) {
                double var20 = var11;
                var11 = var9;
                var9 = var20;
            }

            if (Block.FIRE.canBlockCatchFire(this.field_2017, par2 - 1, par3, par4, ForgeDirection.EAST)) {
                var5.method_1399((double)((float)par2 + var36), (double)((float)par3 + var17 + var19), (double)(par4 + 1), var11, var13);
                var5.method_1399((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1), var11, var15);
                var5.method_1399((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
                var5.method_1399((double)((float)par2 + var36), (double)((float)par3 + var17 + var19), (double)(par4 + 0), var9, var13);
                var5.method_1399((double)((float)par2 + var36), (double)((float)par3 + var17 + var19), (double)(par4 + 0), var9, var13);
                var5.method_1399((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
                var5.method_1399((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1), var11, var15);
                var5.method_1399((double)((float)par2 + var36), (double)((float)par3 + var17 + var19), (double)(par4 + 1), var11, var13);
            }

            if (Block.FIRE.canBlockCatchFire(this.field_2017, par2 + 1, par3, par4, ForgeDirection.WEST)) {
                var5.method_1399((double)((float)(par2 + 1) - var36), (double)((float)par3 + var17 + var19), (double)(par4 + 0), var9, var13);
                var5.method_1399((double)(par2 + 1 - 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
                var5.method_1399((double)(par2 + 1 - 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1), var11, var15);
                var5.method_1399((double)((float)(par2 + 1) - var36), (double)((float)par3 + var17 + var19), (double)(par4 + 1), var11, var13);
                var5.method_1399((double)((float)(par2 + 1) - var36), (double)((float)par3 + var17 + var19), (double)(par4 + 1), var11, var13);
                var5.method_1399((double)(par2 + 1 - 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1), var11, var15);
                var5.method_1399((double)(par2 + 1 - 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
                var5.method_1399((double)((float)(par2 + 1) - var36), (double)((float)par3 + var17 + var19), (double)(par4 + 0), var9, var13);
            }

            if (Block.FIRE.canBlockCatchFire(this.field_2017, par2, par3, par4 - 1, ForgeDirection.SOUTH)) {
                var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17 + var19), (double)((float)par4 + var36), var11, var13);
                var5.method_1399((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var11, var15);
                var5.method_1399((double)(par2 + 1), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
                var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17 + var19), (double)((float)par4 + var36), var9, var13);
                var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17 + var19), (double)((float)par4 + var36), var9, var13);
                var5.method_1399((double)(par2 + 1), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var9, var15);
                var5.method_1399((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 0), var11, var15);
                var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17 + var19), (double)((float)par4 + var36), var11, var13);
            }

            if (Block.FIRE.canBlockCatchFire(this.field_2017, par2, par3, par4 + 1, ForgeDirection.NORTH)) {
                var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17 + var19), (double)((float)(par4 + 1) - var36), var9, var13);
                var5.method_1399((double)(par2 + 1), (double)((float)(par3 + 0) + var19), (double)(par4 + 1 - 0), var9, var15);
                var5.method_1399((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1 - 0), var11, var15);
                var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17 + var19), (double)((float)(par4 + 1) - var36), var11, var13);
                var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17 + var19), (double)((float)(par4 + 1) - var36), var11, var13);
                var5.method_1399((double)(par2 + 0), (double)((float)(par3 + 0) + var19), (double)(par4 + 1 - 0), var11, var15);
                var5.method_1399((double)(par2 + 1), (double)((float)(par3 + 0) + var19), (double)(par4 + 1 - 0), var9, var15);
                var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17 + var19), (double)((float)(par4 + 1) - var36), var9, var13);
            }

            if (Block.FIRE.canBlockCatchFire(this.field_2017, par2, par3 + 1, par4, ForgeDirection.DOWN)) {
                double var20 = (double)par2 + 0.5 + 0.5;
                double var22 = (double)par2 + 0.5 - 0.5;
                double var24 = (double)par4 + 0.5 + 0.5;
                double var26 = (double)par4 + 0.5 - 0.5;
                double var28 = (double)par2 + 0.5 - 0.5;
                double var30 = (double)par2 + 0.5 + 0.5;
                double var32 = (double)par4 + 0.5 - 0.5;
                double var34 = (double)par4 + 0.5 + 0.5;
                var9 = (double)((float)var7 / 256.0F);
                var11 = (double)(((float)var7 + 15.99F) / 256.0F);
                var13 = (double)((float)var8 / 256.0F);
                var15 = (double)(((float)var8 + 15.99F) / 256.0F);
                ++par3;
                var17 = -0.2F;
                if ((par2 + par3 + par4 & 1) == 0) {
                    var5.method_1399(var28, (double)((float)par3 + var17), (double)(par4 + 0), var11, var13);
                    var5.method_1399(var20, (double)(par3 + 0), (double)(par4 + 0), var11, var15);
                    var5.method_1399(var20, (double)(par3 + 0), (double)(par4 + 1), var9, var15);
                    var5.method_1399(var28, (double)((float)par3 + var17), (double)(par4 + 1), var9, var13);
                    var9 = (double)((float)var7 / 256.0F);
                    var11 = (double)(((float)var7 + 15.99F) / 256.0F);
                    var13 = (double)((float)(var8 + 16) / 256.0F);
                    var15 = (double)(((float)var8 + 15.99F + 16.0F) / 256.0F);
                    var5.method_1399(var30, (double)((float)par3 + var17), (double)(par4 + 1), var11, var13);
                    var5.method_1399(var22, (double)(par3 + 0), (double)(par4 + 1), var11, var15);
                    var5.method_1399(var22, (double)(par3 + 0), (double)(par4 + 0), var9, var15);
                    var5.method_1399(var30, (double)((float)par3 + var17), (double)(par4 + 0), var9, var13);
                } else {
                    var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17), var34, var11, var13);
                    var5.method_1399((double)(par2 + 0), (double)(par3 + 0), var26, var11, var15);
                    var5.method_1399((double)(par2 + 1), (double)(par3 + 0), var26, var9, var15);
                    var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17), var34, var9, var13);
                    var9 = (double)((float)var7 / 256.0F);
                    var11 = (double)(((float)var7 + 15.99F) / 256.0F);
                    var13 = (double)((float)(var8 + 16) / 256.0F);
                    var15 = (double)(((float)var8 + 15.99F + 16.0F) / 256.0F);
                    var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17), var32, var11, var13);
                    var5.method_1399((double)(par2 + 1), (double)(par3 + 0), var24, var11, var15);
                    var5.method_1399((double)(par2 + 0), (double)(par3 + 0), var24, var9, var15);
                    var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17), var32, var9, var13);
                }
            }
        } else {
            double var18 = (double)par2 + 0.5 + 0.2;
            double var20 = (double)par2 + 0.5 - 0.2;
            double var22 = (double)par4 + 0.5 + 0.2;
            double var24 = (double)par4 + 0.5 - 0.2;
            double var26 = (double)par2 + 0.5 - 0.3;
            double var28 = (double)par2 + 0.5 + 0.3;
            double var30 = (double)par4 + 0.5 - 0.3;
            double var32 = (double)par4 + 0.5 + 0.3;
            var5.method_1399(var26, (double)((float)par3 + var17), (double)(par4 + 1), var11, var13);
            var5.method_1399(var18, (double)(par3 + 0), (double)(par4 + 1), var11, var15);
            var5.method_1399(var18, (double)(par3 + 0), (double)(par4 + 0), var9, var15);
            var5.method_1399(var26, (double)((float)par3 + var17), (double)(par4 + 0), var9, var13);
            var5.method_1399(var28, (double)((float)par3 + var17), (double)(par4 + 0), var11, var13);
            var5.method_1399(var20, (double)(par3 + 0), (double)(par4 + 0), var11, var15);
            var5.method_1399(var20, (double)(par3 + 0), (double)(par4 + 1), var9, var15);
            var5.method_1399(var28, (double)((float)par3 + var17), (double)(par4 + 1), var9, var13);
            var9 = (double)((float)var7 / 256.0F);
            var11 = (double)(((float)var7 + 15.99F) / 256.0F);
            var13 = (double)((float)(var8 + 16) / 256.0F);
            var15 = (double)(((float)var8 + 15.99F + 16.0F) / 256.0F);
            var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17), var32, var11, var13);
            var5.method_1399((double)(par2 + 1), (double)(par3 + 0), var24, var11, var15);
            var5.method_1399((double)(par2 + 0), (double)(par3 + 0), var24, var9, var15);
            var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17), var32, var9, var13);
            var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17), var30, var11, var13);
            var5.method_1399((double)(par2 + 0), (double)(par3 + 0), var22, var11, var15);
            var5.method_1399((double)(par2 + 1), (double)(par3 + 0), var22, var9, var15);
            var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17), var30, var9, var13);
            var18 = (double)par2 + 0.5 - 0.5;
            var20 = (double)par2 + 0.5 + 0.5;
            var22 = (double)par4 + 0.5 - 0.5;
            var24 = (double)par4 + 0.5 + 0.5;
            var26 = (double)par2 + 0.5 - 0.4;
            var28 = (double)par2 + 0.5 + 0.4;
            var30 = (double)par4 + 0.5 - 0.4;
            var32 = (double)par4 + 0.5 + 0.4;
            var5.method_1399(var26, (double)((float)par3 + var17), (double)(par4 + 0), var9, var13);
            var5.method_1399(var18, (double)(par3 + 0), (double)(par4 + 0), var9, var15);
            var5.method_1399(var18, (double)(par3 + 0), (double)(par4 + 1), var11, var15);
            var5.method_1399(var26, (double)((float)par3 + var17), (double)(par4 + 1), var11, var13);
            var5.method_1399(var28, (double)((float)par3 + var17), (double)(par4 + 1), var9, var13);
            var5.method_1399(var20, (double)(par3 + 0), (double)(par4 + 1), var9, var15);
            var5.method_1399(var20, (double)(par3 + 0), (double)(par4 + 0), var11, var15);
            var5.method_1399(var28, (double)((float)par3 + var17), (double)(par4 + 0), var11, var13);
            var9 = (double)((float)var7 / 256.0F);
            var11 = (double)(((float)var7 + 15.99F) / 256.0F);
            var13 = (double)((float)var8 / 256.0F);
            var15 = (double)(((float)var8 + 15.99F) / 256.0F);
            var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17), var32, var9, var13);
            var5.method_1399((double)(par2 + 0), (double)(par3 + 0), var24, var9, var15);
            var5.method_1399((double)(par2 + 1), (double)(par3 + 0), var24, var11, var15);
            var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17), var32, var11, var13);
            var5.method_1399((double)(par2 + 1), (double)((float)par3 + var17), var30, var9, var13);
            var5.method_1399((double)(par2 + 1), (double)(par3 + 0), var22, var9, var15);
            var5.method_1399((double)(par2 + 0), (double)(par3 + 0), var22, var11, var15);
            var5.method_1399((double)(par2 + 0), (double)((float)par3 + var17), var30, var11, var13);
        }

        return true;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_1450(Block par1Block, int par2, int par3, int par4, float par5, float par6, float par7) {
        this.field_2058 = true;
        boolean var8 = false;
        float var9 = this.field_2059;
        float var10 = this.field_2059;
        float var11 = this.field_2059;
        float var12 = this.field_2059;
        boolean var13 = true;
        boolean var14 = true;
        boolean var15 = true;
        boolean var16 = true;
        boolean var17 = true;
        boolean var18 = true;
        this.field_2059 = par1Block.method_465(this.field_2017, par2, par3, par4);
        this.field_2060 = par1Block.method_465(this.field_2017, par2 - 1, par3, par4);
        this.field_2061 = par1Block.method_465(this.field_2017, par2, par3 - 1, par4);
        this.field_2062 = par1Block.method_465(this.field_2017, par2, par3, par4 - 1);
        this.field_2063 = par1Block.method_465(this.field_2017, par2 + 1, par3, par4);
        this.field_2064 = par1Block.method_465(this.field_2017, par2, par3 + 1, par4);
        this.field_2065 = par1Block.method_465(this.field_2017, par2, par3, par4 + 1);
        int var19 = par1Block.method_455(this.field_2017, par2, par3, par4);
        int var20 = var19;
        int var21 = var19;
        int var22 = var19;
        int var23 = var19;
        int var24 = var19;
        int var25 = var19;
        if (par1Block.boundingBoxMinY <= 0.0) {
            var21 = par1Block.method_455(this.field_2017, par2, par3 - 1, par4);
        }

        if (par1Block.boundingBoxMaxY >= 1.0) {
            var24 = par1Block.method_455(this.field_2017, par2, par3 + 1, par4);
        }

        if (par1Block.boundingBoxMinX <= 0.0) {
            var20 = par1Block.method_455(this.field_2017, par2 - 1, par3, par4);
        }

        if (par1Block.boundingBoxMaxX >= 1.0) {
            var23 = par1Block.method_455(this.field_2017, par2 + 1, par3, par4);
        }

        if (par1Block.boundingBoxMinZ <= 0.0) {
            var22 = par1Block.method_455(this.field_2017, par2, par3, par4 - 1);
        }

        if (par1Block.boundingBoxMaxZ >= 1.0) {
            var25 = par1Block.method_455(this.field_2017, par2, par3, par4 + 1);
        }

        Tessellator var26 = Tessellator.INSTANCE;
        var26.method_1411(983055);
        this.field_2009 = Block.field_495[this.field_2017.getBlock(par2 + 1, par3 + 1, par4)];
        this.field_2018 = Block.field_495[this.field_2017.getBlock(par2 + 1, par3 - 1, par4)];
        this.field_2013 = Block.field_495[this.field_2017.getBlock(par2 + 1, par3, par4 + 1)];
        this.field_2015 = Block.field_495[this.field_2017.getBlock(par2 + 1, par3, par4 - 1)];
        this.field_2010 = Block.field_495[this.field_2017.getBlock(par2 - 1, par3 + 1, par4)];
        this.field_2019 = Block.field_495[this.field_2017.getBlock(par2 - 1, par3 - 1, par4)];
        this.field_2012 = Block.field_495[this.field_2017.getBlock(par2 - 1, par3, par4 - 1)];
        this.field_2014 = Block.field_495[this.field_2017.getBlock(par2 - 1, par3, par4 + 1)];
        this.field_2011 = Block.field_495[this.field_2017.getBlock(par2, par3 + 1, par4 + 1)];
        this.field_2008 = Block.field_495[this.field_2017.getBlock(par2, par3 + 1, par4 - 1)];
        this.field_2020 = Block.field_495[this.field_2017.getBlock(par2, par3 - 1, par4 + 1)];
        this.field_2016 = Block.field_495[this.field_2017.getBlock(par2, par3 - 1, par4 - 1)];
        if (par1Block.field_439 == 3) {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
        }

        if (this.field_2049 >= 0) {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3 - 1, par4, 0)) {
            if (this.field_2033 > 0) {
                if (par1Block.boundingBoxMinY <= 0.0) {
                    --par3;
                }

                this.field_1998 = par1Block.method_455(this.field_2017, par2 - 1, par3, par4);
                this.field_2000 = par1Block.method_455(this.field_2017, par2, par3, par4 - 1);
                this.field_2001 = par1Block.method_455(this.field_2017, par2, par3, par4 + 1);
                this.field_2003 = par1Block.method_455(this.field_2017, par2 + 1, par3, par4);
                this.field_2067 = par1Block.method_465(this.field_2017, par2 - 1, par3, par4);
                this.field_2069 = par1Block.method_465(this.field_2017, par2, par3, par4 - 1);
                this.field_2070 = par1Block.method_465(this.field_2017, par2, par3, par4 + 1);
                this.field_1983 = par1Block.method_465(this.field_2017, par2 + 1, par3, par4);
                if (!this.field_2016 && !this.field_2019) {
                    this.field_2066 = this.field_2067;
                    this.field_1997 = this.field_1998;
                } else {
                    this.field_2066 = par1Block.method_465(this.field_2017, par2 - 1, par3, par4 - 1);
                    this.field_1997 = par1Block.method_455(this.field_2017, par2 - 1, par3, par4 - 1);
                }

                if (!this.field_2020 && !this.field_2019) {
                    this.field_2068 = this.field_2067;
                    this.field_1999 = this.field_1998;
                } else {
                    this.field_2068 = par1Block.method_465(this.field_2017, par2 - 1, par3, par4 + 1);
                    this.field_1999 = par1Block.method_455(this.field_2017, par2 - 1, par3, par4 + 1);
                }

                if (!this.field_2016 && !this.field_2018) {
                    this.field_2071 = this.field_1983;
                    this.field_2002 = this.field_2003;
                } else {
                    this.field_2071 = par1Block.method_465(this.field_2017, par2 + 1, par3, par4 - 1);
                    this.field_2002 = par1Block.method_455(this.field_2017, par2 + 1, par3, par4 - 1);
                }

                if (!this.field_2020 && !this.field_2018) {
                    this.field_1984 = this.field_1983;
                    this.field_2004 = this.field_2003;
                } else {
                    this.field_1984 = par1Block.method_465(this.field_2017, par2 + 1, par3, par4 + 1);
                    this.field_2004 = par1Block.method_455(this.field_2017, par2 + 1, par3, par4 + 1);
                }

                if (par1Block.boundingBoxMinY <= 0.0) {
                    ++par3;
                }

                var9 = (this.field_2068 + this.field_2067 + this.field_2070 + this.field_2061) / 4.0F;
                var12 = (this.field_2070 + this.field_2061 + this.field_1984 + this.field_1983) / 4.0F;
                var11 = (this.field_2061 + this.field_2069 + this.field_1983 + this.field_2071) / 4.0F;
                var10 = (this.field_2067 + this.field_2066 + this.field_2061 + this.field_2069) / 4.0F;
                this.field_2034 = this.method_1433(this.field_1999, this.field_1998, this.field_2001, var21);
                this.field_2037 = this.method_1433(this.field_2001, this.field_2004, this.field_2003, var21);
                this.field_2036 = this.method_1433(this.field_2000, this.field_2003, this.field_2002, var21);
                this.field_2035 = this.method_1433(this.field_1998, this.field_1997, this.field_2000, var21);
            } else {
                var12 = this.field_2061;
                var11 = this.field_2061;
                var10 = this.field_2061;
                var9 = this.field_2061;
                this.field_2034 = this.field_2035 = this.field_2036 = this.field_2037 = this.field_1998;
            }

            this.field_2038 = this.field_2039 = this.field_2040 = this.field_2041 = (var13 ? par5 : 1.0F) * 0.5F;
            this.field_2042 = this.field_2043 = this.field_2044 = this.field_2045 = (var13 ? par6 : 1.0F) * 0.5F;
            this.field_2046 = this.field_2005 = this.field_2006 = this.field_2007 = (var13 ? par7 : 1.0F) * 0.5F;
            this.field_2038 *= var9;
            this.field_2042 *= var9;
            this.field_2046 *= var9;
            this.field_2039 *= var10;
            this.field_2043 *= var10;
            this.field_2005 *= var10;
            this.field_2040 *= var11;
            this.field_2044 *= var11;
            this.field_2006 *= var11;
            this.field_2041 *= var12;
            this.field_2045 *= var12;
            this.field_2007 *= var12;
            this.method_1444(par1Block, (double)par2, (double)par3, (double)par4, par1Block.method_439(this.field_2017, par2, par3, par4, 0));
            var8 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3 + 1, par4, 1)) {
            if (this.field_2033 > 0) {
                if (par1Block.boundingBoxMaxY >= 1.0) {
                    ++par3;
                }

                this.field_2022 = par1Block.method_455(this.field_2017, par2 - 1, par3, par4);
                this.field_2026 = par1Block.method_455(this.field_2017, par2 + 1, par3, par4);
                this.field_2024 = par1Block.method_455(this.field_2017, par2, par3, par4 - 1);
                this.field_2027 = par1Block.method_455(this.field_2017, par2, par3, par4 + 1);
                this.field_1986 = par1Block.method_465(this.field_2017, par2 - 1, par3, par4);
                this.field_1990 = par1Block.method_465(this.field_2017, par2 + 1, par3, par4);
                this.field_1988 = par1Block.method_465(this.field_2017, par2, par3, par4 - 1);
                this.field_1991 = par1Block.method_465(this.field_2017, par2, par3, par4 + 1);
                if (!this.field_2008 && !this.field_2010) {
                    this.field_1985 = this.field_1986;
                    this.field_2021 = this.field_2022;
                } else {
                    this.field_1985 = par1Block.method_465(this.field_2017, par2 - 1, par3, par4 - 1);
                    this.field_2021 = par1Block.method_455(this.field_2017, par2 - 1, par3, par4 - 1);
                }

                if (!this.field_2008 && !this.field_2009) {
                    this.field_1989 = this.field_1990;
                    this.field_2025 = this.field_2026;
                } else {
                    this.field_1989 = par1Block.method_465(this.field_2017, par2 + 1, par3, par4 - 1);
                    this.field_2025 = par1Block.method_455(this.field_2017, par2 + 1, par3, par4 - 1);
                }

                if (!this.field_2011 && !this.field_2010) {
                    this.field_1987 = this.field_1986;
                    this.field_2023 = this.field_2022;
                } else {
                    this.field_1987 = par1Block.method_465(this.field_2017, par2 - 1, par3, par4 + 1);
                    this.field_2023 = par1Block.method_455(this.field_2017, par2 - 1, par3, par4 + 1);
                }

                if (!this.field_2011 && !this.field_2009) {
                    this.field_1992 = this.field_1990;
                    this.field_2028 = this.field_2026;
                } else {
                    this.field_1992 = par1Block.method_465(this.field_2017, par2 + 1, par3, par4 + 1);
                    this.field_2028 = par1Block.method_455(this.field_2017, par2 + 1, par3, par4 + 1);
                }

                if (par1Block.boundingBoxMaxY >= 1.0) {
                    --par3;
                }

                var12 = (this.field_1987 + this.field_1986 + this.field_1991 + this.field_2064) / 4.0F;
                var9 = (this.field_1991 + this.field_2064 + this.field_1992 + this.field_1990) / 4.0F;
                var10 = (this.field_2064 + this.field_1988 + this.field_1990 + this.field_1989) / 4.0F;
                var11 = (this.field_1986 + this.field_1985 + this.field_2064 + this.field_1988) / 4.0F;
                this.field_2037 = this.method_1433(this.field_2023, this.field_2022, this.field_2027, var24);
                this.field_2034 = this.method_1433(this.field_2027, this.field_2028, this.field_2026, var24);
                this.field_2035 = this.method_1433(this.field_2024, this.field_2026, this.field_2025, var24);
                this.field_2036 = this.method_1433(this.field_2022, this.field_2021, this.field_2024, var24);
            } else {
                var12 = this.field_2064;
                var11 = this.field_2064;
                var10 = this.field_2064;
                var9 = this.field_2064;
                this.field_2034 = this.field_2035 = this.field_2036 = this.field_2037 = var24;
            }

            this.field_2038 = this.field_2039 = this.field_2040 = this.field_2041 = var14 ? par5 : 1.0F;
            this.field_2042 = this.field_2043 = this.field_2044 = this.field_2045 = var14 ? par6 : 1.0F;
            this.field_2046 = this.field_2005 = this.field_2006 = this.field_2007 = var14 ? par7 : 1.0F;
            this.field_2038 *= var9;
            this.field_2042 *= var9;
            this.field_2046 *= var9;
            this.field_2039 *= var10;
            this.field_2043 *= var10;
            this.field_2005 *= var10;
            this.field_2040 *= var11;
            this.field_2044 *= var11;
            this.field_2006 *= var11;
            this.field_2041 *= var12;
            this.field_2045 *= var12;
            this.field_2007 *= var12;
            this.method_1456(par1Block, (double)par2, (double)par3, (double)par4, par1Block.method_439(this.field_2017, par2, par3, par4, 1));
            var8 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3, par4 - 1, 2)) {
            if (this.field_2033 > 0) {
                if (par1Block.boundingBoxMinZ <= 0.0) {
                    --par4;
                }

                this.field_1993 = par1Block.method_465(this.field_2017, par2 - 1, par3, par4);
                this.field_2069 = par1Block.method_465(this.field_2017, par2, par3 - 1, par4);
                this.field_1988 = par1Block.method_465(this.field_2017, par2, par3 + 1, par4);
                this.field_1994 = par1Block.method_465(this.field_2017, par2 + 1, par3, par4);
                this.field_2029 = par1Block.method_455(this.field_2017, par2 - 1, par3, par4);
                this.field_2000 = par1Block.method_455(this.field_2017, par2, par3 - 1, par4);
                this.field_2024 = par1Block.method_455(this.field_2017, par2, par3 + 1, par4);
                this.field_2030 = par1Block.method_455(this.field_2017, par2 + 1, par3, par4);
                if (!this.field_2012 && !this.field_2016) {
                    this.field_2066 = this.field_1993;
                    this.field_1997 = this.field_2029;
                } else {
                    this.field_2066 = par1Block.method_465(this.field_2017, par2 - 1, par3 - 1, par4);
                    this.field_1997 = par1Block.method_455(this.field_2017, par2 - 1, par3 - 1, par4);
                }

                if (!this.field_2012 && !this.field_2008) {
                    this.field_1985 = this.field_1993;
                    this.field_2021 = this.field_2029;
                } else {
                    this.field_1985 = par1Block.method_465(this.field_2017, par2 - 1, par3 + 1, par4);
                    this.field_2021 = par1Block.method_455(this.field_2017, par2 - 1, par3 + 1, par4);
                }

                if (!this.field_2015 && !this.field_2016) {
                    this.field_2071 = this.field_1994;
                    this.field_2002 = this.field_2030;
                } else {
                    this.field_2071 = par1Block.method_465(this.field_2017, par2 + 1, par3 - 1, par4);
                    this.field_2002 = par1Block.method_455(this.field_2017, par2 + 1, par3 - 1, par4);
                }

                if (!this.field_2015 && !this.field_2008) {
                    this.field_1989 = this.field_1994;
                    this.field_2025 = this.field_2030;
                } else {
                    this.field_1989 = par1Block.method_465(this.field_2017, par2 + 1, par3 + 1, par4);
                    this.field_2025 = par1Block.method_455(this.field_2017, par2 + 1, par3 + 1, par4);
                }

                if (par1Block.boundingBoxMinZ <= 0.0) {
                    ++par4;
                }

                var9 = (this.field_1993 + this.field_1985 + this.field_2062 + this.field_1988) / 4.0F;
                var10 = (this.field_2062 + this.field_1988 + this.field_1994 + this.field_1989) / 4.0F;
                var11 = (this.field_2069 + this.field_2062 + this.field_2071 + this.field_1994) / 4.0F;
                var12 = (this.field_2066 + this.field_1993 + this.field_2069 + this.field_2062) / 4.0F;
                this.field_2034 = this.method_1433(this.field_2029, this.field_2021, this.field_2024, var22);
                this.field_2035 = this.method_1433(this.field_2024, this.field_2030, this.field_2025, var22);
                this.field_2036 = this.method_1433(this.field_2000, this.field_2002, this.field_2030, var22);
                this.field_2037 = this.method_1433(this.field_1997, this.field_2029, this.field_2000, var22);
            } else {
                var12 = this.field_2062;
                var11 = this.field_2062;
                var10 = this.field_2062;
                var9 = this.field_2062;
                this.field_2034 = this.field_2035 = this.field_2036 = this.field_2037 = var22;
            }

            this.field_2038 = this.field_2039 = this.field_2040 = this.field_2041 = (var15 ? par5 : 1.0F) * 0.8F;
            this.field_2042 = this.field_2043 = this.field_2044 = this.field_2045 = (var15 ? par6 : 1.0F) * 0.8F;
            this.field_2046 = this.field_2005 = this.field_2006 = this.field_2007 = (var15 ? par7 : 1.0F) * 0.8F;
            this.field_2038 *= var9;
            this.field_2042 *= var9;
            this.field_2046 *= var9;
            this.field_2039 *= var10;
            this.field_2043 *= var10;
            this.field_2005 *= var10;
            this.field_2040 *= var11;
            this.field_2044 *= var11;
            this.field_2006 *= var11;
            this.field_2041 *= var12;
            this.field_2045 *= var12;
            this.field_2007 *= var12;
            int var27 = par1Block.method_439(this.field_2017, par2, par3, par4, 2);
            this.method_1461(par1Block, (double)par2, (double)par3, (double)par4, var27);
            if (Tessellator.INSTANCE.defaultTexture && field_2047 && var27 == 3 && this.field_2049 < 0) {
                this.field_2038 *= par5;
                this.field_2039 *= par5;
                this.field_2040 *= par5;
                this.field_2041 *= par5;
                this.field_2042 *= par6;
                this.field_2043 *= par6;
                this.field_2044 *= par6;
                this.field_2045 *= par6;
                this.field_2046 *= par7;
                this.field_2005 *= par7;
                this.field_2006 *= par7;
                this.field_2007 *= par7;
                this.method_1461(par1Block, (double)par2, (double)par3, (double)par4, 38);
            }

            var8 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3, par4 + 1, 3)) {
            if (this.field_2033 > 0) {
                if (par1Block.boundingBoxMaxZ >= 1.0) {
                    ++par4;
                }

                this.field_1995 = par1Block.method_465(this.field_2017, par2 - 1, par3, par4);
                this.field_1996 = par1Block.method_465(this.field_2017, par2 + 1, par3, par4);
                this.field_2070 = par1Block.method_465(this.field_2017, par2, par3 - 1, par4);
                this.field_1991 = par1Block.method_465(this.field_2017, par2, par3 + 1, par4);
                this.field_2031 = par1Block.method_455(this.field_2017, par2 - 1, par3, par4);
                this.field_2032 = par1Block.method_455(this.field_2017, par2 + 1, par3, par4);
                this.field_2001 = par1Block.method_455(this.field_2017, par2, par3 - 1, par4);
                this.field_2027 = par1Block.method_455(this.field_2017, par2, par3 + 1, par4);
                if (!this.field_2014 && !this.field_2020) {
                    this.field_2068 = this.field_1995;
                    this.field_1999 = this.field_2031;
                } else {
                    this.field_2068 = par1Block.method_465(this.field_2017, par2 - 1, par3 - 1, par4);
                    this.field_1999 = par1Block.method_455(this.field_2017, par2 - 1, par3 - 1, par4);
                }

                if (!this.field_2014 && !this.field_2011) {
                    this.field_1987 = this.field_1995;
                    this.field_2023 = this.field_2031;
                } else {
                    this.field_1987 = par1Block.method_465(this.field_2017, par2 - 1, par3 + 1, par4);
                    this.field_2023 = par1Block.method_455(this.field_2017, par2 - 1, par3 + 1, par4);
                }

                if (!this.field_2013 && !this.field_2020) {
                    this.field_1984 = this.field_1996;
                    this.field_2004 = this.field_2032;
                } else {
                    this.field_1984 = par1Block.method_465(this.field_2017, par2 + 1, par3 - 1, par4);
                    this.field_2004 = par1Block.method_455(this.field_2017, par2 + 1, par3 - 1, par4);
                }

                if (!this.field_2013 && !this.field_2011) {
                    this.field_1992 = this.field_1996;
                    this.field_2028 = this.field_2032;
                } else {
                    this.field_1992 = par1Block.method_465(this.field_2017, par2 + 1, par3 + 1, par4);
                    this.field_2028 = par1Block.method_455(this.field_2017, par2 + 1, par3 + 1, par4);
                }

                if (par1Block.boundingBoxMaxZ >= 1.0) {
                    --par4;
                }

                var9 = (this.field_1995 + this.field_1987 + this.field_2065 + this.field_1991) / 4.0F;
                var12 = (this.field_2065 + this.field_1991 + this.field_1996 + this.field_1992) / 4.0F;
                var11 = (this.field_2070 + this.field_2065 + this.field_1984 + this.field_1996) / 4.0F;
                var10 = (this.field_2068 + this.field_1995 + this.field_2070 + this.field_2065) / 4.0F;
                this.field_2034 = this.method_1433(this.field_2031, this.field_2023, this.field_2027, var25);
                this.field_2037 = this.method_1433(this.field_2027, this.field_2032, this.field_2028, var25);
                this.field_2036 = this.method_1433(this.field_2001, this.field_2004, this.field_2032, var25);
                this.field_2035 = this.method_1433(this.field_1999, this.field_2031, this.field_2001, var25);
            } else {
                var12 = this.field_2065;
                var11 = this.field_2065;
                var10 = this.field_2065;
                var9 = this.field_2065;
                this.field_2034 = this.field_2035 = this.field_2036 = this.field_2037 = var25;
            }

            this.field_2038 = this.field_2039 = this.field_2040 = this.field_2041 = (var16 ? par5 : 1.0F) * 0.8F;
            this.field_2042 = this.field_2043 = this.field_2044 = this.field_2045 = (var16 ? par6 : 1.0F) * 0.8F;
            this.field_2046 = this.field_2005 = this.field_2006 = this.field_2007 = (var16 ? par7 : 1.0F) * 0.8F;
            this.field_2038 *= var9;
            this.field_2042 *= var9;
            this.field_2046 *= var9;
            this.field_2039 *= var10;
            this.field_2043 *= var10;
            this.field_2005 *= var10;
            this.field_2040 *= var11;
            this.field_2044 *= var11;
            this.field_2006 *= var11;
            this.field_2041 *= var12;
            this.field_2045 *= var12;
            this.field_2007 *= var12;
            int var27 = par1Block.method_439(this.field_2017, par2, par3, par4, 3);
            this.method_1465(par1Block, (double)par2, (double)par3, (double)par4, par1Block.method_439(this.field_2017, par2, par3, par4, 3));
            if (Tessellator.INSTANCE.defaultTexture && field_2047 && var27 == 3 && this.field_2049 < 0) {
                this.field_2038 *= par5;
                this.field_2039 *= par5;
                this.field_2040 *= par5;
                this.field_2041 *= par5;
                this.field_2042 *= par6;
                this.field_2043 *= par6;
                this.field_2044 *= par6;
                this.field_2045 *= par6;
                this.field_2046 *= par7;
                this.field_2005 *= par7;
                this.field_2006 *= par7;
                this.field_2007 *= par7;
                this.method_1465(par1Block, (double)par2, (double)par3, (double)par4, 38);
            }

            var8 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2 - 1, par3, par4, 4)) {
            if (this.field_2033 > 0) {
                if (par1Block.boundingBoxMinX <= 0.0) {
                    --par2;
                }

                this.field_2067 = par1Block.method_465(this.field_2017, par2, par3 - 1, par4);
                this.field_1993 = par1Block.method_465(this.field_2017, par2, par3, par4 - 1);
                this.field_1995 = par1Block.method_465(this.field_2017, par2, par3, par4 + 1);
                this.field_1986 = par1Block.method_465(this.field_2017, par2, par3 + 1, par4);
                this.field_1998 = par1Block.method_455(this.field_2017, par2, par3 - 1, par4);
                this.field_2029 = par1Block.method_455(this.field_2017, par2, par3, par4 - 1);
                this.field_2031 = par1Block.method_455(this.field_2017, par2, par3, par4 + 1);
                this.field_2022 = par1Block.method_455(this.field_2017, par2, par3 + 1, par4);
                if (!this.field_2012 && !this.field_2019) {
                    this.field_2066 = this.field_1993;
                    this.field_1997 = this.field_2029;
                } else {
                    this.field_2066 = par1Block.method_465(this.field_2017, par2, par3 - 1, par4 - 1);
                    this.field_1997 = par1Block.method_455(this.field_2017, par2, par3 - 1, par4 - 1);
                }

                if (!this.field_2014 && !this.field_2019) {
                    this.field_2068 = this.field_1995;
                    this.field_1999 = this.field_2031;
                } else {
                    this.field_2068 = par1Block.method_465(this.field_2017, par2, par3 - 1, par4 + 1);
                    this.field_1999 = par1Block.method_455(this.field_2017, par2, par3 - 1, par4 + 1);
                }

                if (!this.field_2012 && !this.field_2010) {
                    this.field_1985 = this.field_1993;
                    this.field_2021 = this.field_2029;
                } else {
                    this.field_1985 = par1Block.method_465(this.field_2017, par2, par3 + 1, par4 - 1);
                    this.field_2021 = par1Block.method_455(this.field_2017, par2, par3 + 1, par4 - 1);
                }

                if (!this.field_2014 && !this.field_2010) {
                    this.field_1987 = this.field_1995;
                    this.field_2023 = this.field_2031;
                } else {
                    this.field_1987 = par1Block.method_465(this.field_2017, par2, par3 + 1, par4 + 1);
                    this.field_2023 = par1Block.method_455(this.field_2017, par2, par3 + 1, par4 + 1);
                }

                if (par1Block.boundingBoxMinX <= 0.0) {
                    ++par2;
                }

                var12 = (this.field_2067 + this.field_2068 + this.field_2060 + this.field_1995) / 4.0F;
                var9 = (this.field_2060 + this.field_1995 + this.field_1986 + this.field_1987) / 4.0F;
                var10 = (this.field_1993 + this.field_2060 + this.field_1985 + this.field_1986) / 4.0F;
                var11 = (this.field_2066 + this.field_2067 + this.field_1993 + this.field_2060) / 4.0F;
                this.field_2037 = this.method_1433(this.field_1998, this.field_1999, this.field_2031, var20);
                this.field_2034 = this.method_1433(this.field_2031, this.field_2022, this.field_2023, var20);
                this.field_2035 = this.method_1433(this.field_2029, this.field_2021, this.field_2022, var20);
                this.field_2036 = this.method_1433(this.field_1997, this.field_1998, this.field_2029, var20);
            } else {
                var12 = this.field_2060;
                var11 = this.field_2060;
                var10 = this.field_2060;
                var9 = this.field_2060;
                this.field_2034 = this.field_2035 = this.field_2036 = this.field_2037 = var20;
            }

            this.field_2038 = this.field_2039 = this.field_2040 = this.field_2041 = (var17 ? par5 : 1.0F) * 0.6F;
            this.field_2042 = this.field_2043 = this.field_2044 = this.field_2045 = (var17 ? par6 : 1.0F) * 0.6F;
            this.field_2046 = this.field_2005 = this.field_2006 = this.field_2007 = (var17 ? par7 : 1.0F) * 0.6F;
            this.field_2038 *= var9;
            this.field_2042 *= var9;
            this.field_2046 *= var9;
            this.field_2039 *= var10;
            this.field_2043 *= var10;
            this.field_2005 *= var10;
            this.field_2040 *= var11;
            this.field_2044 *= var11;
            this.field_2006 *= var11;
            this.field_2041 *= var12;
            this.field_2045 *= var12;
            this.field_2007 *= var12;
            int var27 = par1Block.method_439(this.field_2017, par2, par3, par4, 4);
            this.method_1468(par1Block, (double)par2, (double)par3, (double)par4, var27);
            if (Tessellator.INSTANCE.defaultTexture && field_2047 && var27 == 3 && this.field_2049 < 0) {
                this.field_2038 *= par5;
                this.field_2039 *= par5;
                this.field_2040 *= par5;
                this.field_2041 *= par5;
                this.field_2042 *= par6;
                this.field_2043 *= par6;
                this.field_2044 *= par6;
                this.field_2045 *= par6;
                this.field_2046 *= par7;
                this.field_2005 *= par7;
                this.field_2006 *= par7;
                this.field_2007 *= par7;
                this.method_1468(par1Block, (double)par2, (double)par3, (double)par4, 38);
            }

            var8 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2 + 1, par3, par4, 5)) {
            if (this.field_2033 > 0) {
                if (par1Block.boundingBoxMaxX >= 1.0) {
                    ++par2;
                }

                this.field_1983 = par1Block.method_465(this.field_2017, par2, par3 - 1, par4);
                this.field_1994 = par1Block.method_465(this.field_2017, par2, par3, par4 - 1);
                this.field_1996 = par1Block.method_465(this.field_2017, par2, par3, par4 + 1);
                this.field_1990 = par1Block.method_465(this.field_2017, par2, par3 + 1, par4);
                this.field_2003 = par1Block.method_455(this.field_2017, par2, par3 - 1, par4);
                this.field_2030 = par1Block.method_455(this.field_2017, par2, par3, par4 - 1);
                this.field_2032 = par1Block.method_455(this.field_2017, par2, par3, par4 + 1);
                this.field_2026 = par1Block.method_455(this.field_2017, par2, par3 + 1, par4);
                if (!this.field_2018 && !this.field_2015) {
                    this.field_2071 = this.field_1994;
                    this.field_2002 = this.field_2030;
                } else {
                    this.field_2071 = par1Block.method_465(this.field_2017, par2, par3 - 1, par4 - 1);
                    this.field_2002 = par1Block.method_455(this.field_2017, par2, par3 - 1, par4 - 1);
                }

                if (!this.field_2018 && !this.field_2013) {
                    this.field_1984 = this.field_1996;
                    this.field_2004 = this.field_2032;
                } else {
                    this.field_1984 = par1Block.method_465(this.field_2017, par2, par3 - 1, par4 + 1);
                    this.field_2004 = par1Block.method_455(this.field_2017, par2, par3 - 1, par4 + 1);
                }

                if (!this.field_2009 && !this.field_2015) {
                    this.field_1989 = this.field_1994;
                    this.field_2025 = this.field_2030;
                } else {
                    this.field_1989 = par1Block.method_465(this.field_2017, par2, par3 + 1, par4 - 1);
                    this.field_2025 = par1Block.method_455(this.field_2017, par2, par3 + 1, par4 - 1);
                }

                if (!this.field_2009 && !this.field_2013) {
                    this.field_1992 = this.field_1996;
                    this.field_2028 = this.field_2032;
                } else {
                    this.field_1992 = par1Block.method_465(this.field_2017, par2, par3 + 1, par4 + 1);
                    this.field_2028 = par1Block.method_455(this.field_2017, par2, par3 + 1, par4 + 1);
                }

                if (par1Block.boundingBoxMaxX >= 1.0) {
                    --par2;
                }

                var9 = (this.field_1983 + this.field_1984 + this.field_2063 + this.field_1996) / 4.0F;
                var12 = (this.field_2063 + this.field_1996 + this.field_1990 + this.field_1992) / 4.0F;
                var11 = (this.field_1994 + this.field_2063 + this.field_1989 + this.field_1990) / 4.0F;
                var10 = (this.field_2071 + this.field_1983 + this.field_1994 + this.field_2063) / 4.0F;
                this.field_2034 = this.method_1433(this.field_2003, this.field_2004, this.field_2032, var23);
                this.field_2037 = this.method_1433(this.field_2032, this.field_2026, this.field_2028, var23);
                this.field_2036 = this.method_1433(this.field_2030, this.field_2025, this.field_2026, var23);
                this.field_2035 = this.method_1433(this.field_2002, this.field_2003, this.field_2030, var23);
            } else {
                var12 = this.field_2063;
                var11 = this.field_2063;
                var10 = this.field_2063;
                var9 = this.field_2063;
                this.field_2034 = this.field_2035 = this.field_2036 = this.field_2037 = var23;
            }

            this.field_2038 = this.field_2039 = this.field_2040 = this.field_2041 = (var18 ? par5 : 1.0F) * 0.6F;
            this.field_2042 = this.field_2043 = this.field_2044 = this.field_2045 = (var18 ? par6 : 1.0F) * 0.6F;
            this.field_2046 = this.field_2005 = this.field_2006 = this.field_2007 = (var18 ? par7 : 1.0F) * 0.6F;
            this.field_2038 *= var9;
            this.field_2042 *= var9;
            this.field_2046 *= var9;
            this.field_2039 *= var10;
            this.field_2043 *= var10;
            this.field_2005 *= var10;
            this.field_2040 *= var11;
            this.field_2044 *= var11;
            this.field_2006 *= var11;
            this.field_2041 *= var12;
            this.field_2045 *= var12;
            this.field_2007 *= var12;
            int var27 = par1Block.method_439(this.field_2017, par2, par3, par4, 5);
            this.method_1470(par1Block, (double)par2, (double)par3, (double)par4, var27);
            if (Tessellator.INSTANCE.defaultTexture && field_2047 && var27 == 3 && this.field_2049 < 0) {
                this.field_2038 *= par5;
                this.field_2039 *= par5;
                this.field_2040 *= par5;
                this.field_2041 *= par5;
                this.field_2042 *= par6;
                this.field_2043 *= par6;
                this.field_2044 *= par6;
                this.field_2045 *= par6;
                this.field_2046 *= par7;
                this.field_2005 *= par7;
                this.field_2006 *= par7;
                this.field_2007 *= par7;
                this.method_1470(par1Block, (double)par2, (double)par3, (double)par4, 38);
            }

            var8 = true;
        }

        this.field_2058 = false;
        return var8;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public boolean method_1463(Block par1Block, int par2, int par3, int par4, float par5, float par6, float par7) {
        this.field_2058 = false;
        Tessellator var8 = Tessellator.INSTANCE;
        boolean var9 = false;
        float var10 = 0.5F;
        float var11 = 1.0F;
        float var12 = 0.8F;
        float var13 = 0.6F;
        float var14 = var11 * par5;
        float var15 = var11 * par6;
        float var16 = var11 * par7;
        float var17 = var10;
        float var18 = var12;
        float var19 = var13;
        float var20 = var10;
        float var21 = var12;
        float var22 = var13;
        float var23 = var10;
        float var24 = var12;
        float var25 = var13;
        if (par1Block != Block.GRASS_BLOCK) {
            var17 = var10 * par5;
            var18 = var12 * par5;
            var19 = var13 * par5;
            var20 = var10 * par6;
            var21 = var12 * par6;
            var22 = var13 * par6;
            var23 = var10 * par7;
            var24 = var12 * par7;
            var25 = var13 * par7;
        }

        int var26 = par1Block.method_455(this.field_2017, par2, par3, par4);
        if (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3 - 1, par4, 0)) {
            var8.method_1411(par1Block.boundingBoxMinY > 0.0 ? var26 : par1Block.method_455(this.field_2017, par2, par3 - 1, par4));
            var8.method_1400(var17, var20, var23);
            this.method_1444(par1Block, (double)par2, (double)par3, (double)par4, par1Block.method_439(this.field_2017, par2, par3, par4, 0));
            var9 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3 + 1, par4, 1)) {
            var8.method_1411(par1Block.boundingBoxMaxY < 1.0 ? var26 : par1Block.method_455(this.field_2017, par2, par3 + 1, par4));
            var8.method_1400(var14, var15, var16);
            this.method_1456(par1Block, (double)par2, (double)par3, (double)par4, par1Block.method_439(this.field_2017, par2, par3, par4, 1));
            var9 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3, par4 - 1, 2)) {
            var8.method_1411(par1Block.boundingBoxMinZ > 0.0 ? var26 : par1Block.method_455(this.field_2017, par2, par3, par4 - 1));
            var8.method_1400(var18, var21, var24);
            int var28 = par1Block.method_439(this.field_2017, par2, par3, par4, 2);
            this.method_1461(par1Block, (double)par2, (double)par3, (double)par4, var28);
            if (Tessellator.INSTANCE.defaultTexture && field_2047 && var28 == 3 && this.field_2049 < 0) {
                var8.method_1400(var18 * par5, var21 * par6, var24 * par7);
                this.method_1461(par1Block, (double)par2, (double)par3, (double)par4, 38);
            }

            var9 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2, par3, par4 + 1, 3)) {
            var8.method_1411(par1Block.boundingBoxMaxZ < 1.0 ? var26 : par1Block.method_455(this.field_2017, par2, par3, par4 + 1));
            var8.method_1400(var18, var21, var24);
            int var28 = par1Block.method_439(this.field_2017, par2, par3, par4, 3);
            this.method_1465(par1Block, (double)par2, (double)par3, (double)par4, var28);
            if (Tessellator.INSTANCE.defaultTexture && field_2047 && var28 == 3 && this.field_2049 < 0) {
                var8.method_1400(var18 * par5, var21 * par6, var24 * par7);
                this.method_1465(par1Block, (double)par2, (double)par3, (double)par4, 38);
            }

            var9 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2 - 1, par3, par4, 4)) {
            var8.method_1411(par1Block.boundingBoxMinX > 0.0 ? var26 : par1Block.method_455(this.field_2017, par2 - 1, par3, par4));
            var8.method_1400(var19, var22, var25);
            int var28 = par1Block.method_439(this.field_2017, par2, par3, par4, 4);
            this.method_1468(par1Block, (double)par2, (double)par3, (double)par4, var28);
            if (Tessellator.INSTANCE.defaultTexture && field_2047 && var28 == 3 && this.field_2049 < 0) {
                var8.method_1400(var19 * par5, var22 * par6, var25 * par7);
                this.method_1468(par1Block, (double)par2, (double)par3, (double)par4, 38);
            }

            var9 = true;
        }

        if (this.field_2051 || par1Block.method_447(this.field_2017, par2 + 1, par3, par4, 5)) {
            var8.method_1411(par1Block.boundingBoxMaxX < 1.0 ? var26 : par1Block.method_455(this.field_2017, par2 + 1, par3, par4));
            var8.method_1400(var19, var22, var25);
            int var28 = par1Block.method_439(this.field_2017, par2, par3, par4, 5);
            this.method_1470(par1Block, (double)par2, (double)par3, (double)par4, var28);
            if (Tessellator.INSTANCE.defaultTexture && field_2047 && var28 == 3 && this.field_2049 < 0) {
                var8.method_1400(var19 * par5, var22 * par6, var25 * par7);
                this.method_1470(par1Block, (double)par2, (double)par3, (double)par4, 38);
            }

            var9 = true;
        }

        return var9;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1447(Block par1Block, int par2, float par3) {
        Tessellator var4 = Tessellator.INSTANCE;
        boolean var5 = par1Block.id == Block.GRASS_BLOCK.id;
        if (this.field_2048) {
            int var6 = par1Block.method_459(par2);
            if (var5) {
                var6 = 16777215;
            }

            float var7 = (float)(var6 >> 16 & 0xFF) / 255.0F;
            float var8 = (float)(var6 >> 8 & 0xFF) / 255.0F;
            float var9 = (float)(var6 & 0xFF) / 255.0F;
            GL11.glColor4f(var7 * par3, var8 * par3, var9 * par3, 1.0F);
        }

        int var6 = par1Block.getBlockType();
        if (var6 != 0 && var6 != 31 && var6 != 16 && var6 != 26) {
            if (var6 == 1) {
                var4.method_1405();
                var4.method_1407(0.0F, -1.0F, 0.0F);
                this.method_1445(par1Block, par2, -0.5, -0.5, -0.5, 1.0F);
                var4.method_1396();
            } else if (var6 == 19) {
                var4.method_1405();
                var4.method_1407(0.0F, -1.0F, 0.0F);
                par1Block.setBlockItemBounds();
                this.method_1446(par1Block, par2, par1Block.boundingBoxMaxY, -0.5, -0.5, -0.5);
                var4.method_1396();
            } else if (var6 == 23) {
                var4.method_1405();
                var4.method_1407(0.0F, -1.0F, 0.0F);
                par1Block.setBlockItemBounds();
                var4.method_1396();
            } else if (var6 == 13) {
                par1Block.setBlockItemBounds();
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                float var7 = 0.0625F;
                var4.method_1405();
                var4.method_1407(0.0F, -1.0F, 0.0F);
                this.method_1444(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(0));
                var4.method_1396();
                var4.method_1405();
                var4.method_1407(0.0F, 1.0F, 0.0F);
                this.method_1456(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(1));
                var4.method_1396();
                var4.method_1405();
                var4.method_1407(0.0F, 0.0F, -1.0F);
                var4.method_1410(0.0F, 0.0F, var7);
                this.method_1461(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(2));
                var4.method_1410(0.0F, 0.0F, -var7);
                var4.method_1396();
                var4.method_1405();
                var4.method_1407(0.0F, 0.0F, 1.0F);
                var4.method_1410(0.0F, 0.0F, -var7);
                this.method_1465(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(3));
                var4.method_1410(0.0F, 0.0F, var7);
                var4.method_1396();
                var4.method_1405();
                var4.method_1407(-1.0F, 0.0F, 0.0F);
                var4.method_1410(var7, 0.0F, 0.0F);
                this.method_1468(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(4));
                var4.method_1410(-var7, 0.0F, 0.0F);
                var4.method_1396();
                var4.method_1405();
                var4.method_1407(1.0F, 0.0F, 0.0F);
                var4.method_1410(-var7, 0.0F, 0.0F);
                this.method_1470(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(5));
                var4.method_1410(var7, 0.0F, 0.0F);
                var4.method_1396();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            } else if (var6 == 22) {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                class_523.field_1809.method_1320(par1Block, par2, par3);
                GL11.glEnable(32826);
            } else if (var6 == 6) {
                var4.method_1405();
                var4.method_1407(0.0F, -1.0F, 0.0F);
                this.method_1457(par1Block, par2, -0.5, -0.5, -0.5);
                var4.method_1396();
            } else if (var6 == 2) {
                var4.method_1405();
                var4.method_1407(0.0F, -1.0F, 0.0F);
                this.method_1443(par1Block, -0.5, -0.5, -0.5, 0.0, 0.0);
                var4.method_1396();
            } else if (var6 == 10) {
                for(int var14 = 0; var14 < 2; ++var14) {
                    if (var14 == 0) {
                        par1Block.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
                    }

                    if (var14 == 1) {
                        par1Block.setBoundingBox(0.0F, 0.0F, 0.5F, 1.0F, 0.5F, 1.0F);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    var4.method_1405();
                    var4.method_1407(0.0F, -1.0F, 0.0F);
                    this.method_1444(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(0));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 1.0F, 0.0F);
                    this.method_1456(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(1));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, -1.0F);
                    this.method_1461(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, 1.0F);
                    this.method_1465(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(3));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(-1.0F, 0.0F, 0.0F);
                    this.method_1468(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(4));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(1.0F, 0.0F, 0.0F);
                    this.method_1470(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(5));
                    var4.method_1396();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            } else if (var6 == 27) {
                int var14 = 0;
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                var4.method_1405();

                for(int var15 = 0; var15 < 8; ++var15) {
                    byte var16 = 0;
                    byte var17 = 1;
                    if (var15 == 0) {
                        var16 = 2;
                    }

                    if (var15 == 1) {
                        var16 = 3;
                    }

                    if (var15 == 2) {
                        var16 = 4;
                    }

                    if (var15 == 3) {
                        var16 = 5;
                        var17 = 2;
                    }

                    if (var15 == 4) {
                        var16 = 6;
                        var17 = 3;
                    }

                    if (var15 == 5) {
                        var16 = 7;
                        var17 = 5;
                    }

                    if (var15 == 6) {
                        var16 = 6;
                        var17 = 2;
                    }

                    if (var15 == 7) {
                        var16 = 3;
                    }

                    float var11 = (float)var16 / 16.0F;
                    float var12 = 1.0F - (float)var14 / 16.0F;
                    float var13 = 1.0F - (float)(var14 + var17) / 16.0F;
                    var14 += var17;
                    par1Block.setBoundingBox(0.5F - var11, var13, 0.5F - var11, 0.5F + var11, var12, 0.5F + var11);
                    var4.method_1407(0.0F, -1.0F, 0.0F);
                    this.method_1444(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(0));
                    var4.method_1407(0.0F, 1.0F, 0.0F);
                    this.method_1456(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(1));
                    var4.method_1407(0.0F, 0.0F, -1.0F);
                    this.method_1461(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(2));
                    var4.method_1407(0.0F, 0.0F, 1.0F);
                    this.method_1465(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(3));
                    var4.method_1407(-1.0F, 0.0F, 0.0F);
                    this.method_1468(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(4));
                    var4.method_1407(1.0F, 0.0F, 0.0F);
                    this.method_1470(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(5));
                }

                var4.method_1396();
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                par1Block.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else if (var6 == 11) {
                for(int var14 = 0; var14 < 4; ++var14) {
                    float var8 = 0.125F;
                    if (var14 == 0) {
                        par1Block.setBoundingBox(0.5F - var8, 0.0F, 0.0F, 0.5F + var8, 1.0F, var8 * 2.0F);
                    }

                    if (var14 == 1) {
                        par1Block.setBoundingBox(0.5F - var8, 0.0F, 1.0F - var8 * 2.0F, 0.5F + var8, 1.0F, 1.0F);
                    }

                    var8 = 0.0625F;
                    if (var14 == 2) {
                        par1Block.setBoundingBox(0.5F - var8, 1.0F - var8 * 3.0F, -var8 * 2.0F, 0.5F + var8, 1.0F - var8, 1.0F + var8 * 2.0F);
                    }

                    if (var14 == 3) {
                        par1Block.setBoundingBox(0.5F - var8, 0.5F - var8 * 3.0F, -var8 * 2.0F, 0.5F + var8, 0.5F - var8, 1.0F + var8 * 2.0F);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    var4.method_1405();
                    var4.method_1407(0.0F, -1.0F, 0.0F);
                    this.method_1444(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(0));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 1.0F, 0.0F);
                    this.method_1456(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(1));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, -1.0F);
                    this.method_1461(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, 1.0F);
                    this.method_1465(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(3));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(-1.0F, 0.0F, 0.0F);
                    this.method_1468(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(4));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(1.0F, 0.0F, 0.0F);
                    this.method_1470(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(5));
                    var4.method_1396();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                par1Block.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else if (var6 == 21) {
                for(int var14 = 0; var14 < 3; ++var14) {
                    float var8 = 0.0625F;
                    if (var14 == 0) {
                        par1Block.setBoundingBox(0.5F - var8, 0.3F, 0.0F, 0.5F + var8, 1.0F, var8 * 2.0F);
                    }

                    if (var14 == 1) {
                        par1Block.setBoundingBox(0.5F - var8, 0.3F, 1.0F - var8 * 2.0F, 0.5F + var8, 1.0F, 1.0F);
                    }

                    var8 = 0.0625F;
                    if (var14 == 2) {
                        par1Block.setBoundingBox(0.5F - var8, 0.5F, 0.0F, 0.5F + var8, 1.0F - var8, 1.0F);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    var4.method_1405();
                    var4.method_1407(0.0F, -1.0F, 0.0F);
                    this.method_1444(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(0));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 1.0F, 0.0F);
                    this.method_1456(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(1));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, -1.0F);
                    this.method_1461(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, 1.0F);
                    this.method_1465(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(3));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(-1.0F, 0.0F, 0.0F);
                    this.method_1468(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(4));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(1.0F, 0.0F, 0.0F);
                    this.method_1470(par1Block, 0.0, 0.0, 0.0, par1Block.method_395(5));
                    var4.method_1396();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }
            } else if (var6 == 32) {
                for(int var14 = 0; var14 < 2; ++var14) {
                    if (var14 == 0) {
                        par1Block.setBoundingBox(0.0F, 0.0F, 0.3125F, 1.0F, 0.8125F, 0.6875F);
                    }

                    if (var14 == 1) {
                        par1Block.setBoundingBox(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    var4.method_1405();
                    var4.method_1407(0.0F, -1.0F, 0.0F);
                    this.method_1444(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(0, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 1.0F, 0.0F);
                    this.method_1456(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(1, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, -1.0F);
                    this.method_1461(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(2, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, 1.0F);
                    this.method_1465(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(3, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(-1.0F, 0.0F, 0.0F);
                    this.method_1468(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(4, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(1.0F, 0.0F, 0.0F);
                    this.method_1470(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(5, par2));
                    var4.method_1396();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                par1Block.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else if (var6 == 35) {
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                this.method_4316((AnvilBlock)par1Block, 0, 0, 0, par2, true);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            } else if (var6 == 34) {
                for(int var14 = 0; var14 < 3; ++var14) {
                    if (var14 == 0) {
                        par1Block.setBoundingBox(0.125F, 0.0F, 0.125F, 0.875F, 0.1875F, 0.875F);
                        this.method_4312(Block.OBSIDIAN.field_439);
                    } else if (var14 == 1) {
                        par1Block.setBoundingBox(0.1875F, 0.1875F, 0.1875F, 0.8125F, 0.875F, 0.8125F);
                        this.method_4312(41);
                    } else if (var14 == 2) {
                        par1Block.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                        this.method_4312(Block.GLASS_BLOCK.field_439);
                    }

                    GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                    var4.method_1405();
                    var4.method_1407(0.0F, -1.0F, 0.0F);
                    this.method_1444(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(0, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 1.0F, 0.0F);
                    this.method_1456(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(1, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, -1.0F);
                    this.method_1461(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(2, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(0.0F, 0.0F, 1.0F);
                    this.method_1465(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(3, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(-1.0F, 0.0F, 0.0F);
                    this.method_1468(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(4, par2));
                    var4.method_1396();
                    var4.method_1405();
                    var4.method_1407(1.0F, 0.0F, 0.0F);
                    this.method_1470(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(5, par2));
                    var4.method_1396();
                    GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                }

                par1Block.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                this.method_1431();
            } else {
                FMLRenderAccessLibrary.renderInventoryBlock((class_535)(Object) this, par1Block, par2, var6);
            }
        } else {
            if (var6 == 16) {
                par2 = 1;
            }

            par1Block.setBlockItemBounds();
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            var4.method_1405();
            var4.method_1407(0.0F, -1.0F, 0.0F);
            this.method_1444(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(0, par2));
            var4.method_1396();
            if (var5 && this.field_2048) {
                int var14 = par1Block.method_459(par2);
                float var8 = (float)(var14 >> 16 & 0xFF) / 255.0F;
                float var9 = (float)(var14 >> 8 & 0xFF) / 255.0F;
                float var10 = (float)(var14 & 0xFF) / 255.0F;
                GL11.glColor4f(var8 * par3, var9 * par3, var10 * par3, 1.0F);
            }

            var4.method_1405();
            var4.method_1407(0.0F, 1.0F, 0.0F);
            this.method_1456(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(1, par2));
            var4.method_1396();
            if (var5 && this.field_2048) {
                GL11.glColor4f(par3, par3, par3, 1.0F);
            }

            var4.method_1405();
            var4.method_1407(0.0F, 0.0F, -1.0F);
            this.method_1461(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(2, par2));
            var4.method_1396();
            var4.method_1405();
            var4.method_1407(0.0F, 0.0F, 1.0F);
            this.method_1465(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(3, par2));
            var4.method_1396();
            var4.method_1405();
            var4.method_1407(-1.0F, 0.0F, 0.0F);
            this.method_1468(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(4, par2));
            var4.method_1396();
            var4.method_1405();
            var4.method_1407(1.0F, 0.0F, 0.0F);
            this.method_1470(par1Block, 0.0, 0.0, 0.0, par1Block.method_396(5, par2));
            var4.method_1396();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static boolean method_1455(int par0) {
        switch(par0) {
            case 0:
                return true;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 12:
            case 14:
            case 15:
            case 17:
            case 18:
            case 19:
            case 20:
            case 23:
            case 24:
            case 25:
            case 28:
            case 29:
            case 30:
            case 33:
            default:
                return FMLRenderAccessLibrary.renderItemAsFull3DBlock(par0);
            case 10:
                return true;
            case 11:
                return true;
            case 13:
                return true;
            case 16:
                return true;
            case 21:
                return true;
            case 22:
                return true;
            case 26:
                return true;
            case 27:
                return true;
            case 31:
                return true;
            case 32:
                return true;
            case 34:
                return true;
            case 35:
                return true;
        }
    }
}
