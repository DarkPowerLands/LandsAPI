package ru.landsproject.api.configuration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.ObjectMappingException;

import java.util.List;
import java.util.Map;

public interface ConfigurationMap {
    @Nullable
    String getString(@NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable
    String getString(@NotNull String path, @Nullable String def);

    boolean isString(@NotNull String path);
    
    boolean isSet(@NotNull String path);

    int getInt(@NotNull String path);

    int getInt(@NotNull String path, int def);

    boolean isInt(@NotNull String path);

    boolean getBoolean(@NotNull String path);

    boolean getBoolean(@NotNull String path, boolean def);

    boolean isBoolean(@NotNull String path);

    double getDouble(@NotNull String path);

    double getDouble(@NotNull String path, double def);

    boolean isDouble(@NotNull String path);

    long getLong(@NotNull String path);

    long getLong(@NotNull String path, long def);

    boolean isLong(@NotNull String path);

    @Nullable
    ConfigurationSection getConfigurationSection(@NotNull String path);

    @Nullable
    List<?> getList(@NotNull String path) throws ObjectMappingException;

    @Contract("_, !null -> !null")
    @Nullable
    List<?> getList(@NotNull String path, @Nullable List<?> def) throws ObjectMappingException;

    boolean isList(@NotNull String path);

    @NotNull
    List<String> getStringList(@NotNull String path) throws ObjectMappingException;

    @NotNull
    List<Integer> getIntegerList(@NotNull String path) throws ObjectMappingException;

    @NotNull
    List<Boolean> getBooleanList(@NotNull String path) throws ObjectMappingException;

    @NotNull
    List<Double> getDoubleList(@NotNull String path) throws ObjectMappingException;

    @NotNull
    List<Float> getFloatList(@NotNull String path) throws ObjectMappingException;

    @NotNull
    List<Long> getLongList(@NotNull String path) throws ObjectMappingException;

    @NotNull
    List<Byte> getByteList(@NotNull String path) throws ObjectMappingException;

    @NotNull
    List<Character> getCharacterList(@NotNull String path) throws ObjectMappingException;

    @NotNull
    List<Short> getShortList(@NotNull String path) throws ObjectMappingException;

    @Nullable
    List<Map> getMapList(@NotNull String path) throws ObjectMappingException;

    @Nullable
    <T> T getObject(@NotNull String path, @NotNull Class<T> def);
    @Nullable
    Object getObject(@NotNull String path);
}
