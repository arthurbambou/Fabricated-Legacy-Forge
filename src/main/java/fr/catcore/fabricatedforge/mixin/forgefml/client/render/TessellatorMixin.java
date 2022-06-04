package fr.catcore.fabricatedforge.mixin.forgefml.client.render;

import com.mojang.blaze3d.platform.GLX;
import fr.catcore.fabricatedforge.mixininterface.ITessellator;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.GlAllocationUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

@Mixin(Tessellator.class)
public abstract class TessellatorMixin implements ITessellator {
    @Shadow public boolean field_1970;
    @Shadow private int field_1953;
    @Shadow public int field_1965;
    @Shadow private static boolean field_1946;
    @Shadow private IntBuffer field_1949;
    @Shadow private int[] field_1952;
    @Shadow private ByteBuffer field_1948;
    @Shadow private boolean field_1940;
    @Shadow private int field_1942;
    @Shadow private int field_1943;
    @Shadow private IntBuffer field_1941;
    @Shadow private boolean field_1959;
    @Shadow private FloatBuffer field_1950;
    @Shadow private boolean field_1960;
    @Shadow private ShortBuffer field_1951;
    @Shadow private boolean field_1958;
    @Shadow private boolean field_1961;
    @Shadow private int field_1962;

    @Shadow protected abstract void method_1412();

    @Shadow private int field_1963;
    @Shadow private double field_1954;
    @Shadow private double field_1955;
    @Shadow private int field_1956;
    @Shadow private int field_1957;
    @Shadow private int field_1969;
    @Shadow public double field_1966;
    @Shadow public double field_1967;
    @Shadow public double field_1968;
    @Shadow public static Tessellator INSTANCE;
    @Shadow private int field_1944;
    @Shadow private static boolean field_1947;
    // Forge Fields
    @Unique
    private static int nativeBufferSize = 2097152;
    @Unique
    private static int trivertsInBuffer = nativeBufferSize / 48 * 6;
    @Unique
    public boolean defaultTexture = false;
    @Unique
    private int rawBufferSize = 0;
    @Unique
    public int textureID = 0;

    @Unique
    private static ByteBuffer forged_field_1948 = GlAllocationUtils.allocateByteBuffer(nativeBufferSize * 4);
    @Unique
    private static IntBuffer forged_field_1949 = forged_field_1948.asIntBuffer();
    @Unique
    private static FloatBuffer forged_field_1950 = forged_field_1948.asFloatBuffer();
    @Unique
    private static ShortBuffer forged_field_1951 = forged_field_1948.asShortBuffer();

