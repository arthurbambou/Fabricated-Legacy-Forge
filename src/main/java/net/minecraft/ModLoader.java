package net.minecraft;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.SpriteHelper;
import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import net.minecraft.advancement.Achievement;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_469;
import net.minecraft.client.class_534;
import net.minecraft.client.class_535;
import net.minecraft.client.class_584;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.command.Command;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.class_690;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPacketListener;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.level.LevelGeneratorType;

public class ModLoader {
    public static final String fmlMarker = "This is an FML marker";
    @Deprecated
    public static final Map<String, Map<String, String>> localizedStrings = Collections.emptyMap();

    public ModLoader() {
    }

    public static void addAchievementDesc(Achievement achievement, String name, String description) {
        String achName = achievement.getStringId();
        addLocalization(achName, name);
        addLocalization(achName + ".desc", description);
    }

    @Deprecated
    public static int addAllFuel(int id, int metadata) {
        return 0;
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void addAllRenderers(Map<Class<? extends Entity>, EntityRenderer> renderers) {
    }

    @SideOnly(Side.CLIENT)
    public static void addAnimation(class_584 anim) {
        TextureFXManager.instance().addAnimation(anim);
    }

    @SideOnly(Side.CLIENT)
    public static int addArmor(String armor) {
        return RenderingRegistry.addNewArmourRendererPrefix(armor);
    }

    public static void addBiome(Biome biome) {
        GameRegistry.addBiome(biome);
    }

    public static void addEntityTracker(
            BaseMod mod, Class<? extends Entity> entityClass, int entityTypeId, int updateRange, int updateInterval, boolean sendVelocityInfo
    ) {
        ModLoaderHelper.buildEntityTracker(mod, entityClass, entityTypeId, updateRange, updateInterval, sendVelocityInfo);
    }

    public static void addCommand(Command command) {
        ModLoaderHelper.addCommand(command);
    }

    public static void addLocalization(String key, String value) {
        addLocalization(key, "en_US", value);
    }

    public static void addLocalization(String key, String lang, String value) {
        LanguageRegistry.instance().addStringLocalization(key, lang, value);
    }

    public static void addName(Object instance, String name) {
        addName(instance, "en_US", name);
    }

    public static void addName(Object instance, String lang, String name) {
        LanguageRegistry.instance().addNameForObject(instance, lang, name);
    }

    @SideOnly(Side.CLIENT)
    public static int addOverride(String fileToOverride, String fileToAdd) {
        return RenderingRegistry.addTextureOverride(fileToOverride, fileToAdd);
    }

    @SideOnly(Side.CLIENT)
    public static void addOverride(String path, String overlayPath, int index) {
        RenderingRegistry.addTextureOverride(path, overlayPath, index);
    }

    public static void addRecipe(ItemStack output, Object... params) {
        GameRegistry.addRecipe(output, params);
    }

    public static void addShapelessRecipe(ItemStack output, Object... params) {
        GameRegistry.addShapelessRecipe(output, params);
    }

    public static void addSmelting(int input, ItemStack output) {
        GameRegistry.addSmelting(input, output, 1.0F);
    }

    public static void addSmelting(int input, ItemStack output, float experience) {
        GameRegistry.addSmelting(input, output, experience);
    }

    public static void addSpawn(Class<? extends MobEntity> entityClass, int weightedProb, int min, int max, EntityCategory spawnList) {
        EntityRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList, LevelGeneratorType.base12Biomes);
    }

    public static void addSpawn(Class<? extends MobEntity> entityClass, int weightedProb, int min, int max, EntityCategory spawnList, Biome... biomes) {
        EntityRegistry.addSpawn(entityClass, weightedProb, min, max, spawnList, biomes);
    }

    public static void addSpawn(String entityName, int weightedProb, int min, int max, EntityCategory spawnList) {
        EntityRegistry.addSpawn(entityName, weightedProb, min, max, spawnList, LevelGeneratorType.base12Biomes);
    }

