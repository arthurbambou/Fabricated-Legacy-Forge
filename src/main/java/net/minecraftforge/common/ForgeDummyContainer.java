package net.minecraftforge.common;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.WorldAccessContainer;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;

public class ForgeDummyContainer extends DummyModContainer implements WorldAccessContainer {
    public ForgeDummyContainer() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "Forge";
        meta.name = "Minecraft Forge";
        meta.version = String.format("%d.%d.%d.%d", 5, 0, 0, 326);
        meta.credits = "Made possible with help from many people";
        meta.authorList = Arrays.asList("LexManos", "Eloraam", "Spacetoad");
        meta.description = "Minecraft Forge is a common open source API allowing a broad range of mods to work cooperatively together. It allows many mods to be created without them editing the main Minecraft code.";
        meta.url = "http://MinecraftForge.net";
        meta.updateUrl = "http://MinecraftForge.net/forum/index.php/topic,5.0.html";
        meta.screenshots = new String[0];
        meta.logoFile = "/forge_logo.png";
    }

    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent evt) {
        ForgeChunkManager.captureConfig(evt.getModConfigurationDirectory());
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent evt) {
        ForgeChunkManager.loadConfiguration();
    }

    public NbtCompound getDataForWriting(WorldSaveHandler handler, LevelProperties info) {
        NbtCompound forgeData = new NbtCompound();
        NbtCompound dimData = DimensionManager.saveDimensionDataMap();
        forgeData.put("DimensionData", dimData);
        return forgeData;
    }

    public void readData(WorldSaveHandler handler, LevelProperties info, Map<String, NbtElement> propertyMap, NbtCompound tag) {
        if (tag.contains("DimensionData")) {
            DimensionManager.loadDimensionDataMap(tag.contains("DimensionData") ? tag.getCompound("DimensionData") : null);
        }

    }
}
