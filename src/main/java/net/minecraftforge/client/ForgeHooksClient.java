package net.minecraftforge.client;

import cpw.mods.fml.client.FMLClientHandler;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_534;
import net.minecraft.client.class_535;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.ITexturePack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureLoadEvent;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public class ForgeHooksClient {
    public static HashMap<ForgeHooksClient.TesKey, Tessellator> tessellators = new HashMap();
    public static HashMap<String, Integer> textures = new HashMap();
    public static TreeSet<ForgeHooksClient.TesKey> renderTextures = new TreeSet();
    public static Tessellator defaultTessellator = null;
    public static boolean inWorld = false;
    public static HashMap<ForgeHooksClient.TesKey, IRenderContextHandler> renderHandlers = new HashMap();
    public static IRenderContextHandler unbindContext = null;
    static int renderPass = -1;

    public ForgeHooksClient() {
    }

    protected static void registerRenderContextHandler(String texture, int subID, IRenderContextHandler handler) {
        Integer texID = (Integer)textures.get(texture);
        if (texID == null) {
            texID = engine().getTextureFromPath(texture);
            textures.put(texture, texID);
        }

        renderHandlers.put(new ForgeHooksClient.TesKey(texID, subID), handler);
    }

    static class_534 engine() {
        return FMLClientHandler.instance().getClient().field_3813;
    }

    public static void bindTexture(String texture, int subID) {
        Integer texID = (Integer)textures.get(texture);
        if (texID == null) {
            texID = engine().getTextureFromPath(texture);
            textures.put(texture, texID);
        }

        if (!inWorld) {
            if (unbindContext != null) {
                unbindContext.afterRenderContext();
                unbindContext = null;
            }

            if (Tessellator.INSTANCE.field_1970) {
                int mode = Tessellator.INSTANCE.field_1965;
                Tessellator.INSTANCE.method_1396();
                Tessellator.INSTANCE.method_1408(mode);
            }

            GL11.glBindTexture(3553, texID);
            unbindContext = (IRenderContextHandler)renderHandlers.get(new ForgeHooksClient.TesKey(texID, subID));
            if (unbindContext != null) {
                unbindContext.beforeRenderContext();
            }

        } else {
            bindTessellator(texID, subID);
        }
    }

    public static void unbindTexture() {
        if (inWorld) {
            Tessellator.INSTANCE = defaultTessellator;
        } else {
            if (Tessellator.INSTANCE.field_1970) {
                int mode = Tessellator.INSTANCE.field_1965;
                Tessellator.INSTANCE.method_1396();
                if (unbindContext != null) {
                    unbindContext.afterRenderContext();
                    unbindContext = null;
                }

                Tessellator.INSTANCE.method_1408(mode);
            }

            GL11.glBindTexture(3553, engine().getTextureFromPath("/terrain.png"));
        }
    }

    protected static void bindTessellator(int texture, int subID) {
        ForgeHooksClient.TesKey key = new ForgeHooksClient.TesKey(texture, subID);
        Tessellator tess = (Tessellator)tessellators.get(key);
        if (tess == null) {
            tess = new Tessellator();
            tess.textureID = texture;
            tessellators.put(key, tess);
        }

        if (inWorld && !renderTextures.contains(key)) {
            renderTextures.add(key);
            tess.method_1405();
            tess.method_1406(defaultTessellator.field_1966, defaultTessellator.field_1967, defaultTessellator.field_1968);
        }

        Tessellator.INSTANCE = tess;
    }

    public static void beforeRenderPass(int pass) {
        renderPass = pass;
        defaultTessellator = Tessellator.INSTANCE;
        Tessellator.renderingWorldRenderer = true;
        GL11.glBindTexture(3553, engine().getTextureFromPath("/terrain.png"));
        renderTextures.clear();
        inWorld = true;
    }

    public static void afterRenderPass(int pass) {
        renderPass = -1;
        inWorld = false;

        for(ForgeHooksClient.TesKey info : renderTextures) {
            IRenderContextHandler handler = (IRenderContextHandler)renderHandlers.get(info);
            GL11.glBindTexture(3553, info.texture);
            Tessellator tess = (Tessellator)tessellators.get(info);
            if (handler == null) {
                tess.method_1396();
            } else {
                Tessellator.INSTANCE = tess;
                handler.beforeRenderContext();
                tess.method_1396();
                handler.afterRenderContext();
            }
        }

        GL11.glBindTexture(3553, engine().getTextureFromPath("/terrain.png"));
        Tessellator.renderingWorldRenderer = false;
        Tessellator.INSTANCE = defaultTessellator;
    }

    public static void beforeBlockRender(Block block, class_535 render) {
        if (!block.isDefaultTexture && render.field_2049 == -1) {
            bindTexture(block.getTextureFile(), 0);
        }

    }

    public static void afterBlockRender(Block block, class_535 render) {
        if (!block.isDefaultTexture && render.field_2049 == -1) {
            unbindTexture();
        }

    }

    public static String getArmorTexture(ItemStack armor, String _default) {
        return armor.getItem() instanceof IArmorTextureProvider ? ((IArmorTextureProvider)armor.getItem()).getArmorTextureFile(armor) : _default;
    }

    public static boolean renderEntityItem(
            ItemEntity entity, ItemStack item, float bobing, float rotation, Random random, class_534 engine, class_535 renderBlocks
    ) {
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, ItemRenderType.ENTITY);
        if (customRenderer == null) {
            return false;
        } else {
            if (customRenderer.shouldUseRenderHelper(ItemRenderType.ENTITY, item, ItemRendererHelper.ENTITY_ROTATION)) {
                GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
            }

            if (!customRenderer.shouldUseRenderHelper(ItemRenderType.ENTITY, item, ItemRendererHelper.ENTITY_BOBBING)) {
                GL11.glTranslatef(0.0F, -bobing, 0.0F);
            }

            boolean is3D = customRenderer.shouldUseRenderHelper(ItemRenderType.ENTITY, item, ItemRendererHelper.BLOCK_3D);
            if (item.getItem() instanceof BlockItem && (is3D || class_535.method_1455(Block.BLOCKS[item.id].getBlockType()))) {
                engine.method_1426(engine.getTextureFromPath(item.getItem().getTextureFile()));
                int renderType = Block.BLOCKS[item.id].getBlockType();
                float scale = renderType != 1 && renderType != 19 && renderType != 12 && renderType != 2 ? 0.25F : 0.5F;
                GL11.glScalef(scale, scale, scale);
                int size = entity.field_23087.count;
                int count = size > 20 ? 4 : (size > 5 ? 3 : (size > 1 ? 2 : 1));

                for(int j = 0; j < count; ++j) {
                    GL11.glPushMatrix();
                    if (j > 0) {
                        GL11.glTranslatef(
                                (random.nextFloat() * 2.0F - 1.0F) * 0.2F / 0.5F,
                                (random.nextFloat() * 2.0F - 1.0F) * 0.2F / 0.5F,
                                (random.nextFloat() * 2.0F - 1.0F) * 0.2F / 0.5F
                        );
                    }

                    customRenderer.renderItem(ItemRenderType.ENTITY, item, new Object[]{renderBlocks, entity});
                    GL11.glPopMatrix();
                }
            } else {
                engine.method_1426(engine.getTextureFromPath(item.getItem().getTextureFile()));
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                customRenderer.renderItem(ItemRenderType.ENTITY, item, new Object[]{renderBlocks, entity});
            }

            return true;
        }
    }

    public static boolean renderInventoryItem(class_535 renderBlocks, class_534 engine, ItemStack item, boolean inColor, float zLevel, float x, float y) {
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, ItemRenderType.INVENTORY);
        if (customRenderer == null) {
            return false;
        } else {
            engine.method_1426(engine.getTextureFromPath(Item.ITEMS[item.id].getTextureFile()));
            if (customRenderer.shouldUseRenderHelper(ItemRenderType.INVENTORY, item, ItemRendererHelper.INVENTORY_BLOCK)) {
                GL11.glPushMatrix();
                GL11.glTranslatef(x - 2.0F, y + 3.0F, -3.0F + zLevel);
                GL11.glScalef(10.0F, 10.0F, 10.0F);
                GL11.glTranslatef(1.0F, 0.5F, 1.0F);
                GL11.glScalef(1.0F, 1.0F, -1.0F);
                GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                if (inColor) {
                    int color = Item.ITEMS[item.id].getDisplayColor(item, 0);
                    float r = (float)(color >> 16 & 0xFF) / 255.0F;
                    float g = (float)(color >> 8 & 0xFF) / 255.0F;
                    float b = (float)(color & 0xFF) / 255.0F;
                    GL11.glColor4f(r, g, b, 1.0F);
                }

                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                renderBlocks.field_2048 = inColor;
                customRenderer.renderItem(ItemRenderType.INVENTORY, item, new Object[]{renderBlocks});
                renderBlocks.field_2048 = true;
                GL11.glPopMatrix();
            } else {
                GL11.glDisable(2896);
                GL11.glPushMatrix();
                GL11.glTranslatef(x, y, -3.0F + zLevel);
                if (inColor) {
                    int color = Item.ITEMS[item.id].getDisplayColor(item, 0);
                    float r = (float)(color >> 16 & 0xFF) / 255.0F;
                    float g = (float)(color >> 8 & 0xFF) / 255.0F;
                    float b = (float)(color & 0xFF) / 255.0F;
                    GL11.glColor4f(r, g, b, 1.0F);
                }

                customRenderer.renderItem(ItemRenderType.INVENTORY, item, new Object[]{renderBlocks});
                GL11.glPopMatrix();
                GL11.glEnable(2896);
            }

            return true;
        }
    }

    public static void renderEquippedItem(IItemRenderer customRenderer, class_535 renderBlocks, MobEntity entity, ItemStack item) {
        if (customRenderer.shouldUseRenderHelper(ItemRenderType.EQUIPPED, item, ItemRendererHelper.EQUIPPED_BLOCK)) {
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            customRenderer.renderItem(ItemRenderType.EQUIPPED, item, new Object[]{renderBlocks, entity});
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glEnable(32826);
            GL11.glTranslatef(0.0F, -0.3F, 0.0F);
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            customRenderer.renderItem(ItemRenderType.EQUIPPED, item, new Object[]{renderBlocks, entity});
            GL11.glDisable(32826);
            GL11.glPopMatrix();
        }

    }

    public static void orientBedCamera(Minecraft mc, MobEntity entity) {
        int x = MathHelper.floor(entity.x);
        int y = MathHelper.floor(entity.y);
        int z = MathHelper.floor(entity.z);
        Block block = Block.BLOCKS[mc.world.getBlock(x, y, z)];
        if (block != null && block.isBed(mc.world, x, y, z, entity)) {
            int var12 = block.getBedDirection(mc.world, x, y, z);
            GL11.glRotatef((float)(var12 * 90), 0.0F, 1.0F, 0.0F);
        }

    }

    public static boolean onDrawBlockHighlight(
            WorldRenderer context, PlayerEntity player, BlockHitResult target, int subID, ItemStack currentItem, float partialTicks
    ) {
        return MinecraftForge.EVENT_BUS.post(new DrawBlockHighlightEvent(context, player, target, subID, currentItem, partialTicks));
    }

    public static void dispatchRenderLast(WorldRenderer context, float partialTicks) {
        MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, partialTicks));
    }

    public static void onTextureLoad(String texture, ITexturePack pack) {
        MinecraftForge.EVENT_BUS.post(new TextureLoadEvent(texture, pack));
    }

    public static void onTextureLoadPre(String texture) {
        if (Tessellator.renderingWorldRenderer) {
            String msg = String.format("Warning: Texture %s not preloaded, will cause render glitches!", texture);
            System.out.println(msg);
            if (Tessellator.class.getPackage() != null && Tessellator.class.getPackage().equals("net.minecraft.src")) {
                Minecraft mc = FMLClientHandler.instance().getClient();
                if (mc.inGameHud != null) {
                    mc.inGameHud.getChatHud().method_898(msg);
                }
            }
        }

    }

    private static class TesKey implements Comparable<ForgeHooksClient.TesKey> {
        public final int texture;
        public final int subid;

        public TesKey(int textureID, int subID) {
            this.texture = textureID;
            this.subid = subID;
        }

        public int compareTo(ForgeHooksClient.TesKey key) {
            return this.subid == key.subid ? this.texture - key.texture : this.subid - key.subid;
        }

        public boolean equals(Object obj) {
            return this.compareTo((ForgeHooksClient.TesKey)obj) == 0;
        }

        public int hashCode() {
            return this.texture + 31 * this.subid;
        }
    }
}
