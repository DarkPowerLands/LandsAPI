package ru.landsproject.api.util.internal;

import org.bukkit.plugin.java.JavaPlugin;
import javax.annotation.Nonnull;

public final class LoaderUtils {
    private static JavaPlugin plugin = null;
    @Nonnull
    public static synchronized JavaPlugin getPlugin() {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(LoaderUtils.class);
        }
        return plugin;
    }
}