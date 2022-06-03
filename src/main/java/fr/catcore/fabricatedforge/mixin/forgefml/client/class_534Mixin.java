package fr.catcore.fabricatedforge.mixin.forgefml.client;

import cpw.mods.fml.client.TextureFXManager;
import fr.catcore.fabricatedforge.forged.ReflectionUtils;
import net.minecraft.client.class_534;
import net.minecraft.client.class_584;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.texture.ITexturePack;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TexturePackManager;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

@Mixin(class_534.class)
public abstract class class_534Mixin {

    @Shadow public TexturePackManager packManager;
    @Shadow private HashMap field_1974;

    @Shadow protected abstract int[] method_1427(BufferedImage bufferedImage);

    @Shadow protected abstract BufferedImage method_1429(BufferedImage bufferedImage);

    @Shadow protected abstract BufferedImage readBufferedImage(InputStream stream);

    @Shadow public boolean field_1971;
    @Shadow public boolean field_1972;
    @Shadow private BufferedImage missingTexture;
    @Shadow private HashMap textureCache;
    @Shadow private IntBuffer field_1976;
    @Shadow public List field_1978;
    @Shadow private GameOptions options;
    @Shadow private ByteBuffer field_1977;
    @Shadow private IntObjectStorage field_1975;
    @Shadow private Map field_1979;

