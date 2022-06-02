package cpw.mods.fml.client.modloader;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Booleans;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.BaseMod;
import net.minecraft.client.options.KeyBinding;

public class ModLoaderKeyBindingHandler extends KeyHandler {
    private ModLoaderModContainer modContainer;
    private List<KeyBinding> helper;
    private boolean[] active = new boolean[0];
    private boolean[] mlRepeats = new boolean[0];
    private boolean[] armed = new boolean[0];

    public ModLoaderKeyBindingHandler() {
        super(new KeyBinding[0], new boolean[0]);
    }

    void setModContainer(ModLoaderModContainer modContainer) {
        this.modContainer = modContainer;
    }

    public void fireKeyEvent(KeyBinding kb) {
        ((BaseMod)this.modContainer.getMod()).keyboardEvent(kb);
    }

    public void keyDown(EnumSet<TickType> type, KeyBinding kb, boolean end, boolean repeats) {
        if (end) {
            int idx = this.helper.indexOf(kb);
            if (type.contains(TickType.CLIENT)) {
                this.armed[idx] = true;
            }

            if (this.armed[idx] && type.contains(TickType.RENDER) && (!this.active[idx] || this.mlRepeats[idx])) {
                this.fireKeyEvent(kb);
                this.active[idx] = true;
                this.armed[idx] = false;
            }

        }
    }

    public void keyUp(EnumSet<TickType> type, KeyBinding kb, boolean end) {
        if (end) {
            int idx = this.helper.indexOf(kb);
            this.active[idx] = false;
        }
    }

    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT, TickType.RENDER);
    }

    public String getLabel() {
        return this.modContainer.getModId() + " KB " + this.keyBindings[0].code;
    }

    void addKeyBinding(KeyBinding binding, boolean repeats) {
        this.keyBindings = (KeyBinding[])ObjectArrays.concat(this.keyBindings, binding);
        this.repeatings = new boolean[this.keyBindings.length];
        Arrays.fill(this.repeatings, true);
        this.active = new boolean[this.keyBindings.length];
        this.armed = new boolean[this.keyBindings.length];
        this.mlRepeats = Booleans.concat(new boolean[][]{this.mlRepeats, {repeats}});
        this.keyDown = new boolean[this.keyBindings.length];
        this.helper = Arrays.asList(this.keyBindings);
    }
}
