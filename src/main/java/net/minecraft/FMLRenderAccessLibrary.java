package net.minecraft;

import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import java.awt.Dimension;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.block.Block;
import net.minecraft.client.class_534;
import net.minecraft.client.class_535;
import net.minecraft.client.class_584;
import net.minecraft.client.texture.ITexturePack;
import net.minecraft.world.BlockView;

public class FMLRenderAccessLibrary {
    public FMLRenderAccessLibrary() {
    }

    public static Logger getLogger() {
        return FMLLog.getLogger();
    }

    public static void log(Level level, String message) {
        FMLLog.log(level, message, new Object[0]);
    }

    public static void log(Level level, String message, Throwable throwable) {
        FMLLog.log(level, throwable, message, new Object[0]);
    }

    public static void setTextureDimensions(int textureId, int width, int height, List<class_584> textureFXList) {
        TextureFXManager.instance().setTextureDimensions(textureId, width, height, textureFXList);
    }

    public static void preRegisterEffect(class_584 textureFX) {
        TextureFXManager.instance().onPreRegisterEffect(textureFX);
    }

    public static boolean onUpdateTextureEffect(class_584 textureFX) {
        return TextureFXManager.instance().onUpdateTextureEffect(textureFX);
    }

    public static Dimension getTextureDimensions(class_584 textureFX) {
        return TextureFXManager.instance().getTextureDimensions(textureFX);
    }

    public static void onTexturePackChange(class_534 engine, ITexturePack texturePack, List<class_584> textureFXList) {
        TextureFXManager.instance().onTexturePackChange(engine, texturePack, textureFXList);
    }

    public static boolean renderWorldBlock(class_535 renderer, BlockView world, int x, int y, int z, Block block, int modelId) {
        return RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, block, modelId);
    }

    public static void renderInventoryBlock(class_535 renderer, Block block, int metadata, int modelID) {
        RenderingRegistry.instance().renderInventoryBlock(renderer, block, metadata, modelID);
    }

    public static boolean renderItemAsFull3DBlock(int modelId) {
        return RenderingRegistry.instance().renderItemAsFull3DBlock(modelId);
    }
}