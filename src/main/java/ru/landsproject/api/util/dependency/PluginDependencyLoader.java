
package ru.landsproject.api.util.dependency;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.util.URLClassLoaderAccess;
import ru.landsproject.api.util.internal.LoaderUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public final class PluginDependencyLoader implements DependencyLoader {


    public static DependencyLoader forPlugin(final @NotNull Plugin plugin) {
        DependencyLoader dependencyLoader = null;
        try {
            dependencyLoader = new PluginDependencyLoader(plugin);
        } catch (IllegalArgumentException ignore) {
        }
        return dependencyLoader;
    }

    private final URLClassLoader pluginClassLoader;

    private PluginDependencyLoader(final @NotNull Plugin plugin) {
        ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
        if (pluginClassLoader instanceof URLClassLoader)
            this.pluginClassLoader = (URLClassLoader) pluginClassLoader;
        else
            throw new IllegalArgumentException("Plugin class loader is not instance of URLClassLoader");
    }

    @SuppressWarnings("Guava")
    private static final Supplier<URLClassLoaderAccess> URL_INJECTOR = Suppliers.memoize(() -> URLClassLoaderAccess.create((URLClassLoader) LoaderUtils.getPlugin().getClass().getClassLoader()));

    @Override
    public boolean load(final @NotNull File dependencyFile) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException {
        URL url = dependencyFile.toPath().toUri().toURL();
        URL_INJECTOR.get().addURL(url);
        return true;
    }

}