package fr.catcore.fabricatedforge.mixin.forgefml.client;

import com.mojang.blaze3d.platform.GLX;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.relauncher.ArgsWrapper;
import cpw.mods.fml.relauncher.FMLRelauncher;
import fr.catcore.fabricatedforge.forged.*;
import fr.catcore.fabricatedforge.mixininterface.IParticleManager;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.client.*;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.color.world.WaterColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AchievementNotification;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.SurvivalInventoryScreen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.texture.ClockSprite;
import net.minecraft.client.texture.CompassSprite;
import net.minecraft.client.texture.TexturePackManager;
import net.minecraft.client.util.NetworkUtils;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.class_481;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Connection;
import net.minecraft.network.IntegratedConnection;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Language;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResultType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.level.storage.AnvilLevelStorage;
import net.minecraft.world.level.storage.LevelStorageAccess;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow public Canvas canvas;

    @Shadow public int width;

    @Shadow public int height;

    @Shadow private boolean windowFocused;

    @Shadow
    public static File getGameFolder() {
        return null;
    }

    @Shadow public File runDirectory;

    @Shadow private LevelStorageAccess currentSave;

    @Shadow public GameOptions options;

    @Shadow public TexturePackManager texturePackManager;

    @Shadow public class_534 field_3813;

    @Shadow protected abstract void method_2915();

    @Shadow public TextRenderer textRenderer;

    @Shadow public TextRenderer shadowTextRenderer;

    @Shadow public GameRenderer gameRenderer;

    @Shadow public StatHandler statHandler;

    @Shadow public Session session;

    @Shadow public MouseInput mouse;

    @Shadow protected abstract void setGlErrorMessage(String message);

    @Shadow public SoundSystem soundSystem;

    @Shadow public ClientWorld world;

    @Shadow public WorldRenderer worldRenderer;

    @Shadow public ParticleManager particleManager;

    @Shadow private ResourceDownloadThread field_3780;

    @Shadow public InGameHud inGameHud;

    @Shadow private String serverAddress;

    @Shadow public abstract void openScreen(Screen screen);

    @Shadow private int serverPort;

    @Shadow public LoadingScreenRenderer loadingScreenRenderer;

    @Shadow public abstract void toggleFullscreen();

    @Shadow protected MinecraftApplet applet;

    @Shadow public volatile boolean running;

    @Shadow @Final public Profiler profiler;

    @Shadow public abstract void scheduleStop();

    @Shadow public volatile boolean paused;

    @Shadow private ClientTickTracker tricker;

    @Shadow public class_481 playerEntity;

    @Shadow public boolean skipGameRender;

    @Shadow protected abstract void method_2926(long l);

    @Shadow
    long debugTime;

    @Shadow public AchievementNotification notification;

    @Shadow protected abstract void method_2917();

    @Shadow protected abstract void method_2923(int i, int j);

    @Shadow
    int fpsCounter;

    @Shadow public abstract boolean isInSingleplayer();

    @Shadow public Screen currentScreen;

    @Shadow private IntegratedServer server;

    @Shadow public abstract boolean isIntegratedServerRunning();

    @Shadow
    public static long getTime() {
        return 0;
    }

    @Shadow
    long time;

    @Shadow private static int currentFps;

    @Shadow public String fpsDebugString;

    @Shadow private Snooper snooper;

    @Shadow private int attackCooldown;

    @Shadow public ClientPlayerInteractionManager interactionManager;

    @Shadow private int blockPlaceDelay;

    @Shadow
    long sysTime;

    @Shadow public boolean focused;

    @Shadow public abstract void closeScreen();

    @Shadow public abstract void openGameMenuScreen();

    @Shadow protected abstract void method_2918();

    @Shadow protected abstract void method_2938(int i);

    @Shadow private int joinPlayerCounter;

    @Shadow private Connection conection;

    @Shadow public abstract class_469 method_2960();

    @Shadow public MobEntity field_3806;

    @Shadow public abstract void setCurrentServerEntry(ServerInfo info);

    @Shadow private boolean isIntegratedServerRunning;

    @Shadow public BlockHitResult result;

    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
    private void fix_openScreen(Screen par1, CallbackInfo ci) {
        if (par1 instanceof FatalErrorScreenForged) {
            ci.cancel();
        }
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void initializeGame() throws LWJGLException {
        if (this.canvas != null) {
            Graphics var1 = this.canvas.getGraphics();
            if (var1 != null) {
                var1.setColor(Color.BLACK);
                var1.fillRect(0, 0, this.width, this.height);
                var1.dispose();
            }

            Display.setParent(this.canvas);
        } else if (this.windowFocused) {
            Display.setFullscreen(true);
            this.width = Display.getDisplayMode().getWidth();
            this.height = Display.getDisplayMode().getHeight();
            if (this.width <= 0) {
                this.width = 1;
            }

            if (this.height <= 0) {
                this.height = 1;
            }
        } else {
            Display.setDisplayMode(new DisplayMode(this.width, this.height));
        }

        Display.setTitle("Minecraft Minecraft 1.4");
        System.out.println("LWJGL Version: " + Sys.getVersion());

        try {
            Display.create(new PixelFormat().withDepthBits(24));
        } catch (LWJGLException var5) {
            var5.printStackTrace();

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var4) {
            }

            Display.create();
        }

        GLX.createContext();
        this.runDirectory = getGameFolder();
        this.currentSave = new AnvilLevelStorage(new File(this.runDirectory, "saves"));
        this.options = new GameOptions((Minecraft)(Object)this, this.runDirectory);
        this.texturePackManager = new TexturePackManager(this.runDirectory, (Minecraft)(Object)this);
        this.field_3813 = new class_534(this.texturePackManager, this.options);
        this.method_2915();
        this.textRenderer = new TextRenderer(this.options, "/font/default.png", this.field_3813, false);
        this.shadowTextRenderer = new TextRenderer(this.options, "/font/alternate.png", this.field_3813, false);
        FMLClientHandler.instance().beginMinecraftLoading((Minecraft)(Object)this);
        if (this.options.language != null) {
            Language.getInstance().setCode(this.options.language);
            this.textRenderer.method_960(Language.getInstance().method_638());
            this.textRenderer.setRightToLeft(Language.hasSpecialCharacters(this.options.language));
        }

        WaterColors.setColorMap(this.field_3813.method_1421("/misc/watercolor.png"));
        GrassColors.setColorMap(this.field_3813.method_1421("/misc/grasscolor.png"));
        FoliageColors.setColorMap(this.field_3813.method_1421("/misc/foliagecolor.png"));
        this.gameRenderer = new GameRenderer((Minecraft)(Object)this);
        EntityRenderDispatcher.field_2094.field_2099 = new HeldItemRenderer((Minecraft)(Object)this);
        this.statHandler = new StatHandler(this.session, this.runDirectory);
        AchievementsAndCriterions.TAKING_INVENTORY.setStatFormatter(new class_334((Minecraft)(Object)this));
        this.method_2915();
        Mouse.create();
        this.mouse = new MouseInput(this.canvas);
        this.setGlErrorMessage("Pre startup");
        GL11.glEnable(3553);
        GL11.glShadeModel(7425);
        GL11.glClearDepth(1.0);
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glCullFace(1029);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        this.setGlErrorMessage("Startup");
        this.soundSystem.method_1709(this.options);
        this.field_3813.method_1416(new class_587Forged());
        this.field_3813.method_1416(new class_590Forged());
        this.field_3813.method_1416(new class_588Forged());
        this.field_3813.method_1416(new CompassSpriteForged((Minecraft)(Object)this));
        this.field_3813.method_1416(new ClockSpriteForged((Minecraft)(Object)this));
        this.field_3813.method_1416(new class_589Forged());
        this.field_3813.method_1416(new class_586Forged());
        this.field_3813.method_1416(new class_585Forged(0));
        this.field_3813.method_1416(new class_585Forged(1));
        this.worldRenderer = new WorldRenderer((Minecraft)(Object)this, this.field_3813);
        GL11.glViewport(0, 0, this.width, this.height);
        this.particleManager = new ParticleManager(this.world, this.field_3813);
        FMLClientHandler.instance().finishMinecraftLoading();

        try {
            this.field_3780 = new ResourceDownloadThread(this.runDirectory, (Minecraft)(Object)this);
            this.field_3780.start();
        } catch (Exception var31) {
        }

        this.setGlErrorMessage("Post startup");
        this.inGameHud = new InGameHud((Minecraft)(Object)this);
        if (this.serverAddress != null) {
            this.openScreen(new ConnectScreen((Minecraft)(Object)this, this.serverAddress, this.serverPort));
        } else {
            this.openScreen(new TitleScreen());
        }

        this.loadingScreenRenderer = new LoadingScreenRenderer((Minecraft)(Object)this);
        if (this.options.fullscreen && !this.windowFocused) {
            this.toggleFullscreen();
        }

        FMLClientHandler.instance().onInitializationComplete();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void runGameLoop() {
        if (this.applet != null && !this.applet.isActive()) {
            this.running = false;
        } else {
            Box.getLocalPool().tick();
            if (this.world != null) {
                this.world.method_4696().tick();
            }

            this.profiler.push("root");
            if (this.canvas == null && Display.isCloseRequested()) {
                this.scheduleStop();
            }

            if (this.paused && this.world != null) {
                float var1 = this.tricker.tickDelta;
                this.tricker.tick();
                this.tricker.tickDelta = var1;
            } else {
                this.tricker.tick();
            }

            long var6 = System.nanoTime();
            this.profiler.push("tick");

            for(int var3 = 0; var3 < this.tricker.ticksThisFrame; ++var3) {
                this.tick();
            }

            this.profiler.swap("preRenderErrors");
            long var7 = System.nanoTime() - var6;
            this.setGlErrorMessage("Pre render");
            class_535.field_2047 = this.options.fancyGraphics;
            this.profiler.swap("sound");
            this.soundSystem.updateListener(this.playerEntity, this.tricker.tickDelta);
            this.profiler.pop();
            this.profiler.push("render");
            this.profiler.push("display");
            GL11.glEnable(3553);
            if (!Keyboard.isKeyDown(65)) {
                Display.update();
            }

            if (this.playerEntity != null && this.playerEntity.isInsideWall()) {
                this.options.perspective = 0;
            }

            this.profiler.pop();
            if (!this.skipGameRender) {
                FMLCommonHandler.instance().onRenderTickStart(this.tricker.tickDelta);
                this.profiler.swap("gameRenderer");
                this.gameRenderer.method_1331(this.tricker.tickDelta);
                this.profiler.pop();
                FMLCommonHandler.instance().onRenderTickEnd(this.tricker.tickDelta);
            }

            GL11.glFlush();
            this.profiler.pop();
            if (!Display.isActive() && this.windowFocused) {
                this.toggleFullscreen();
            }

            if (this.options.debugEnabled && this.options.debugProfilerEnabled) {
                if (!this.profiler.enabled) {
                    this.profiler.reset();
                }

                this.profiler.enabled = true;
                this.method_2926(var7);
            } else {
                this.profiler.enabled = false;
                this.debugTime = System.nanoTime();
            }

            this.notification.tick();
            this.profiler.push("root");
            Thread.yield();
            if (Keyboard.isKeyDown(65)) {
                Display.update();
            }

            this.method_2917();
            if (this.canvas != null && !this.windowFocused && (this.canvas.getWidth() != this.width || this.canvas.getHeight() != this.height)) {
                this.width = this.canvas.getWidth();
                this.height = this.canvas.getHeight();
                if (this.width <= 0) {
                    this.width = 1;
                }

                if (this.height <= 0) {
                    this.height = 1;
                }

                this.method_2923(this.width, this.height);
            }

            this.setGlErrorMessage("Post render");
            ++this.fpsCounter;
            boolean var5 = this.paused;
            this.paused = this.isInSingleplayer() && this.currentScreen != null && this.currentScreen.shouldPauseGame() && !this.server.isPublished();
            if (this.isIntegratedServerRunning() && this.playerEntity != null && this.playerEntity.field_1667 != null && this.paused != var5) {
                ((IntegratedConnection)this.playerEntity.field_1667.method_1205()).setPaused(this.paused);
            }

            while(getTime() >= this.time + 1000L) {
                currentFps = this.fpsCounter;
                this.fpsDebugString = currentFps + " fps, " + BufferBuilder.field_1780 + " chunk updates";
                BufferBuilder.field_1780 = 0;
                this.time += 1000L;
                this.fpsCounter = 0;
                this.snooper.addCpuInfo();
                if (!this.snooper.isActive()) {
                    this.snooper.setActive();
                }
            }

            this.profiler.pop();
            if (this.options.maxFramerate > 0) {
                GameRenderer var10000 = this.gameRenderer;
                Display.sync(GameRenderer.method_1328(this.options.maxFramerate));
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void method_2925(int par1, boolean par2) {
        if (!par2) {
            this.attackCooldown = 0;
        }

        if (par1 != 0 || this.attackCooldown <= 0) {
            if (par2 && this.result != null && this.result.field_595 == HitResultType.TILE && par1 == 0) {
                int var3 = this.result.x;
                int var4 = this.result.y;
                int var5 = this.result.z;
                this.interactionManager.method_1239(var3, var4, var5, this.result.side);
                if (this.playerEntity.method_4579(var3, var4, var5)) {
                    this.particleManager.addBlockHitEffects(var3, var4, var5, this.result);
                    this.playerEntity.method_3207();
                }
            } else {
                this.interactionManager.cancelBlockBreaking();
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void method_2941(int par1) {
        if (par1 != 0 || this.attackCooldown <= 0) {
            if (par1 == 0) {
                this.playerEntity.method_3207();
            }

            if (par1 == 1) {
                this.blockPlaceDelay = 4;
            }

            boolean var2 = true;
            ItemStack var3 = this.playerEntity.inventory.getMainHandStack();
            if (this.result == null) {
                if (par1 == 0 && this.interactionManager.hasLimitedAttackSpeed()) {
                    this.attackCooldown = 10;
                }
            } else if (this.result.field_595 == HitResultType.ENTITY) {
                if (par1 == 0) {
                    this.interactionManager.attackEntity(this.playerEntity, this.result.entity);
                }

                if (par1 == 1 && this.interactionManager.interactEntity(this.playerEntity, this.result.entity)) {
                    var2 = false;
                }
            } else if (this.result.field_595 == HitResultType.TILE) {
                int var4 = this.result.x;
                int var5 = this.result.y;
                int var6 = this.result.z;
                int var7 = this.result.side;
                if (par1 == 0) {
                    this.interactionManager.method_1235(var4, var5, var6, this.result.side);
                } else {
                    int var8 = var3 != null ? var3.count : 0;
                    boolean result = !ForgeEventFactory.onPlayerInteract(this.playerEntity, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, var4, var5, var6, var7).isCanceled();
                    if (result && this.interactionManager.method_1229(this.playerEntity, this.world, var3, var4, var5, var6, var7, this.result.pos)) {
                        var2 = false;
                        this.playerEntity.method_3207();
                    }

                    if (var3 == null) {
                        return;
                    }

                    if (var3.count == 0) {
                        this.playerEntity.inventory.main[this.playerEntity.inventory.selectedSlot] = null;
                    } else if (var3.count != var8 || this.interactionManager.hasCreativeInventory()) {
                        this.gameRenderer.firstPersonRenderer.method_1358();
                    }
                }
            }

            if (var2 && par1 == 1) {
                ItemStack var9 = this.playerEntity.inventory.getMainHandStack();
                boolean result = !ForgeEventFactory.onPlayerInteract(this.playerEntity, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, 0, 0, 0, -1).isCanceled();
                if (result && var9 != null && this.interactionManager.method_1228(this.playerEntity, this.world, var9)) {
                    this.gameRenderer.firstPersonRenderer.method_1360();
                }
            }
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void tick() {
        FMLCommonHandler.instance().rescheduleTicks(Side.CLIENT);
        if (this.blockPlaceDelay > 0) {
            --this.blockPlaceDelay;
        }

        FMLCommonHandler.instance().onPreClientTick();
        this.profiler.push("stats");
        this.statHandler.method_1740();
        this.profiler.swap("gui");
        if (!this.paused) {
            this.inGameHud.tick();
        }

        this.profiler.swap("pick");
        this.gameRenderer.updateTargetedEntity(1.0F);
        this.profiler.swap("gameMode");
        if (!this.paused && this.world != null) {
            this.interactionManager.tick();
        }

        GL11.glBindTexture(3553, this.field_3813.getTextureFromPath("/terrain.png"));
        this.profiler.swap("textures");
        if (!this.paused) {
            this.field_3813.method_1414();
        }

        if (this.currentScreen == null && this.playerEntity != null) {
            if (this.playerEntity.method_2600() <= 0) {
                this.openScreen((Screen)null);
            } else if (this.playerEntity.method_2641() && this.world != null) {
                this.openScreen(new SleepingChatScreen());
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof SleepingChatScreen && !this.playerEntity.method_2641()) {
            this.openScreen((Screen)null);
        }

        if (this.currentScreen != null) {
            this.attackCooldown = 10000;
        }

        if (this.currentScreen != null) {
            this.currentScreen.handleInput();
            if (this.currentScreen != null) {
                this.currentScreen.field_1235.method_1164();
                this.currentScreen.tick();
            }
        }

        if (this.currentScreen == null || this.currentScreen.passEvents) {
            this.profiler.swap("mouse");

            while(Mouse.next()) {
                KeyBinding.setKeyPressed(Mouse.getEventButton() - 100, Mouse.getEventButtonState());
                if (Mouse.getEventButtonState()) {
                    KeyBinding.onKeyPressed(Mouse.getEventButton() - 100);
                }

                long var1 = getTime() - this.sysTime;
                if (var1 <= 200L) {
                    int var3 = Mouse.getEventDWheel();
                    if (var3 != 0) {
                        this.playerEntity.inventory.method_3134(var3);
                        if (this.options.field_953) {
                            if (var3 > 0) {
                                var3 = 1;
                            }

                            if (var3 < 0) {
                                var3 = -1;
                            }

                            this.options.field_956 += (float)var3 * 0.25F;
                        }
                    }

                    if (this.currentScreen == null) {
                        if (!this.focused && Mouse.getEventButtonState()) {
                            this.closeScreen();
                        }
                    } else if (this.currentScreen != null) {
                        this.currentScreen.handleMouse();
                    }
                }
            }

            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }

            this.profiler.swap("keyboard");

            while(Keyboard.next()) {
                KeyBinding.setKeyPressed(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                if (Keyboard.getEventKeyState()) {
                    KeyBinding.onKeyPressed(Keyboard.getEventKey());
                }

                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == 87) {
                        this.toggleFullscreen();
                    } else {
                        if (this.currentScreen != null) {
                            this.currentScreen.method_1040();
                        } else {
                            if (Keyboard.getEventKey() == 1) {
                                this.openGameMenuScreen();
                            }

                            if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
                                this.method_2918();
                            }

                            if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61)) {
                                this.field_3813.updateAnaglyph3D();
                            }

                            if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61)) {
                                boolean var4 = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                                this.options.setOption(GameOption.RENDER_DISTANCE, var4 ? -1 : 1);
                            }

                            if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61)) {
                                this.worldRenderer.reload();
                            }

                            if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61)) {
                                this.options.advancedItemTooltips = !this.options.advancedItemTooltips;
                                this.options.save();
                            }

                            if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61)) {
                                this.options.pauseOnLostFocus = !this.options.pauseOnLostFocus;
                                this.options.save();
                            }

                            if (Keyboard.getEventKey() == 59) {
                                this.options.hudHidden = !this.options.hudHidden;
                            }

                            if (Keyboard.getEventKey() == 61) {
                                this.options.debugEnabled = !this.options.debugEnabled;
                                this.options.debugProfilerEnabled = Screen.hasShiftDown();
                            }

                            if (Keyboard.getEventKey() == 63) {
                                ++this.options.perspective;
                                if (this.options.perspective > 2) {
                                    this.options.perspective = 0;
                                }
                            }

                            if (Keyboard.getEventKey() == 66) {
                                this.options.smoothCameraEnabled = !this.options.smoothCameraEnabled;
                            }
                        }

                        for(int var5 = 0; var5 < 9; ++var5) {
                            if (Keyboard.getEventKey() == 2 + var5) {
                                this.playerEntity.inventory.selectedSlot = var5;
                            }
                        }

                        if (this.options.debugEnabled && this.options.debugProfilerEnabled) {
                            if (Keyboard.getEventKey() == 11) {
                                this.method_2938(0);
                            }

                            for(int var6 = 0; var6 < 9; ++var6) {
                                if (Keyboard.getEventKey() == 2 + var6) {
                                    this.method_2938(var6 + 1);
                                }
                            }
                        }
                    }
                }
            }

            boolean var4 = this.options.chatVisibility != 2;

            while(this.options.keyInventory.wasPressed()) {
                this.openScreen(new SurvivalInventoryScreen(this.playerEntity));
            }

            while(this.options.keyDrop.wasPressed()) {
                this.playerEntity.method_4580();
            }

            while(this.options.keyChat.wasPressed() && var4) {
                this.openScreen(new ChatScreen());
            }

            if (this.currentScreen == null && this.options.keyCommand.wasPressed() && var4) {
                this.openScreen(new ChatScreen("/"));
            }

            if (this.playerEntity.isUsingItem()) {
                if (!this.options.keyUse.pressed) {
                    this.interactionManager.stopUsingItem(this.playerEntity);
                }

                while(this.options.keyAttack.wasPressed()) {
                }

                while(this.options.keyUse.wasPressed()) {
                }

                while(this.options.keyPickItem.wasPressed()) {
                }
            } else {
                while(this.options.keyAttack.wasPressed()) {
                    this.method_2941(0);
                }

                while(this.options.keyUse.wasPressed()) {
                    this.method_2941(1);
                }

                while(this.options.keyPickItem.wasPressed()) {
                    this.doPick();
                }
            }

            if (this.options.keyUse.pressed && this.blockPlaceDelay == 0 && !this.playerEntity.isUsingItem()) {
                this.method_2941(1);
            }

            this.method_2925(0, this.currentScreen == null && this.options.keyAttack.pressed && this.focused);
        }

        if (this.world != null) {
            if (this.playerEntity != null) {
                ++this.joinPlayerCounter;
                if (this.joinPlayerCounter == 30) {
                    this.joinPlayerCounter = 0;
                    this.world.loadEntity(this.playerEntity);
                }
            }

            this.profiler.swap("gameRenderer");
            if (!this.paused) {
                this.gameRenderer.tick();
            }

            this.profiler.swap("levelRenderer");
            if (!this.paused) {
                this.worldRenderer.tick();
            }

            this.profiler.swap("level");
            if (!this.paused) {
                if (this.world.field_4553 > 0) {
                    --this.world.field_4553;
                }

                this.world.tickEntities();
            }

            if (!this.paused) {
                this.world.method_3645(this.world.field_4556 > 0, true);
                this.world.tick();
            }

            this.profiler.swap("animateTick");
            if (!this.paused && this.world != null) {
                this.world.method_1248(MathHelper.floor(this.playerEntity.x), MathHelper.floor(this.playerEntity.y), MathHelper.floor(this.playerEntity.z));
            }

            this.profiler.swap("particles");
            if (!this.paused) {
                this.particleManager.tick();
            }
        } else if (this.conection != null) {
            this.profiler.swap("pendingConnection");
            this.conection.applyQueuedPackets();
        }

        FMLCommonHandler.instance().onPostClientTick();
        this.profiler.pop();
        this.sysTime = getTime();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void connect(ClientWorld par1WorldClient, String par2Str) {
        this.statHandler.method_1739();
        if (par1WorldClient == null) {
            class_469 var3 = this.method_2960();
            if (var3 != null) {
                var3.method_1201();
            }

            if (this.conection != null) {
                this.conection.close();
            }

            if (this.server != null) {
                this.server.stopRunning();
                if (this.loadingScreenRenderer != null) {
                    this.loadingScreenRenderer.setTask("Shutting down internal server...");
                }

                while(!this.server.isStopped()) {
                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException var5) {
                    }
                }
            }

            this.server = null;
        }

        this.field_3806 = null;
        this.conection = null;
        if (this.loadingScreenRenderer != null) {
            this.loadingScreenRenderer.setTitleAndTask(par2Str);
            this.loadingScreenRenderer.setTask("");
        }

        if (par1WorldClient == null && this.world != null) {
            if (this.texturePackManager.isDownloadingTexturePack()) {
                this.texturePackManager.method_1685();
            }

            this.setCurrentServerEntry((ServerInfo)null);
            this.isIntegratedServerRunning = false;
        }

        this.soundSystem.method_1711((String)null, 0.0F, 0.0F, 0.0F);
        this.soundSystem.stopAll();
        this.world = par1WorldClient;
        if (par1WorldClient != null) {
            if (this.worldRenderer != null) {
                this.worldRenderer.method_1371(par1WorldClient);
            }

            if (this.particleManager != null) {
                this.particleManager.setWorld(par1WorldClient);
            }

            if (this.playerEntity == null) {
                this.playerEntity = this.interactionManager.method_1232(par1WorldClient);
                this.interactionManager.method_1236(this.playerEntity);
            }

            this.playerEntity.afterSpawn();
            par1WorldClient.spawnEntity(this.playerEntity);
            this.playerEntity.input = new KeyboardInput(this.options);
            this.interactionManager.copyAbilities(this.playerEntity);
            this.field_3806 = this.playerEntity;
        } else {
            this.currentSave.method_254();
            this.playerEntity = null;
        }

        System.gc();
        this.sysTime = 0L;
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public static void main(String[] par0ArrayOfStr) {
        FMLRelauncher.handleClientRelaunch(new ArgsWrapper(par0ArrayOfStr));
    }

    @Unique
    private static void fmlReentry(ArgsWrapper wrapper) {
        String[] par0ArrayOfStr = wrapper.args;
        HashMap var1 = new HashMap();
        boolean var2 = false;
        boolean var3 = true;
        boolean var4 = false;
        String var5 = "Player" + getTime() % 1000L;
        if (par0ArrayOfStr.length > 0) {
            var5 = par0ArrayOfStr[0];
        }

        String var6 = "-";
        if (par0ArrayOfStr.length > 1) {
            var6 = par0ArrayOfStr[1];
        }

        for(int var7 = 2; var7 < par0ArrayOfStr.length; ++var7) {
            String var8 = par0ArrayOfStr[var7];
            String var9 = var7 == par0ArrayOfStr.length - 1 ? null : par0ArrayOfStr[var7 + 1];
            boolean var10 = false;
            if (var8.equals("-demo") || var8.equals("--demo")) {
                var2 = true;
            } else if (var8.equals("--applet")) {
                var3 = false;
            } else if (var8.equals("--password") && var9 != null) {
                String[] var11 = NetworkUtils.method_4414(var5, var9);
                if (var11 != null) {
                    var5 = var11[0];
                    var6 = var11[1];
                    System.out.println("Logged in insecurely as " + var5 + " - sessionId is " + var6);
                } else {
                    System.out.println("Could not log in as " + var5 + " with given password");
                }

                var10 = true;
            }

            if (var10) {
                ++var7;
            }
        }

        var1.put("demo", "" + var2);
        var1.put("stand-alone", "" + var3);
        var1.put("username", var5);
        var1.put("fullscreen", "" + var4);
        var1.put("sessionid", var6);
        Frame var13 = new Frame();
        var13.setTitle("Minecraft");
        var13.setBackground(Color.BLACK);
        JPanel var12 = new JPanel();
        var13.setLayout(new BorderLayout());
        var12.setPreferredSize(new Dimension(854, 480));
        var13.add(var12, "Center");
        var13.pack();
        var13.setLocationRelativeTo((Component)null);
        var13.setVisible(true);
        var13.addWindowListener(new MinecraftWindowEventListener());
        MinecraftAppletStub var14 = new MinecraftAppletStub(var1);
        MinecraftApplet var15 = new MinecraftApplet();
        var15.setStub(var14);
        var14.setLayout(new BorderLayout());
        var14.add(var15, "Center");
        var14.validate();
        var13.removeAll();
        var13.setLayout(new BorderLayout());
        var13.add(var14, "Center");
        var13.validate();
        var15.init();
        var15.start();
        Runtime.getRuntime().addShutdownHook(new MinecraftShutdownHook());
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void doPick() {
        if (this.result != null) {
            boolean var1 = this.playerEntity.abilities.creativeMode;
            if (!ForgeHooks.onPickBlock(this.result, this.playerEntity, this.world)) {
                return;
            }

            if (var1) {
                int var5 = this.playerEntity.playerScreenHandler.slots.size() - 9 + this.playerEntity.inventory.selectedSlot;
                this.interactionManager.clickCreativeStack(this.playerEntity.inventory.getInvStack(this.playerEntity.inventory.selectedSlot), var5);
            }
        }

    }
}
