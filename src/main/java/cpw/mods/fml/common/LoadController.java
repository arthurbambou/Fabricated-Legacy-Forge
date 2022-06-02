package cpw.mods.fml.common;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.event.FMLLoadEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class LoadController {
    private Loader loader;
    private EventBus masterChannel;
    private ImmutableMap<String, EventBus> eventChannels;
    private LoaderState state;
    private Multimap<String, ModState> modStates = ArrayListMultimap.create();
    private Multimap<String, Throwable> errors = ArrayListMultimap.create();
    private Map<String, ModContainer> modList;
    private List<ModContainer> activeModList = Lists.newArrayList();
    private ModContainer activeContainer;
    private BiMap<ModContainer, Object> modObjectList;

    public LoadController(Loader loader) {
        this.loader = loader;
        this.masterChannel = new EventBus("FMLMainChannel");
        this.masterChannel.register(this);
        this.state = LoaderState.NOINIT;
    }

    @Subscribe
    public void buildModList(FMLLoadEvent event) {
        this.modList = this.loader.getIndexedModList();
        Builder<String, EventBus> eventBus = ImmutableMap.builder();

        for(ModContainer mod : this.loader.getModList()) {
            EventBus bus = new EventBus(mod.getModId());
            boolean isActive = mod.registerBus(bus, this);
            if (isActive) {
                FMLLog.fine("Activating mod %s", new Object[]{mod.getModId()});
                this.activeModList.add(mod);
                this.modStates.put(mod.getModId(), ModState.UNLOADED);
                eventBus.put(mod.getModId(), bus);
            } else {
                FMLLog.warning("Mod %s has been disabled through configuration", new Object[]{mod.getModId()});
                this.modStates.put(mod.getModId(), ModState.UNLOADED);
                this.modStates.put(mod.getModId(), ModState.DISABLED);
            }
        }

        this.eventChannels = eventBus.build();
    }

    public void distributeStateMessage(LoaderState state, Object... eventData) {
        if (state.hasEvent()) {
            this.masterChannel.post(state.getEvent(eventData));
        }

    }

    public void transition(LoaderState desiredState) {
        LoaderState oldState = this.state;
        this.state = this.state.transition(!this.errors.isEmpty());
        if (this.state != desiredState) {
            Throwable toThrow = null;
            FMLLog.severe("Fatal errors were detected during the transition from %s to %s. Loading cannot continue", new Object[]{oldState, desiredState});
            StringBuilder sb = new StringBuilder();
            this.printModStates(sb);
            FMLLog.severe(sb.toString(), new Object[0]);
            FMLLog.severe("The following problems were captured during this phase", new Object[0]);

            for(Entry<String, Throwable> error : this.errors.entries()) {
                FMLLog.log(Level.SEVERE, (Throwable)error.getValue(), "Caught exception from %s", new Object[]{error.getKey()});
                if (error.getValue() instanceof IFMLHandledException) {
                    toThrow = (Throwable)error.getValue();
                } else if (toThrow == null) {
                    toThrow = (Throwable)error.getValue();
                }
            }

            if (toThrow != null && toThrow instanceof RuntimeException) {
                throw (RuntimeException)toThrow;
            } else {
                throw new LoaderException(toThrow);
            }
        }
    }

    public ModContainer activeContainer() {
        return this.activeContainer;
    }

    @Subscribe
    public void propogateStateMessage(FMLStateEvent stateEvent) {
        if (stateEvent instanceof FMLPreInitializationEvent) {
            this.modObjectList = this.buildModObjectList();
        }

        for(ModContainer mc : this.activeModList) {
            this.activeContainer = mc;
            String modId = mc.getModId();
            stateEvent.applyModContainer(this.activeContainer());
            FMLLog.finer("Posting state event %s to mod %s", new Object[]{stateEvent.getEventType(), modId});
            ((EventBus)this.eventChannels.get(modId)).post(stateEvent);
            FMLLog.finer("State event %s delivered to mod %s", new Object[]{stateEvent.getEventType(), modId});
            this.activeContainer = null;
            if (!this.errors.containsKey(modId)) {
                this.modStates.put(modId, stateEvent.getModState());
            } else {
                this.modStates.put(modId, ModState.ERRORED);
            }
        }

    }

    public ImmutableBiMap<ModContainer, Object> buildModObjectList() {
        com.google.common.collect.ImmutableBiMap.Builder<ModContainer, Object> builder = ImmutableBiMap.builder();

        for(ModContainer mc : this.activeModList) {
            if (!mc.isImmutable() && mc.getMod() != null) {
                builder.put(mc, mc.getMod());
            }

            if (mc.getMod() == null && !mc.isImmutable() && this.state != LoaderState.CONSTRUCTING) {
                FMLLog.severe("There is a severe problem with %s - it appears not to have constructed correctly", new Object[]{mc.getModId()});
                if (this.state != LoaderState.CONSTRUCTING) {
                    this.errorOccurred(mc, new RuntimeException());
                }
            }
        }

        return builder.build();
    }

    public void errorOccurred(ModContainer modContainer, Throwable exception) {
        if (exception instanceof InvocationTargetException) {
            this.errors.put(modContainer.getModId(), ((InvocationTargetException)exception).getCause());
        } else {
            this.errors.put(modContainer.getModId(), exception);
        }

    }

    public void printModStates(StringBuilder ret) {
        for(ModContainer mc : this.loader.getModList()) {
            ret.append("\n\t").append(mc.getModId()).append(" [").append(mc.getName()).append("] (").append(mc.getSource().getName()).append(") ");
            Joiner.on("->").appendTo(ret, this.modStates.get(mc.getModId()));
        }

    }

    public List<ModContainer> getActiveModList() {
        return this.activeModList;
    }

    public ModState getModState(ModContainer selectedMod) {
        return (ModState)Iterables.getLast(this.modStates.get(selectedMod.getModId()), ModState.AVAILABLE);
    }

    public void distributeStateMessage(Class<?> customEvent) {
        try {
            this.masterChannel.post(customEvent.newInstance());
        } catch (Exception var3) {
            FMLLog.log(Level.SEVERE, var3, "An unexpected exception", new Object[0]);
            throw new LoaderException(var3);
        }
    }

    public BiMap<ModContainer, Object> getModObjectList() {
        if (this.modObjectList == null) {
            FMLLog.severe(
                    "Detected an attempt by a mod %s to perform game activity during mod construction. This is a serious programming error.",
                    new Object[]{this.activeContainer}
            );
            return this.buildModObjectList();
        } else {
            return ImmutableBiMap.copyOf(this.modObjectList);
        }
    }

    public boolean isInState(LoaderState state) {
        return this.state == state;
    }
}
