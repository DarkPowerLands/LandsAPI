package ru.landsproject.api.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Version implements Comparable<Version> {
    v1_20_R3(23),
    v1_20_R2(22),
    v1_20_R1(21),
    v1_19_R3(20),
    v1_19_R2(19),
    v1_19_R1(18),
    v1_18_R2(17),
    v1_18_R1(16),
    v1_17_R1(15),
    v1_16_R3(14),
    v1_16_R2(13),
    v1_16_R1(12),
    v1_15_R1(11),
    v1_14_R1(10),
    v1_13_R2(9),
    v1_13_R1(8),
    v1_12_R1(7),
    v1_11_R1(6),
    v1_10_R1(5),
    v1_9_R2(4),
    v1_9_R1(3),
    v1_8_R3(2),
    v1_8_R2(1),
    v1_8_R1(0),
    UNKNOWN(-1);

    private final int value;

    Version(int value) {
        this.value = value;
    }

    @NotNull
    public static Version getServerVersion(@NotNull Server server) {
        Validate.notNull(server, "Server cannot be null");
        String packageName = server.getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        try {
            return valueOf(version.trim());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static boolean isPaper(@NotNull Server server) {
        Validate.notNull(server, "Server cannot be null");
        return server.getName().equalsIgnoreCase("Paper");
    }

    public static boolean isSupportedVersion(@Nullable Logger logger, @NotNull Version serverVersion, @NotNull Version... supportedVersions) {
        for (Version version : supportedVersions) {
            if (version == serverVersion)
                return true;
        }
        if (logger == null)
            return false;
        logger.warning(String.format("The Server version which you are running is unsupported, you are running version '%s'.", new Object[] { serverVersion }));
        logger.warning(String.format("The plugin supports following versions %s.", new Object[] { combineVersions(supportedVersions) }));
        if (serverVersion == UNKNOWN) {
            logger.warning(String.format("The Version '%s' can indicate, that you are using a newer Minecraft version than currently supported.", new Object[] { serverVersion }));
            logger.warning("In this case please update to the newest version of this plugin. If this is the newest Version, than please be patient. It can take some weeks until the plugin is updated.");
        }
        logger.log(Level.WARNING, "No compatible Server version found!", new IllegalStateException("No compatible Server version found!"));
        return false;
    }

    @NotNull
    private static String combineVersions(@NotNull Version... versions) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Version version : versions) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(" ");
            }
            stringBuilder.append("'");
            stringBuilder.append(version);
            stringBuilder.append("'");
        }
        return stringBuilder.toString();
    }

    public boolean isNewerThan(@NotNull Version version) {
        Validate.notNull(version, "Version cannot be null");
        Validate.isTrue((this != UNKNOWN), "Cannot check, if version UNKNOWN is newer");
        Validate.isTrue((version != UNKNOWN), "Cannot check, if version UNKNOWN is newer");
        return (this.value > version.value);
    }

    public boolean isNewerOrSameThan(@NotNull Version version) {
        Validate.notNull(version, "Version cannot be null");
        Validate.isTrue((this != UNKNOWN), "Cannot check, if version UNKNOWN is newer or same");
        Validate.isTrue((version != UNKNOWN), "Cannot check, if version UNKNOWN is newer or same");
        return (this.value >= version.value);
    }

    public boolean isOlderThan(@NotNull Version version) {
        Validate.notNull(version, "Version cannot be null");
        Validate.isTrue((this != UNKNOWN), "Cannot check, if version UNKNOWN is older");
        Validate.isTrue((version != UNKNOWN), "Cannot check, if version UNKNOWN is older");
        return (this.value < version.value);
    }

    public boolean isOlderOrSameThan(@NotNull Version version) {
        Validate.notNull(version, "Version cannot be null");
        Validate.isTrue((this != UNKNOWN), "Cannot check, if version UNKNOWN is older or same");
        Validate.isTrue((version != UNKNOWN), "Cannot check, if version UNKNOWN is older or same");
        return (this.value <= version.value);
    }
}
