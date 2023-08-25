package ru.landsproject.api.localization;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Localization {
    @Nullable
    String getTranslated(@NotNull String key);

    @Nullable
    default Material getMaterial(@NotNull String key) {
        return Material.AIR;
    }
}
