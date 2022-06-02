package cpw.mods.fml.common.network;

import com.google.common.base.Strings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.network.NetworkMod.VersionCheckHandler;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Level;
import net.minecraft.item.Item;

public class NetworkModHandler {
    private static Object connectionHandlerDefaultValue;
    private static Object packetHandlerDefaultValue;
    private static Object clientHandlerDefaultValue;
    private static Object serverHandlerDefaultValue;
    private static Object tinyPacketHandlerDefaultValue;
    private static int assignedIds = 1;
    private int localId;
    private int networkId;
    private ModContainer container;
    private NetworkMod mod;
    private Method checkHandler;
    private VersionRange acceptableRange;
    private ITinyPacketHandler tinyPacketHandler;

    public NetworkModHandler(ModContainer container, NetworkMod modAnnotation) {
        this.container = container;
        this.mod = modAnnotation;
        this.localId = assignedIds++;
        this.networkId = this.localId;
        if (Item.MAP.id == assignedIds) {
            ++assignedIds;
        }

    }

    public NetworkModHandler(ModContainer container, Class<?> networkModClass, ASMDataTable table) {
        this(container, (NetworkMod)networkModClass.getAnnotation(NetworkMod.class));
        if (this.mod != null) {
            Set<ASMData> versionCheckHandlers = table.getAnnotationsFor(container).get(VersionCheckHandler.class.getName());
            String versionCheckHandlerMethod = null;

            for(ASMData vch : versionCheckHandlers) {
                if (vch.getClassName().equals(networkModClass.getName())) {
                    versionCheckHandlerMethod = vch.getObjectName();
                    break;
                }
            }

            if (versionCheckHandlerMethod != null) {
                try {
                    Method checkHandlerMethod = networkModClass.getDeclaredMethod(versionCheckHandlerMethod, String.class);
                    if (checkHandlerMethod.isAnnotationPresent(VersionCheckHandler.class)) {
                        this.checkHandler = checkHandlerMethod;
                    }
                } catch (Exception var12) {
                    FMLLog.log(
                            Level.WARNING,
                            var12,
                            "The declared version check handler method %s on network mod id %s is not accessible",
                            new Object[]{versionCheckHandlerMethod, container.getModId()}
                    );
                }
            }

            if (this.checkHandler == null) {
                String versionBounds = this.mod.versionBounds();
                if (!Strings.isNullOrEmpty(versionBounds)) {
                    try {
                        this.acceptableRange = VersionRange.createFromVersionSpec(versionBounds);
                    } catch (InvalidVersionSpecificationException var11) {
                        FMLLog.log(
                                Level.WARNING, var11, "Invalid bounded range %s specified for network mod id %s", new Object[]{versionBounds, container.getModId()}
                        );
                    }
                }
            }

            FMLLog.finest("Testing mod %s to verify it accepts its own version in a remote connection", new Object[]{container.getModId()});
            boolean acceptsSelf = this.acceptVersion(container.getVersion());
            if (!acceptsSelf) {
                FMLLog.severe(
                        "The mod %s appears to reject its own version number (%s) in its version handling. This is likely a severe bug in the mod!",
                        new Object[]{container.getModId(), container.getVersion()}
                );
            } else {
                FMLLog.finest("The mod %s accepts its own version (%s)", new Object[]{container.getModId(), container.getVersion()});
            }

            this.tryCreatingPacketHandler(container, this.mod.packetHandler(), this.mod.channels(), null);
            if (FMLCommonHandler.instance().getSide().isClient() && this.mod.clientPacketHandlerSpec() != this.getClientHandlerSpecDefaultValue()) {
                this.tryCreatingPacketHandler(
                        container, this.mod.clientPacketHandlerSpec().packetHandler(), this.mod.clientPacketHandlerSpec().channels(), Side.CLIENT
                );
            }

            if (this.mod.serverPacketHandlerSpec() != this.getServerHandlerSpecDefaultValue()) {
                this.tryCreatingPacketHandler(
                        container, this.mod.serverPacketHandlerSpec().packetHandler(), this.mod.serverPacketHandlerSpec().channels(), Side.SERVER
                );
            }

            if (this.mod.connectionHandler() != this.getConnectionHandlerDefaultValue()) {
                IConnectionHandler instance;
                try {
                    instance = (IConnectionHandler)this.mod.connectionHandler().newInstance();
                } catch (Exception var10) {
                    FMLLog.log(Level.SEVERE, var10, "Unable to create connection handler instance %s", new Object[]{this.mod.connectionHandler().getName()});
                    throw new FMLNetworkException(var10);
                }

                NetworkRegistry.instance().registerConnectionHandler(instance);
            }

            if (this.mod.tinyPacketHandler() != this.getTinyPacketHandlerDefaultValue()) {
                try {
                    this.tinyPacketHandler = (ITinyPacketHandler)this.mod.tinyPacketHandler().newInstance();
                } catch (Exception var9) {
                    FMLLog.log(Level.SEVERE, var9, "Unable to create tiny packet handler instance %s", new Object[]{this.mod.tinyPacketHandler().getName()});
                    throw new FMLNetworkException(var9);
                }
            }

        }
    }

