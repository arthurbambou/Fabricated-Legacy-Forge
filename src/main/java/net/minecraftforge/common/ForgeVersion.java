package net.minecraftforge.common;

public class ForgeVersion {
    public static final int majorVersion = 5;
    public static final int minorVersion = 0;
    public static final int revisionVersion = 0;
    public static final int buildVersion = 326;

    public ForgeVersion() {
    }

    public static int getMajorVersion() {
        return 5;
    }

    public static int getMinorVersion() {
        return 0;
    }

    public static int getRevisionVersion() {
        return 0;
    }

    public static int getBuildVersion() {
        return 326;
    }

    public static String getVersion() {
        return String.format("%d.%d.%d.%d", 5, 0, 0, 326);
    }
}
