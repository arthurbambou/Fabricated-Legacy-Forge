package fr.catcore.fabricatedforge.mixin.forgefml.client;

import cpw.mods.fml.relauncher.FMLRelauncher;
import fr.catcore.fabricatedforge.mixininterface.IMinecraftApplet;
import net.minecraft.client.AppletMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;
import net.minecraft.client.awt.AppletCanvas;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.applet.Applet;
import java.awt.*;

@Mixin(MinecraftApplet.class)
public class MinecraftAppletMixin extends Applet implements IMinecraftApplet {

    @Shadow private Canvas canvas;

    @Shadow private Minecraft clientInstance;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void init() {
        FMLRelauncher.appletEntry(this);
    }

    @Unique
    private boolean relaunched = false;

    @Override
    public void setRelaunched(boolean relaunched) {
        this.relaunched = relaunched;
    }

    @Override
    public boolean isRelaunched() {
        return relaunched;
    }

    @Unique
    public void fmlInitReentry() {
        this.canvas = new AppletCanvas((MinecraftApplet)(Object) this);
        boolean var1 = "true".equalsIgnoreCase(this.getParameter("fullscreen"));
        this.clientInstance = new AppletMinecraft((MinecraftApplet)(Object) this, this.canvas, (MinecraftApplet)(Object) this, this.getWidth(), this.getHeight(), var1);
        this.clientInstance.host = this.getDocumentBase().getHost();
        if (this.getDocumentBase().getPort() > 0) {
            this.clientInstance.host = this.clientInstance.host + ":" + this.getDocumentBase().getPort();
        }

        if (this.getParameter("username") != null && this.getParameter("sessionid") != null) {
            this.clientInstance.session = new Session(this.getParameter("username"), this.getParameter("sessionid"));
            System.out.println("Setting user: " + this.clientInstance.session.username + ", " + this.clientInstance.session.field_1048);
        } else {
            this.clientInstance.session = new Session("Player", "");
        }

        ((MinecraftAccessor)this.clientInstance).setDemo_invoker("true".equals(this.getParameter("demo")));
        if (this.getParameter("server") != null && this.getParameter("port") != null) {
            this.clientInstance.setServer(this.getParameter("server"), Integer.parseInt(this.getParameter("port")));
        }

        this.clientInstance.isApplet = !"true".equals(this.getParameter("stand-alone"));
        this.setLayout(new BorderLayout());
        this.add(this.canvas, "Center");
        this.canvas.setFocusable(true);
        this.validate();
    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void start() {
        FMLRelauncher.appletStart(this);
    }

    @Unique
    public void fmlStartReentry() {
        if (this.clientInstance != null) {
            this.clientInstance.paused = false;
        }

    }
}