    private void tryCreatingPacketHandler(ModContainer container, Class<? extends IPacketHandler> clazz, String[] channels, Side side) {
        if (side == null || !side.isClient() || FMLCommonHandler.instance().getSide().isClient()) {
            if (clazz != this.getPacketHandlerDefaultValue()) {
                if (channels.length == 0) {
                    FMLLog.log(
                            Level.WARNING,
                            "The mod id %s attempted to register a packet handler without specifying channels for it",
                            new Object[]{container.getModId()}
                    );
                } else {
                    IPacketHandler instance;
                    try {
                        instance = (IPacketHandler)clazz.newInstance();
                    } catch (Exception var10) {
                        FMLLog.log(
                                Level.SEVERE,
                                var10,
                                "Unable to create a packet handler instance %s for mod %s",
                                new Object[]{clazz.getName(), container.getModId()}
                        );
                        throw new FMLNetworkException(var10);
                    }

                    for(String channel : channels) {
                        NetworkRegistry.instance().registerChannel(instance, channel, side);
                    }
                }
            } else if (channels.length > 0) {
                FMLLog.warning("The mod id %s attempted to register channels without specifying a packet handler", new Object[]{container.getModId()});
            }

        }
    }

    private Object getConnectionHandlerDefaultValue() {
        try {
            if (connectionHandlerDefaultValue == null) {
                connectionHandlerDefaultValue = NetworkMod.class.getMethod("connectionHandler").getDefaultValue();
            }

            return connectionHandlerDefaultValue;
        } catch (NoSuchMethodException var2) {
            throw new RuntimeException("Derp?", var2);
        }
    }

    private Object getPacketHandlerDefaultValue() {
        try {
            if (packetHandlerDefaultValue == null) {
                packetHandlerDefaultValue = NetworkMod.class.getMethod("packetHandler").getDefaultValue();
            }

            return packetHandlerDefaultValue;
        } catch (NoSuchMethodException var2) {
            throw new RuntimeException("Derp?", var2);
        }
    }

    private Object getTinyPacketHandlerDefaultValue() {
        try {
            if (tinyPacketHandlerDefaultValue == null) {
                tinyPacketHandlerDefaultValue = NetworkMod.class.getMethod("tinyPacketHandler").getDefaultValue();
            }

            return tinyPacketHandlerDefaultValue;
        } catch (NoSuchMethodException var2) {
            throw new RuntimeException("Derp?", var2);
        }
    }

    private Object getClientHandlerSpecDefaultValue() {
        try {
            if (clientHandlerDefaultValue == null) {
                clientHandlerDefaultValue = NetworkMod.class.getMethod("clientPacketHandlerSpec").getDefaultValue();
            }

            return clientHandlerDefaultValue;
        } catch (NoSuchMethodException var2) {
            throw new RuntimeException("Derp?", var2);
        }
    }

    private Object getServerHandlerSpecDefaultValue() {
        try {
            if (serverHandlerDefaultValue == null) {
                serverHandlerDefaultValue = NetworkMod.class.getMethod("serverPacketHandlerSpec").getDefaultValue();
            }

            return serverHandlerDefaultValue;
        } catch (NoSuchMethodException var2) {
            throw new RuntimeException("Derp?", var2);
        }
    }

    public boolean requiresClientSide() {
        return this.mod.clientSideRequired();
    }

    public boolean requiresServerSide() {
        return this.mod.serverSideRequired();
    }

    public boolean acceptVersion(String version) {
        if (this.checkHandler != null) {
            try {
                return this.checkHandler.invoke(this.container.getMod(), version);
            } catch (Exception var3) {
                FMLLog.log(
                        Level.WARNING,
                        var3,
                        "There was a problem invoking the checkhandler method %s for network mod id %s",
                        new Object[]{this.checkHandler.getName(), this.container.getModId()}
                );
                return false;
            }
        } else {
            return this.acceptableRange != null
                    ? this.acceptableRange.containsVersion(new DefaultArtifactVersion(version))
                    : this.container.getVersion().equals(version);
        }
    }

    public int getLocalId() {
        return this.localId;
    }

    public int getNetworkId() {
        return this.networkId;
    }

    public ModContainer getContainer() {
        return this.container;
    }

    public NetworkMod getMod() {
        return this.mod;
    }

    public boolean isNetworkMod() {
        return this.mod != null;
    }

    public void setNetworkId(int value) {
        this.networkId = value;
    }

    public boolean hasTinyPacketHandler() {
        return this.tinyPacketHandler != null;
    }

    public ITinyPacketHandler getTinyPacketHandler() {
        return this.tinyPacketHandler;
    }
}
