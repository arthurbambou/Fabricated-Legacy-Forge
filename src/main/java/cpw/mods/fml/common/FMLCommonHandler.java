package cpw.mods.fml.common;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.server.FMLServerHandler;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.MapUpdate_S2CPacket;
import net.minecraft.server.ListenThread;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.class_739;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;

public class FMLCommonHandler {
    private static final FMLCommonHandler INSTANCE = new FMLCommonHandler();
    private IFMLSidedHandler sidedDelegate;
    private List<IScheduledTickHandler> scheduledClientTicks = Lists.newArrayList();
    private List<IScheduledTickHandler> scheduledServerTicks = Lists.newArrayList();
    private Class<?> forge;
    private boolean noForge;
    private List<String> brandings;
    private List<ICrashCallable> crashCallables = Lists.newArrayList(new ICrashCallable[]{Loader.instance().getCallableCrashInformation()});
    private Set<WorldSaveHandler> handlerSet = Sets.newSetFromMap(new MapMaker().weakKeys().makeMap());

    public FMLCommonHandler() {
    }

    public void beginLoading(IFMLSidedHandler handler) {
        this.sidedDelegate = handler;
        FMLLog.info("Attempting early MinecraftForge initialization", new Object[0]);
        this.callForgeMethod("initialize");
        this.callForgeMethod("registerCrashCallable");
        FMLLog.info("Completed early MinecraftForge initialization", new Object[0]);
    }

    public void rescheduleTicks(Side side) {
        TickRegistry.updateTickQueue(side.isClient() ? this.scheduledClientTicks : this.scheduledServerTicks, side);
    }

    public void tickStart(EnumSet<TickType> ticks, Side side, Object... data) {
        List<IScheduledTickHandler> scheduledTicks = side.isClient() ? this.scheduledClientTicks : this.scheduledServerTicks;
        if (scheduledTicks.size() != 0) {
            for(IScheduledTickHandler ticker : scheduledTicks) {
                EnumSet<TickType> ticksToRun = EnumSet.copyOf((EnumSet)Objects.firstNonNull(ticker.ticks(), EnumSet.noneOf(TickType.class)));
                ticksToRun.removeAll(EnumSet.complementOf(ticks));
                if (!ticksToRun.isEmpty()) {
                    ticker.tickStart(ticksToRun, data);
                }
            }

        }
    }

    public void tickEnd(EnumSet<TickType> ticks, Side side, Object... data) {
        List<IScheduledTickHandler> scheduledTicks = side.isClient() ? this.scheduledClientTicks : this.scheduledServerTicks;
        if (scheduledTicks.size() != 0) {
            for(IScheduledTickHandler ticker : scheduledTicks) {
                EnumSet<TickType> ticksToRun = EnumSet.copyOf((EnumSet)Objects.firstNonNull(ticker.ticks(), EnumSet.noneOf(TickType.class)));
                ticksToRun.removeAll(EnumSet.complementOf(ticks));
                if (!ticksToRun.isEmpty()) {
                    ticker.tickEnd(ticksToRun, data);
                }
            }

        }
    }

    public static FMLCommonHandler instance() {
        return INSTANCE;
    }

    public ModContainer findContainerFor(Object mod) {
        return (ModContainer)Loader.instance().getReversedModObjectList().get(mod);
    }

    public Logger getFMLLogger() {
        return FMLLog.getLogger();
    }

    public Side getSide() {
        return this.sidedDelegate.getSide();
    }

    public Side getEffectiveSide() {
        Thread thr = Thread.currentThread();
        return !(thr instanceof class_739) && !(thr instanceof ListenThread) ? Side.CLIENT : Side.SERVER;
    }

    public void raiseException(Throwable exception, String message, boolean stopGame) {
        instance().getFMLLogger().throwing("FMLHandler", "raiseException", exception);
        if (stopGame) {
            this.getSidedDelegate().haltGame(message, exception);
        }

    }

    private Class<?> findMinecraftForge() {
        if (this.forge == null && !this.noForge) {
            try {
                this.forge = Class.forName("net.minecraftforge.common.MinecraftForge");
            } catch (Exception var2) {
                this.noForge = true;
            }
        }

        return this.forge;
    }

