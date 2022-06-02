package net.minecraftforge.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Event {
    private boolean isCanceled = false;
    private final boolean isCancelable;
    private Event.Result result = Event.Result.DEFAULT;
    private final boolean hasResult;
    private static ListenerList listeners = new ListenerList();

    public Event() {
        this.setup();
        this.isCancelable = this.hasAnnotation(Cancelable.class);
        this.hasResult = this.hasAnnotation(Event.HasResult.class);
    }

    private boolean hasAnnotation(Class annotation) {
        for(Class cls = this.getClass(); cls != Event.class; cls = cls.getSuperclass()) {
            if (cls.isAnnotationPresent(Cancelable.class)) {
                return true;
            }
        }

        return false;
    }

    public boolean isCancelable() {
        return this.isCancelable;
    }

    public boolean isCanceled() {
        return this.isCanceled;
    }

    public void setCanceled(boolean cancel) {
        if (!this.isCancelable()) {
            throw new IllegalArgumentException("Attempted to cancel a uncancelable event");
        } else {
            this.isCanceled = cancel;
        }
    }

    public boolean hasResult() {
        return this.hasResult;
    }

    public Event.Result getResult() {
        return this.result;
    }

    public void setResult(Event.Result value) {
        this.result = value;
    }

    protected void setup() {
    }

    public ListenerList getListenerList() {
        return listeners;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface HasResult {
    }

    public static enum Result {
        DENY,
        DEFAULT,
        ALLOW;

        private Result() {
        }
    }
}
