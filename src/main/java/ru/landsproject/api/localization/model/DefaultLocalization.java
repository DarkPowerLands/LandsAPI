package ru.landsproject.api.localization.model;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.landsproject.api.configuration.Configuration;
import ru.landsproject.api.configuration.Type;
import ru.landsproject.api.localization.Localization;

import java.io.File;

public class DefaultLocalization implements Localization {

    private String fileName;

    private Configuration configuration;

    public DefaultLocalization(String fileName, File folder) {
        this.fileName = fileName;

        configuration = new Configuration(fileName, folder, Type.JSON);
    }

    public String getFileName() {
        return fileName;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public @Nullable String getTranslated(@NotNull String key) {
        return getConfiguration().getString(key);
    }

    @Override
    public @Nullable Material getMaterial(@NotNull String key) {
        return Material.valueOf(getTranslated("material." + key.toLowerCase()));
    }
}
