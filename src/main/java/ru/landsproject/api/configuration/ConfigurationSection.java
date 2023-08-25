package ru.landsproject.api.configuration;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.ObjectMappingException;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationSection implements ConfigurationMap {

    private ConfigurationNode node;

    public ConfigurationSection(ConfigurationNode node) {
        this.node = node;
    }

    public ConfigurationNode getNode() {
        return node;
    }
    public Set<String> getKeys(boolean deep) {
        HashSet<String> keys = new HashSet<>();
        if (deep) {
            this.populateKeysDeep(keys, this.node, "", true);
        } else {
            keys.addAll(this.node.getChildrenMap().keySet().stream().map(Object::toString).collect(Collectors.toSet()));
        }
        return keys;
    }

    private void populateKeysDeep(Set<String> keys, ConfigurationNode currentNode, String currentPath, boolean deep) {
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : currentNode.getChildrenMap().entrySet()) {
            String key = entry.getKey().toString();
            ConfigurationNode childNode = entry.getValue();
            String childPath = currentPath.isEmpty() ? key : currentPath + "." + key;
            keys.add(childPath);
            if (!deep || !childNode.hasMapChildren()) continue;
            this.populateKeysDeep(keys, childNode, childPath, true);
        }
    }
    
    

    @Override
    public @Nullable String getString(@NotNull String path) {
        return this.getNode().getNode(path.split("\\.")).getString();
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        return this.getNode().getNode(path.split("\\.")).getString(def);
    }

    @Override
    public boolean isString(@NotNull String path) {
        Object value = this.getNode().getNode(path.split("\\.")).getValue();
        return value instanceof String;
    }

    @Override
    public boolean isSet(@NotNull String path) {
        ConfigurationNode subNode = this.getNode().getNode(path.split("\\."));
        return !subNode.isVirtual();
    }

    @Override
    public int getInt(@NotNull String path) {
        return this.getNode().getNode(path.split("\\.")).getInt();
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return this.getNode().getNode(path.split("\\.")).getInt(def);
    }

    @Override
    public boolean isInt(@NotNull String path) {
        Object value = this.getNode().getNode(path.split("\\.")).getValue();
        return value instanceof Integer;
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return this.getNode().getNode(path.split("\\.")).getBoolean();
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        return this.getNode().getNode(path.split("\\.")).getBoolean(def);
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        Object value = this.getNode().getNode(path.split("\\.")).getValue();
        return value instanceof Boolean;
    }

    @Override
    public double getDouble(@NotNull String path) {
        return this.getNode().getNode(path.split("\\.")).getDouble();
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return this.getNode().getNode(path.split("\\.")).getDouble(def);
    }

    @Override
    public boolean isDouble(@NotNull String path) {
        Object value = this.getNode().getNode(path.split("\\.")).getValue();
        return value instanceof Double;
    }

    @Override
    public long getLong(@NotNull String path) {
        return this.getNode().getNode(path.split("\\.")).getLong();
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        return this.getNode().getNode(path.split("\\.")).getLong(def);
    }

    @Override
    public boolean isLong(@NotNull String path) {
        Object value = this.getNode().getNode(path.split("\\.")).getValue();
        return value instanceof Long;
    }

    @Override
    public @Nullable ConfigurationSection getConfigurationSection(@NotNull String path) {
        ConfigurationNode subNode = this.node.getNode(path.split("\\."));
        return new ConfigurationSection(subNode);
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path) throws ObjectMappingException {
        if (!isSet(path)) return new ArrayList<>();
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Object.class));
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path, @Nullable List<?> def) throws ObjectMappingException {
        if (!isSet(path)) return def;
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Object.class));
    }

    @Override
    public boolean isList(@NotNull String path) {
        Object value = this.getNode().getNode(path.split("\\.")).getValue();
        return value instanceof List;
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(String.class));
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Integer.class));
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Boolean.class));
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Double.class));
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Float.class));
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Long.class));
    }

    @Override
    public @NotNull List<Byte> getByteList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Byte.class));
    }

    @Override
    public @NotNull List<Character> getCharacterList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Character.class));
    }

    @Override
    public @NotNull List<Short> getShortList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Short.class));
    }

    @Override
    public List<Map> getMapList(@NotNull String path) throws ObjectMappingException {
        return this.getNode().getNode(path.split("\\.")).getList(TypeToken.of(Map.class));
    }

    @Override
    public <T> @Nullable T getObject(@NotNull String path, @NotNull Class<T> def) {
        return (T) this.getNode().getNode(path.split("\\.")).getValue(def);
    }

    @Override
    public Object getObject(@NotNull String path) {
        return this.getNode().getNode(path.split("\\.")).getValue();
    }
}
