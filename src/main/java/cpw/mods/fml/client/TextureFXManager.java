package cpw.mods.fml.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.imageio.ImageIO;

import net.minecraft.ModTextureStatic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_534;
import net.minecraft.client.class_584;
import net.minecraft.client.texture.ITexturePack;
import org.lwjgl.opengl.GL11;

public class TextureFXManager {
    private static final TextureFXManager INSTANCE = new TextureFXManager();
    private Map<Integer, TextureFXManager.TextureProperties> textureProperties = Maps.newHashMap();
    private Multimap<String, OverrideInfo> overrideInfo = ArrayListMultimap.create();
    private HashSet<OverrideInfo> animationSet = new HashSet();
    private List<class_584> addedTextureFX = new ArrayList();
    private Minecraft client;
    private HashMap<Integer, Dimension> textureDims = new HashMap();
    private IdentityHashMap<class_584, Integer> effectTextures = new IdentityHashMap();
    private ITexturePack earlyTexturePack;

    public TextureFXManager() {
    }

    void setClient(Minecraft client) {
        this.client = client;
    }

    public boolean onUpdateTextureEffect(class_584 effect) {
        ITextureFX ifx = effect instanceof ITextureFX ? (ITextureFX)effect : null;
        if (ifx != null && ifx.getErrored()) {
            return false;
        } else {
            String name = effect.getClass().getSimpleName();
            this.client.profiler.push(name);

            try {
                if (!FMLClientHandler.instance().hasOptifine()) {
                    effect.method_1613();
                }
            } catch (Exception var6) {
                FMLLog.warning(
                        "Texture FX %s has failed to animate. Likely caused by a texture pack change that they did not respond correctly to", new Object[]{name}
                );
                if (ifx != null) {
                    ifx.setErrored(true);
                }

                this.client.profiler.pop();
                return false;
            }

            this.client.profiler.pop();
            if (ifx != null) {
                Dimension dim = this.getTextureDimensions(effect);
                int target = (dim.width >> 4) * (dim.height >> 4) << 2;
                if (effect.field_2152.length != target) {
                    FMLLog.warning("Detected a texture FX sizing discrepancy in %s (%d, %d)", new Object[]{name, effect.field_2152.length, target});
                    ifx.setErrored(true);
                    return false;
                }
            }

            return true;
        }
    }

    public void scaleTextureFXData(byte[] data, ByteBuffer buf, int target, int length) {
        int sWidth = (int)Math.sqrt((double)(data.length / 4));
        int factor = target / sWidth;
        byte[] tmp = new byte[4];
        buf.clear();
        if (factor > 1) {
            for(int y = 0; y < sWidth; ++y) {
                int sRowOff = sWidth * y;
                int tRowOff = target * y * factor;

                for(int x = 0; x < sWidth; ++x) {
                    int sPos = (x + sRowOff) * 4;
                    tmp[0] = data[sPos + 0];
                    tmp[1] = data[sPos + 1];
                    tmp[2] = data[sPos + 2];
                    tmp[3] = data[sPos + 3];
                    int tPosTop = x * factor + tRowOff;

                    for(int y2 = 0; y2 < factor; ++y2) {
                        buf.position((tPosTop + y2 * target) * 4);

                        for(int x2 = 0; x2 < factor; ++x2) {
                            buf.put(tmp);
                        }
                    }
                }
            }
        }

        buf.position(0).limit(length);
    }

    public void onPreRegisterEffect(class_584 effect) {
        Dimension dim = this.getTextureDimensions(effect);
        if (effect instanceof ITextureFX) {
            ((ITextureFX)effect).onTextureDimensionsUpdate(dim.width, dim.height);
        }

    }

    public int getEffectTexture(class_584 effect) {
        Integer id = (Integer)this.effectTextures.get(effect);
        if (id != null) {
            return id;
        } else {
            int old = GL11.glGetInteger(32873);
            effect.method_1614(this.client.field_3813);
            id = GL11.glGetInteger(32873);
            GL11.glBindTexture(3553, old);
            this.effectTextures.put(effect, id);
            return id;
        }
    }

    public void onTexturePackChange(class_534 engine, ITexturePack texturepack, List<class_584> effects) {
        this.pruneOldTextureFX(texturepack, effects);

        for(class_584 tex : effects) {
            if (tex instanceof ITextureFX) {
                ((ITextureFX)tex).onTexturePackChanged(engine, texturepack, this.getTextureDimensions(tex));
            }
        }

        this.loadTextures(texturepack);
    }

