package cpw.mods.fml.common.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NetworkMod {
    boolean clientSideRequired() default false;

    boolean serverSideRequired() default false;

    String[] channels() default {};

    String versionBounds() default "";

    Class<? extends IPacketHandler> packetHandler() default NetworkMod.NULL.class;

    Class<? extends ITinyPacketHandler> tinyPacketHandler() default NetworkMod.NULL.class;

    Class<? extends IConnectionHandler> connectionHandler() default NetworkMod.NULL.class;

    NetworkMod.SidedPacketHandler clientPacketHandlerSpec() default @NetworkMod.SidedPacketHandler(
            channels = {},
            packetHandler = NetworkMod.NULL.class
    );

    NetworkMod.SidedPacketHandler serverPacketHandlerSpec() default @NetworkMod.SidedPacketHandler(
            channels = {},
            packetHandler = NetworkMod.NULL.class
    );

    public interface NULL extends IPacketHandler, IConnectionHandler, ITinyPacketHandler {
    }

    public @interface SidedPacketHandler {
        String[] channels();

        Class<? extends IPacketHandler> packetHandler();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface VersionCheckHandler {
    }
}