    private Object callForgeMethod(String method) {
        if (this.noForge) {
            return null;
        } else {
            try {
                return this.findMinecraftForge().getMethod(method).invoke(null);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    public void computeBranding() {
        if (this.brandings == null) {
            Builder brd = ImmutableList.builder();
            brd.add(Loader.instance().getMCVersionString());
            brd.add("FML v" + Loader.instance().getFMLVersionString());
            String forgeBranding = (String)this.callForgeMethod("getBrandingVersion");
            if (!Strings.isNullOrEmpty(forgeBranding)) {
                brd.add(forgeBranding);
            }

            brd.addAll(this.sidedDelegate.getAdditionalBrandingInformation());

            try {
                Properties props = new Properties();
                props.load(this.getClass().getClassLoader().getResourceAsStream("fmlbranding.properties"));
                brd.add(props.getProperty("fmlbranding"));
            } catch (Exception var5) {
            }

            int tModCount = Loader.instance().getModList().size();
            int aModCount = Loader.instance().getActiveModList().size();
            brd.add(String.format("%d mod%s loaded, %d mod%s active", tModCount, tModCount != 1 ? "s" : "", aModCount, aModCount != 1 ? "s" : ""));
            this.brandings = brd.build();
        }

    }

    public List<String> getBrandings() {
        if (this.brandings == null) {
            this.computeBranding();
        }

        return ImmutableList.copyOf(this.brandings);
    }

    public IFMLSidedHandler getSidedDelegate() {
        return this.sidedDelegate;
    }

    public void onPostServerTick() {
        this.tickEnd(EnumSet.of(TickType.SERVER), Side.SERVER);
    }

    public void onPostWorldTick(Object world) {
        this.tickEnd(EnumSet.of(TickType.WORLD), Side.SERVER, world);
    }

    public void onPreServerTick() {
        this.tickStart(EnumSet.of(TickType.SERVER), Side.SERVER);
    }

    public void onPreWorldTick(Object world) {
        this.tickStart(EnumSet.of(TickType.WORLD), Side.SERVER, world);
    }

    public void onWorldLoadTick(World[] worlds) {
        this.rescheduleTicks(Side.SERVER);

        for(World w : worlds) {
            this.tickStart(EnumSet.of(TickType.WORLDLOAD), Side.SERVER, w);
        }

    }

    public void handleServerStarting(MinecraftServer server) {
        Loader.instance().serverStarting(server);
    }

    public void handleServerStarted() {
        Loader.instance().serverStarted();
    }

    public void handleServerStopping() {
        Loader.instance().serverStopping();
    }

    public MinecraftServer getMinecraftServerInstance() {
        return this.sidedDelegate.getServer();
    }

    public void showGuiScreen(Object clientGuiElement) {
        this.sidedDelegate.showGuiScreen(clientGuiElement);
    }

    public Entity spawnEntityIntoClientWorld(EntityRegistration registration, EntitySpawnPacket entitySpawnPacket) {
        return this.sidedDelegate.spawnEntityIntoClientWorld(registration, entitySpawnPacket);
    }

    public void adjustEntityLocationOnClient(EntitySpawnAdjustmentPacket entitySpawnAdjustmentPacket) {
        this.sidedDelegate.adjustEntityLocationOnClient(entitySpawnAdjustmentPacket);
    }

    public void onServerStart(MinecraftDedicatedServer dedicatedServer) {
        FMLServerHandler.instance();
        this.sidedDelegate.beginServerLoading(dedicatedServer);
    }

    public void onServerStarted() {
        this.sidedDelegate.finishServerLoading();
    }

    public void onPreClientTick() {
        this.tickStart(EnumSet.of(TickType.CLIENT), Side.CLIENT);
    }

    public void onPostClientTick() {
        this.tickEnd(EnumSet.of(TickType.CLIENT), Side.CLIENT);
    }

    public void onRenderTickStart(float timer) {
        this.tickStart(EnumSet.of(TickType.RENDER), Side.CLIENT, timer);
    }

    public void onRenderTickEnd(float timer) {
        this.tickEnd(EnumSet.of(TickType.RENDER), Side.CLIENT, timer);
    }

    public void onPlayerPreTick(PlayerEntity player) {
        Side side = player instanceof ServerPlayerEntity ? Side.SERVER : Side.CLIENT;
        this.tickStart(EnumSet.of(TickType.PLAYER), side, player);
    }

    public void onPlayerPostTick(PlayerEntity player) {
        Side side = player instanceof ServerPlayerEntity ? Side.SERVER : Side.CLIENT;
        this.tickEnd(EnumSet.of(TickType.PLAYER), side, player);
    }

    public void registerCrashCallable(ICrashCallable callable) {
        this.crashCallables.add(callable);
    }

    public void enhanceCrashReport(CrashReport crashReport) {
        for(ICrashCallable call : this.crashCallables) {
            crashReport.method_22519(call.getLabel(), call);
        }

    }

    public void handleTinyPacket(PacketListener handler, MapUpdate_S2CPacket mapData) {
        this.sidedDelegate.handleTinyPacket(handler, mapData);
    }

    public void handleWorldDataSave(WorldSaveHandler handler, LevelProperties worldInfo, NbtCompound tagCompound) {
        for(ModContainer mc : Loader.instance().getModList()) {
            if (mc instanceof InjectedModContainer) {
                WorldAccessContainer wac = ((InjectedModContainer)mc).getWrappedWorldAccessContainer();
                if (wac != null) {
                    NbtCompound dataForWriting = wac.getDataForWriting(handler, worldInfo);
                    tagCompound.put(mc.getModId(), dataForWriting);
                }
            }
        }

    }

    public void handleWorldDataLoad(WorldSaveHandler handler, LevelProperties worldInfo, NbtCompound tagCompound) {
        if (this.getEffectiveSide() == Side.SERVER) {
            if (!this.handlerSet.contains(handler)) {
                this.handlerSet.add(handler);
                Map<String, NbtElement> additionalProperties = Maps.newHashMap();
                worldInfo.setAdditionalProperties(additionalProperties);

                for(ModContainer mc : Loader.instance().getModList()) {
                    if (mc instanceof InjectedModContainer) {
                        WorldAccessContainer wac = ((InjectedModContainer)mc).getWrappedWorldAccessContainer();
                        if (wac != null) {
                            wac.readData(handler, worldInfo, additionalProperties, tagCompound.getCompound(mc.getModId()));
                        }
                    }
                }

            }
        }
    }
}