    @Unique
    private static boolean forged_field_1940 = false;
    @Unique
    private static IntBuffer forged_field_1941;
    @Unique
    private static int forged_field_1943 = 10;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ctr2(int par1, CallbackInfo ci) {
        this.field_1944 = nativeBufferSize * 4;
        this.field_1948 = forged_field_1948;
        this.field_1949 = forged_field_1949;
        this.field_1950 = forged_field_1950;
        this.field_1951 = forged_field_1951;
        this.field_1952 = new int[this.field_1944];
        this.field_1940 = forged_field_1940;
        this.field_1941 = forged_field_1941;
        this.field_1943 = forged_field_1943;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public int method_1396() {
        if (!this.field_1970) {
            throw new IllegalStateException("Not tesselating!");
        } else {
            this.field_1970 = false;
            int offs = 0;

            while(offs < this.field_1953) {
                int vtc = 0;
                if (this.field_1965 == 7 && field_1946) {
                    vtc = Math.min(this.field_1953 - offs, trivertsInBuffer);
                } else {
                    vtc = Math.min(this.field_1953 - offs, nativeBufferSize >> 5);
                }

                field_1949.clear();
                field_1949.put(this.field_1952, offs * 8, vtc * 8);
                field_1948.position(0);
                field_1948.limit(vtc * 32);
                offs += vtc;
                if (field_1940) {
                    this.field_1942 = (this.field_1942 + 1) % field_1943;
                    ARBVertexBufferObject.glBindBufferARB(34962, field_1941.get(this.field_1942));
                    ARBVertexBufferObject.glBufferDataARB(34962, field_1948, 35040);
                }

                if (this.field_1959) {
                    if (field_1940) {
                        GL11.glTexCoordPointer(2, 5126, 32, 12L);
                    } else {
                        field_1950.position(3);
                        GL11.glTexCoordPointer(2, 32, field_1950);
                    }

                    GL11.glEnableClientState(32888);
                }

                if (this.field_1960) {
                    GLX.gl13ClientActiveTexture(GLX.lightmapTextureUnit);
                    if (field_1940) {
                        GL11.glTexCoordPointer(2, 5122, 32, 28L);
                    } else {
                        field_1951.position(14);
                        GL11.glTexCoordPointer(2, 32, field_1951);
                    }

                    GL11.glEnableClientState(32888);
                    GLX.gl13ClientActiveTexture(GLX.textureUnit);
                }

                if (this.field_1958) {
                    if (field_1940) {
                        GL11.glColorPointer(4, 5121, 32, 20L);
                    } else {
                        field_1948.position(20);
                        GL11.glColorPointer(4, true, 32, field_1948);
                    }

                    GL11.glEnableClientState(32886);
                }

                if (this.field_1961) {
                    if (field_1940) {
                        GL11.glNormalPointer(5121, 32, 24L);
                    } else {
                        field_1948.position(24);
                        GL11.glNormalPointer(32, field_1948);
                    }

                    GL11.glEnableClientState(32885);
                }

                if (field_1940) {
                    GL11.glVertexPointer(3, 5126, 32, 0L);
                } else {
                    field_1950.position(0);
                    GL11.glVertexPointer(3, 32, field_1950);
                }

                GL11.glEnableClientState(32884);
                if (this.field_1965 == 7 && field_1946) {
                    GL11.glDrawArrays(4, 0, vtc);
                } else {
                    GL11.glDrawArrays(this.field_1965, 0, vtc);
                }

                GL11.glDisableClientState(32884);
                if (this.field_1959) {
                    GL11.glDisableClientState(32888);
                }

                if (this.field_1960) {
                    GLX.gl13ClientActiveTexture(GLX.lightmapTextureUnit);
                    GL11.glDisableClientState(32888);
                    GLX.gl13ClientActiveTexture(GLX.textureUnit);
                }

                if (this.field_1958) {
                    GL11.glDisableClientState(32886);
                }

                if (this.field_1961) {
                    GL11.glDisableClientState(32885);
                }
            }

            if (this.rawBufferSize > 131072 && this.field_1962 < this.rawBufferSize << 3) {
                this.rawBufferSize = 0;
                this.field_1952 = null;
            }

            int var1 = this.field_1962 * 4;
            this.method_1412();
            return var1;
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1398(double par1, double par3, double par5) {
        if (this.field_1962 >= this.rawBufferSize - 32) {
            if (this.rawBufferSize == 0) {
                this.rawBufferSize = 65536;
                this.field_1952 = new int[this.rawBufferSize];
            } else {
                this.rawBufferSize *= 2;
                this.field_1952 = Arrays.copyOf(this.field_1952, this.rawBufferSize);
            }
        }

        ++this.field_1963;
        if (this.field_1965 == 7 && field_1946 && this.field_1963 % 4 == 0) {
            for(int var7 = 0; var7 < 2; ++var7) {
                int var8 = 8 * (3 - var7);
                if (this.field_1959) {
                    this.field_1952[this.field_1962 + 3] = this.field_1952[this.field_1962 - var8 + 3];
                    this.field_1952[this.field_1962 + 4] = this.field_1952[this.field_1962 - var8 + 4];
                }

                if (this.field_1960) {
                    this.field_1952[this.field_1962 + 7] = this.field_1952[this.field_1962 - var8 + 7];
                }

                if (this.field_1958) {
                    this.field_1952[this.field_1962 + 5] = this.field_1952[this.field_1962 - var8 + 5];
                }

                this.field_1952[this.field_1962 + 0] = this.field_1952[this.field_1962 - var8 + 0];
                this.field_1952[this.field_1962 + 1] = this.field_1952[this.field_1962 - var8 + 1];
                this.field_1952[this.field_1962 + 2] = this.field_1952[this.field_1962 - var8 + 2];
                ++this.field_1953;
                this.field_1962 += 8;
            }
        }

        if (this.field_1959) {
            this.field_1952[this.field_1962 + 3] = Float.floatToRawIntBits((float)this.field_1954);
            this.field_1952[this.field_1962 + 4] = Float.floatToRawIntBits((float)this.field_1955);
        }

        if (this.field_1960) {
            this.field_1952[this.field_1962 + 7] = this.field_1956;
        }

        if (this.field_1958) {
            this.field_1952[this.field_1962 + 5] = this.field_1957;
        }

        if (this.field_1961) {
            this.field_1952[this.field_1962 + 6] = this.field_1969;
        }

        this.field_1952[this.field_1962 + 0] = Float.floatToRawIntBits((float)(par1 + this.field_1966));
        this.field_1952[this.field_1962 + 1] = Float.floatToRawIntBits((float)(par3 + this.field_1967));
        this.field_1952[this.field_1962 + 2] = Float.floatToRawIntBits((float)(par5 + this.field_1968));
        this.field_1962 += 8;
        ++this.field_1953;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void method_1410(float par1, float par2, float par3) {
        this.field_1966 += (double)par1;
        this.field_1967 += (double)par2;
        this.field_1968 += (double)par3;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void classInit(CallbackInfo ci) {
        INSTANCE.setDefaultTexture(true);
        forged_field_1940 = field_1947 && GLContext.getCapabilities().GL_ARB_vertex_buffer_object;
        if (forged_field_1940) {
            forged_field_1941 = GlAllocationUtils.allocateIntBuffer(forged_field_1943);
            ARBVertexBufferObject.glGenBuffersARB(forged_field_1941);
        }
    }

    @Override
    public void setDefaultTexture(boolean defaultTexture) {
        this.defaultTexture = defaultTexture;
    }

    @Override
    public boolean defaultTexture() {
        return this.defaultTexture;
    }

    @Override
    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    @Override
    public int getTextureID() {
        return this.textureID;
    }
}