    public void setTextureDimensions(int id, int width, int height, List<class_584> effects) {
        Dimension dim = new Dimension(width, height);
        this.textureDims.put(id, dim);

        for(class_584 tex : effects) {
            if (this.getEffectTexture(tex) == id && tex instanceof ITextureFX) {
                ((ITextureFX)tex).onTextureDimensionsUpdate(width, height);
            }
        }

    }

    public Dimension getTextureDimensions(class_584 effect) {
        return this.getTextureDimensions(this.getEffectTexture(effect));
    }

    public Dimension getTextureDimensions(int id) {
        return (Dimension)this.textureDims.get(id);
    }

    public void addAnimation(class_584 anim) {
        OverrideInfo info = new OverrideInfo();
        info.index = anim.field_2153;
        info.imageIndex = anim.field_2157;
        info.textureFX = anim;
        if (this.animationSet.contains(info)) {
            this.animationSet.remove(info);
        }

        this.animationSet.add(info);
    }

    public void loadTextures(ITexturePack texturePack) {
        this.registerTextureOverrides(this.client.field_3813);
    }

    public void registerTextureOverrides(class_534 renderer) {
        for(OverrideInfo animationOverride : this.animationSet) {
            renderer.method_1416(animationOverride.textureFX);
            this.addedTextureFX.add(animationOverride.textureFX);
            FMLCommonHandler.instance()
                    .getFMLLogger()
                    .finer(
                            String.format(
                                    "Registered texture override %d (%d) on %s (%d)",
                                    animationOverride.index,
                                    animationOverride.textureFX.field_2153,
                                    animationOverride.textureFX.getClass().getSimpleName(),
                                    animationOverride.textureFX.field_2157
                            )
                    );
        }

        for(String fileToOverride : this.overrideInfo.keySet()) {
            for(OverrideInfo override : this.overrideInfo.get(fileToOverride)) {
                try {
                    BufferedImage image = this.loadImageFromTexturePack(renderer, override.override);
                    ModTextureStatic mts = new ModTextureStatic(override.index, 1, override.texture, image);
                    renderer.method_1416(mts);
                    this.addedTextureFX.add(mts);
                    FMLCommonHandler.instance()
                            .getFMLLogger()
                            .finer(
                                    String.format("Registered texture override %d (%d) on %s (%d)", override.index, mts.field_2153, override.texture, mts.field_2157)
                            );
                } catch (IOException var8) {
                    FMLCommonHandler.instance().getFMLLogger().throwing("FMLClientHandler", "registerTextureOverrides", var8);
                }
            }
        }

    }

    protected void registerAnimatedTexturesFor(ModContainer mod) {
    }

    public void onEarlyTexturePackLoad(ITexturePack fallback) {
        if (this.client == null) {
            this.earlyTexturePack = fallback;
        } else {
            this.loadTextures(fallback);
        }

    }

    public void pruneOldTextureFX(ITexturePack var1, List<class_584> effects) {
        ListIterator<class_584> li = this.addedTextureFX.listIterator();

        while(li.hasNext()) {
            class_584 tex = (class_584)li.next();
            if (tex instanceof FMLTextureFX) {
                if (((FMLTextureFX)tex).unregister(this.client.field_3813, effects)) {
                    li.remove();
                }
            } else {
                effects.remove(tex);
                li.remove();
            }
        }

    }

    public void addNewTextureOverride(String textureToOverride, String overridingTexturePath, int location) {
        OverrideInfo info = new OverrideInfo();
        info.index = location;
        info.override = overridingTexturePath;
        info.texture = textureToOverride;
        this.overrideInfo.put(textureToOverride, info);
        FMLLog.fine(
                "Overriding %s @ %d with %s. %d slots remaining",
                new Object[]{textureToOverride, location, overridingTexturePath, SpriteHelper.freeSlotCount(textureToOverride)}
        );
    }

    public BufferedImage loadImageFromTexturePack(class_534 renderEngine, String path) throws IOException {
        InputStream image = this.client.texturePackManager.getCurrentTexturePack().openStream(path);
        if (image == null) {
            throw new RuntimeException(String.format("The requested image path %s is not found", path));
        } else {
            BufferedImage result = ImageIO.read(image);
            if (result == null) {
                throw new RuntimeException(String.format("The requested image path %s appears to be corrupted", path));
            } else {
                return result;
            }
        }
    }

    public static TextureFXManager instance() {
        return INSTANCE;
    }

    private class TextureProperties {
        private int textureId;
        private Dimension dim;

        private TextureProperties() {
        }
    }
}