    @Shadow protected abstract int[] method_1419(BufferedImage bufferedImage, int[] is);

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int[] method_1421(String par1Str) {
        ITexturePack var2 = this.packManager.getCurrentTexturePack();
        int[] var3 = (int[])this.field_1974.get(par1Str);
        if (var3 != null) {
            return var3;
        } else {
            try {
                Object var4 = null;
                int[] var7;
                if (par1Str.startsWith("##")) {
                    var7 = this.method_1427(this.method_1429(this.readBufferedImage(var2.openStream(par1Str.substring(2)))));
                } else if (par1Str.startsWith("%clamp%")) {
                    this.field_1971 = true;
                    var7 = this.method_1427(this.readBufferedImage(var2.openStream(par1Str.substring(7))));
                    this.field_1971 = false;
                } else if (par1Str.startsWith("%blur%")) {
                    this.field_1972 = true;
                    this.field_1971 = true;
                    var7 = this.method_1427(this.readBufferedImage(var2.openStream(par1Str.substring(6))));
                    this.field_1971 = false;
                    this.field_1972 = false;
                } else {
                    InputStream var8 = var2.openStream(par1Str);
                    if (var8 == null) {
                        var7 = this.method_1427(this.missingTexture);
                    } else {
                        var7 = this.method_1427(this.readBufferedImage(var8));
                    }
                }

                this.field_1974.put(par1Str, var7);
                return var7;
            } catch (Exception var7) {
                ReflectionUtils.class_534_log.log(Level.INFO, String.format("An error occured reading texture file %s (getTexture)", par1Str), var7);
                var7.printStackTrace();
                int[] var5 = this.method_1427(this.missingTexture);
                this.field_1974.put(par1Str, var5);
                return var5;
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int getTextureFromPath(String par1Str) {
        Integer var2 = (Integer)this.textureCache.get(par1Str);
        if (var2 != null) {
            return var2;
        } else {
            ITexturePack var6 = this.packManager.getCurrentTexturePack();

            try {
                ForgeHooksClient.onTextureLoadPre(par1Str);
                this.field_1976.clear();
                GlAllocationUtils.method_850(this.field_1976);
                int var3 = this.field_1976.get(0);
                if (par1Str.startsWith("##")) {
                    this.method_1418(this.method_1429(this.readBufferedImage(var6.openStream(par1Str.substring(2)))), var3);
                } else if (par1Str.startsWith("%clamp%")) {
                    this.field_1971 = true;
                    this.method_1418(this.readBufferedImage(var6.openStream(par1Str.substring(7))), var3);
                    this.field_1971 = false;
                } else if (par1Str.startsWith("%blur%")) {
                    this.field_1972 = true;
                    this.method_1418(this.readBufferedImage(var6.openStream(par1Str.substring(6))), var3);
                    this.field_1972 = false;
                } else if (par1Str.startsWith("%blurclamp%")) {
                    this.field_1972 = true;
                    this.field_1971 = true;
                    this.method_1418(this.readBufferedImage(var6.openStream(par1Str.substring(11))), var3);
                    this.field_1972 = false;
                    this.field_1971 = false;
                } else {
                    InputStream var7 = var6.openStream(par1Str);
                    if (var7 == null) {
                        this.method_1418(this.missingTexture, var3);
                    } else {
                        this.method_1418(this.readBufferedImage(var7), var3);
                    }
                }

                this.textureCache.put(par1Str, var3);
                ForgeHooksClient.onTextureLoad(par1Str, var6);
                return var3;
            } catch (Exception var61) {
                var61.printStackTrace();
                GlAllocationUtils.method_850(this.field_1976);
                int var4 = this.field_1976.get(0);
                this.method_1418(this.missingTexture, var4);
                this.textureCache.put(par1Str, var4);
                return var4;
            }
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1418(BufferedImage par1BufferedImage, int par2) {
        GL11.glBindTexture(3553, par2);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        if (this.field_1972) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        }

        if (this.field_1971) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }

        int var3 = par1BufferedImage.getWidth();
        int var4 = par1BufferedImage.getHeight();
        TextureFXManager.instance().setTextureDimensions(par2, var3, var4, this.field_1978);
        int[] var5 = new int[var3 * var4];
        byte[] var6 = new byte[var3 * var4 * 4];
        par1BufferedImage.getRGB(0, 0, var3, var4, var5, 0, var3);

        for(int var7 = 0; var7 < var5.length; ++var7) {
            int var8 = var5[var7] >> 24 & 0xFF;
            int var9 = var5[var7] >> 16 & 0xFF;
            int var10 = var5[var7] >> 8 & 0xFF;
            int var11 = var5[var7] & 0xFF;
            if (this.options != null && this.options.anaglyph3d) {
                int var12 = (var9 * 30 + var10 * 59 + var11 * 11) / 100;
                int var13 = (var9 * 30 + var10 * 70) / 100;
                int var14 = (var9 * 30 + var11 * 70) / 100;
                var9 = var12;
                var10 = var13;
                var11 = var14;
            }

            var6[var7 * 4 + 0] = (byte)var9;
            var6[var7 * 4 + 1] = (byte)var10;
            var6[var7 * 4 + 2] = (byte)var11;
            var6[var7 * 4 + 3] = (byte)var8;
        }

        this.field_1977.clear();
        this.field_1977.put(var6);
        this.field_1977.position(0).limit(var6.length);
        GL11.glTexImage2D(3553, 0, 6408, var3, var4, 0, 6408, 5121, this.field_1977);
    }

    @Inject(method = "method_1416", at = @At("HEAD"))
    private void FMLonPreRegisterEffect(class_584 par1TextureFX, CallbackInfo ci) {
        TextureFXManager.instance().onPreRegisterEffect(par1TextureFX);
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1414() {
        int var1 = -1;

        for(class_584 var3 : (List<class_584>)this.field_1978) {
            var3.field_2154 = this.options.anaglyph3d;
            if (TextureFXManager.instance().onUpdateTextureEffect(var3)) {
                var1 = this.method_4309(var3, var1);
            }

            var3.method_1613();
            var1 = this.method_4309(var3, var1);
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int method_4309(class_584 par1TextureFX, int par2) {
        Dimension dim = TextureFXManager.instance().getTextureDimensions(par1TextureFX);
        int tWidth = dim.width >> 4;
        int tHeight = dim.height >> 4;
        int tLen = tWidth * tHeight << 2;
        if (par1TextureFX.field_2152.length == tLen) {
            this.field_1977.clear();
            this.field_1977.put(par1TextureFX.field_2152);
            this.field_1977.position(0).limit(par1TextureFX.field_2152.length);
        } else {
            TextureFXManager.instance().scaleTextureFXData(par1TextureFX.field_2152, this.field_1977, tWidth, tLen);
        }

        if (par1TextureFX.field_2153 != par2) {
            par1TextureFX.method_1614((class_534)(Object) this);
            par2 = par1TextureFX.field_2153;
        }

        for(int var3 = 0; var3 < par1TextureFX.field_2156; ++var3) {
            int xOffset = par1TextureFX.field_2153 % 16 * tWidth + var3 * tWidth;

            for(int var4 = 0; var4 < par1TextureFX.field_2156; ++var4) {
                int yOffset = par1TextureFX.field_2153 / 16 * tHeight + var4 * tHeight;
                GL11.glTexSubImage2D(3553, 0, xOffset, yOffset, tWidth, tHeight, 6408, 5121, this.field_1977);
            }
        }

        return par2;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void updateAnaglyph3D() {
        ITexturePack var1 = this.packManager.getCurrentTexturePack();

        for(int var3 : (Set<Integer>)this.field_1975.method_2309()) {
            BufferedImage var4 = (BufferedImage)this.field_1975.get(var3);
            this.method_1418(var4, var3);
        }

        for(PlayerSkinTexture var8 : (Collection<PlayerSkinTexture>)this.field_1979.values()) {
            var8.field_1872 = false;
        }

        for(String var9 : (Set<String>)this.textureCache.keySet()) {
            try {
                BufferedImage var4;
                if (var9.startsWith("##")) {
                    var4 = this.method_1429(this.readBufferedImage(var1.openStream(var9.substring(2))));
                } else if (var9.startsWith("%clamp%")) {
                    this.field_1971 = true;
                    var4 = this.readBufferedImage(var1.openStream(var9.substring(7)));
                } else if (var9.startsWith("%blur%")) {
                    this.field_1972 = true;
                    var4 = this.readBufferedImage(var1.openStream(var9.substring(6)));
                } else if (var9.startsWith("%blurclamp%")) {
                    this.field_1972 = true;
                    this.field_1971 = true;
                    var4 = this.readBufferedImage(var1.openStream(var9.substring(11)));
                } else {
                    var4 = this.readBufferedImage(var1.openStream(var9));
                }

                int var5 = (int) this.textureCache.get(var9);
                this.method_1418(var4, var5);
                this.field_1972 = false;
                this.field_1971 = false;
            } catch (Exception var81) {
                ReflectionUtils.class_534_log.log(Level.INFO, String.format("An error occured reading texture file %s (refreshTexture)", var9), var81);
                var81.printStackTrace();
            }
        }

        for(String var9 : (Set<String>)this.field_1974.keySet()) {
            try {
                BufferedImage var4;
                if (var9.startsWith("##")) {
                    var4 = this.method_1429(this.readBufferedImage(var1.openStream(var9.substring(2))));
                } else if (var9.startsWith("%clamp%")) {
                    this.field_1971 = true;
                    var4 = this.readBufferedImage(var1.openStream(var9.substring(7)));
                } else if (var9.startsWith("%blur%")) {
                    this.field_1972 = true;
                    var4 = this.readBufferedImage(var1.openStream(var9.substring(6)));
                } else {
                    var4 = this.readBufferedImage(var1.openStream(var9));
                }

                this.method_1419(var4, (int[])this.field_1974.get(var9));
                this.field_1972 = false;
                this.field_1971 = false;
            } catch (Exception var7) {
                ReflectionUtils.class_534_log.log(Level.INFO, String.format("An error occured reading texture file data %s (refreshTexture)", var9), var7);
                var7.printStackTrace();
            }
        }

    }
}
