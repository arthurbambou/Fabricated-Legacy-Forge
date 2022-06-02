package cpw.mods.fml.common;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;

public enum LoaderState {
    NOINIT("Uninitialized", null),
    LOADING("Loading", null),
    CONSTRUCTING("Constructing mods", FMLConstructionEvent.class),
    PREINITIALIZATION("Pre-initializing mods", FMLPreInitializationEvent.class),
    INITIALIZATION("Initializing mods", FMLInitializationEvent.class),
    POSTINITIALIZATION("Post-initializing mods", FMLPostInitializationEvent.class),
    AVAILABLE("Mod loading complete", FMLLoadCompleteEvent.class),
    SERVER_STARTING("Server starting", FMLServerStartingEvent.class),
    SERVER_STARTED("Server started", FMLServerStartedEvent.class),
    SERVER_STOPPING("Server stopping", FMLServerStoppingEvent.class),
    ERRORED("Mod Loading errored", null);

    private Class<? extends FMLStateEvent> eventClass;
    private String name;

    private LoaderState(String name, Class<? extends FMLStateEvent> event) {
        this.name = name;
        this.eventClass = event;
    }

    public LoaderState transition(boolean errored) {
        if (errored) {
            return ERRORED;
        } else {
            return this == SERVER_STOPPING ? AVAILABLE : values()[this.ordinal() < values().length ? this.ordinal() + 1 : this.ordinal()];
        }
    }

    public boolean hasEvent() {
        return this.eventClass != null;
    }

    public FMLStateEvent getEvent(Object... eventData) {
        try {
            return (FMLStateEvent)this.eventClass.getConstructor(Object[].class).newInstance((Object)eventData);
        } catch (Exception var3) {
            throw Throwables.propagate(var3);
        }
    }

    public LoaderState requiredState() {
        return this == NOINIT ? NOINIT : values()[this.ordinal() - 1];
    }

    public static enum ModState {
        UNLOADED("Unloaded"),
        LOADED("Loaded"),
        CONSTRUCTED("Constructed"),
        PREINITIALIZED("Pre-initialized"),
        INITIALIZED("Initialized"),
        POSTINITIALIZED("Post-initialized"),
        AVAILABLE("Available"),
        DISABLED("Disabled"),
        ERRORED("Errored");

        private String label;

        private ModState(String label) {
            this.label = label;
        }

        public String toString() {
            return this.label;
        }
    }
}