    public static void addSpawn(String entityName, int weightedProb, int min, int max, EntityCategory spawnList, Biome... biomes) {
        EntityRegistry.addSpawn(entityName, weightedProb, min, max, spawnList, biomes);
    }

    public static void addTrade(int profession, TradeEntry entry) {
        ModLoaderHelper.registerTrade(profession, entry);
    }

    public static void clientSendPacket(Packet packet) {
        PacketDispatcher.sendPacketToServer(packet);
    }

    @Deprecated
    public static boolean dispenseEntity(World world, double x, double y, double z, int xVel, int zVel, ItemStack item) {
        return false;
    }

    public static void genericContainerRemoval(World world, int x, int y, int z) {
    }

    public static List<BaseMod> getLoadedMods() {
        return ModLoaderModContainer.findAll(BaseMod.class);
    }

    public static Logger getLogger() {
        return FMLLog.getLogger();
    }

    @SideOnly(Side.CLIENT)
    public static Minecraft getMinecraftInstance() {
        return FMLClientHandler.instance().getClient();
    }

    public static MinecraftServer getMinecraftServerInstance() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, int fieldindex) {
        return (T)ObfuscationReflectionHelper.getPrivateValue(instanceclass, instance, fieldindex);
    }

    public static <T, E> T getPrivateValue(Class<? super E> instanceclass, E instance, String field) {
        return (T)ObfuscationReflectionHelper.getPrivateValue(instanceclass, instance, new String[]{field});
    }

    @SideOnly(Side.CLIENT)
    public static int getUniqueBlockModelID(BaseMod mod, boolean inventoryRenderer) {
        return ModLoaderClientHelper.obtainBlockModelIdFor(mod, inventoryRenderer);
    }

    public static int getUniqueEntityId() {
        return EntityRegistry.findGlobalUniqueEntityId();
    }

    @SideOnly(Side.CLIENT)
    public static int getUniqueSpriteIndex(String path) {
        return SpriteHelper.getUniqueSpriteIndex(path);
    }

    public static boolean isChannelActive(PlayerEntity player, String channel) {
        return NetworkRegistry.instance().isChannelActive(channel, (Player)player);
    }

    @SideOnly(Side.CLIENT)
    public static boolean isGUIOpen(Class<? extends Screen> gui) {
        return FMLClientHandler.instance().getClient().currentScreen != null && FMLClientHandler.instance().getClient().currentScreen.equals(gui);
    }

    public static boolean isModLoaded(String modname) {
        return Loader.isModLoaded(modname);
    }

    @Deprecated
    public static void loadConfig() {
    }

    @SideOnly(Side.CLIENT)
    public static BufferedImage loadImage(class_534 renderEngine, String path) throws Exception {
        return TextureFXManager.instance().loadImageFromTexturePack(renderEngine, path);
    }

    @Deprecated
    public static void onItemPickup(PlayerEntity player, ItemStack item) {
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void onTick(float tick, Minecraft game) {
    }

    @SideOnly(Side.CLIENT)
    public static void openGUI(PlayerEntity player, Screen gui) {
        FMLClientHandler.instance().displayGuiScreen(player, gui);
    }

    @Deprecated
    public static void populateChunk(ChunkProvider generator, int chunkX, int chunkZ, World world) {
    }

    @Deprecated
    public static void receivePacket(CustomPayloadC2SPacket packet) {
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static KeyBinding[] registerAllKeys(KeyBinding[] keys) {
        return keys;
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void registerAllTextureOverrides(class_534 cache) {
    }

    public static void registerBlock(Block block) {
        GameRegistry.registerBlock(block);
    }

    public static void registerBlock(Block block, Class<? extends BlockItem> itemclass) {
        GameRegistry.registerBlock(block, itemclass);
    }

    public static void registerContainerID(BaseMod mod, int id) {
        ModLoaderHelper.buildGuiHelper(mod, id);
    }

    public static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id) {
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, id);
    }

    public static void registerEntityID(Class<? extends Entity> entityClass, String entityName, int id, int background, int foreground) {
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, id, background, foreground);
    }

    @SideOnly(Side.CLIENT)
    public static void registerKey(BaseMod mod, KeyBinding keyHandler, boolean allowRepeat) {
        ModLoaderClientHelper.registerKeyBinding(mod, keyHandler, allowRepeat);
    }

    public static void registerPacketChannel(BaseMod mod, String channel) {
        NetworkRegistry.instance().registerChannel(ModLoaderHelper.buildPacketHandlerFor(mod), channel);
    }

    public static void registerTileEntity(Class<? extends BlockEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, id);
    }

    @SideOnly(Side.CLIENT)
    public static void registerTileEntity(Class<? extends BlockEntity> tileEntityClass, String id, BlockEntityRenderer renderer) {
        ClientRegistry.registerTileEntity(tileEntityClass, id, renderer);
    }

    public static void removeBiome(Biome biome) {
        GameRegistry.removeBiome(biome);
    }

    public static void removeSpawn(Class<? extends MobEntity> entityClass, EntityCategory spawnList) {
        EntityRegistry.removeSpawn(entityClass, spawnList, LevelGeneratorType.base12Biomes);
    }

    public static void removeSpawn(Class<? extends MobEntity> entityClass, EntityCategory spawnList, Biome... biomes) {
        EntityRegistry.removeSpawn(entityClass, spawnList, biomes);
    }

    public static void removeSpawn(String entityName, EntityCategory spawnList) {
        EntityRegistry.removeSpawn(entityName, spawnList, LevelGeneratorType.base12Biomes);
    }

    public static void removeSpawn(String entityName, EntityCategory spawnList, Biome... biomes) {
        EntityRegistry.removeSpawn(entityName, spawnList, biomes);
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static boolean renderBlockIsItemFull3D(int modelID) {
        return RenderingRegistry.instance().renderItemAsFull3DBlock(modelID);
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void renderInvBlock(class_535 renderer, Block block, int metadata, int modelID) {
        RenderingRegistry.instance().renderInventoryBlock(renderer, block, metadata, modelID);
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static boolean renderWorldBlock(class_535 renderer, BlockView world, int x, int y, int z, Block block, int modelID) {
        return RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, block, modelID);
    }

    @Deprecated
    public static void saveConfig() {
    }

    public static void sendPacket(Packet packet) {
        PacketDispatcher.sendPacketToServer(packet);
    }

    @Deprecated
    public static void serverChat(String text) {
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public static void serverLogin(class_469 handler, class_690 loginPacket) {
    }

    public static void serverSendPacket(ServerPacketListener handler, Packet packet) {
        if (handler != null) {
            PacketDispatcher.sendPacketToPlayer(packet, (Player)handler.getPlayer());
        }

    }

    public static void serverOpenWindow(ServerPlayerEntity player, ScreenHandler container, int ID, int x, int y, int z) {
        ModLoaderHelper.openGui(ID, player, container, x, y, z);
    }

    public static void setInGameHook(BaseMod mod, boolean enable, boolean useClock) {
        ModLoaderHelper.updateStandardTicks(mod, enable, useClock);
    }

    public static void setInGUIHook(BaseMod mod, boolean enable, boolean useClock) {
        ModLoaderHelper.updateGUITicks(mod, enable, useClock);
    }

    public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, int fieldindex, E value) {
        ObfuscationReflectionHelper.setPrivateValue(instanceclass, instance, value, fieldindex);
    }

    public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) {
        ObfuscationReflectionHelper.setPrivateValue(instanceclass, instance, value, new String[]{field});
    }

    @Deprecated
    public static void takenFromCrafting(PlayerEntity player, ItemStack item, Inventory matrix) {
    }

    @Deprecated
    public static void takenFromFurnace(PlayerEntity player, ItemStack item) {
    }

    public static void throwException(String message, Throwable e) {
        FMLCommonHandler.instance().raiseException(e, message, true);
    }

    public static void throwException(Throwable e) {
        throwException("Exception in ModLoader", e);
    }
}
