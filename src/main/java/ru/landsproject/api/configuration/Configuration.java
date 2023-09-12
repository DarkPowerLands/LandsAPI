package ru.landsproject.api.configuration;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.loader.AbstractConfigurationLoader;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.ObjectMappingException;
import ru.landsproject.api.configuration.json.JSONConfigurationLoader;
import ru.landsproject.api.configuration.yaml.YAMLConfigurationLoader;
import ru.landsproject.api.util.interfaces.Colorful;
import ru.landsproject.api.util.interfaces.Initable;
import ru.landsproject.api.util.stream.StreamTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration implements ConfigurationMap, Initable {
    private String fileName;
    private final File file;
    private final File folder;
    private Colorful colorful;
    private AbstractConfigurationLoader loader;
    private ConfigurationNode configuration;
    private final Map<String, ConfigurationSection> sectionCache = new HashMap<>();
    private Type type;

    public Configuration(String fileName, File folder, Type type) {
        this.fileName = fileName;
        this.file = new File(folder, fileName);
        this.folder = folder;
        this.type = type;
        setColorful(new Colorful() {
            @Override
            public String getColor(String text) {
                return text.replace("&", "§");
            }
        });
    }

    public void useDefaultColorful() {
        setColorful(new Colorful() {
            @Override
            public String getColor(String text) {
                return Messager.color(text);
            }
        });
    }

    @Override
    public void init() {
        setConfiguration(getConfiguration(fileName));
        loadSections();
    }

    public Colorful getColorful() {
        return colorful;
    }

    public void setColorful(Colorful colorful) {
        this.colorful = colorful;
    }
    public File getFile() {
        return file;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setConfiguration(ConfigurationNode configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
    }

    public Type getType() {
        return type;
    }

    public ConfigurationNode getConfiguration() {
        return configuration;
    }

    public ConfigurationNode getConfiguration(String fileName) {
        ConfigurationNode configuration = this.configuration;
        if (configuration == null) {
            File configFile = new File(folder, fileName);
            if (!configFile.exists()) {
                StreamTool.saveResource(getClass(), folder, fileName, false);
            }
            configuration = loadConfiguration(configFile);
            assert configuration != null;
            applyColorToConfiguration(configuration);
        }
        return configuration;
    }

    private ConfigurationNode loadConfiguration(File file) {
        if(type == Type.JSON) {
            loader = JSONConfigurationLoader.builder().setFile(file).build();
        } else if (type == Type.YAML) {
            loader = YAMLConfigurationLoader.builder().setFile(file).build();
        }
        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadSections() {
        sectionCache.clear();
        populateSections(configuration, "", sectionCache);
    }

    private void populateSections(ConfigurationNode node, String path, Map<String, ConfigurationSection> sections) {
        if (node.hasMapChildren()) {
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.getChildrenMap().entrySet()) {
                String key = entry.getKey().toString();
                ConfigurationNode childNode = entry.getValue();
                String childPath = path.isEmpty() ? key : path + "." + key;
                ConfigurationSection section = new ConfigurationSection(childNode);
                sections.put(childPath, section);
                this.populateSections(childNode, childPath, sections);
            }
        }
    }

    private void applyColorToConfiguration(ConfigurationNode configuration) {
        configuration.getChildrenMap().forEach((key, value) -> {
            if (value.hasListChildren()) {
                List<? extends ConfigurationNode> listChildren = value.getChildrenList();
                ArrayList<ConfigurationNode> modifiedListChildren = new ArrayList<>();
                for (ConfigurationNode configurationNode : listChildren) {
                    if (configurationNode.getValue() instanceof String) {
                        String stringValue = (String) configurationNode.getValue();
                        configurationNode.setValue(getColorful().getColor(stringValue));
                    }
                    modifiedListChildren.add(configurationNode);
                }
                value.setValue(modifiedListChildren);
                this.applyColorToConfiguration(value);
            } else if (value.hasMapChildren()) {
                this.applyColorToConfiguration(value);
            } else if (value.getValue() instanceof String) {
                String stringValue = (String) value.getValue();
                value.setValue(getColorful().getColor(stringValue));
            }
        });
    }


    @Override
    public @Nullable String getString(@NotNull String path) {
        return this.getConfiguration().getNode(path.split("\\.")).getString();
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        return this.getConfiguration().getNode(path.split("\\.")).getString(def);
    }

    @Override
    public boolean isString(@NotNull String path) {
        Object value = this.getConfiguration().getNode(path.split("\\.")).getValue();
        return value instanceof String;
    }

    @Override
    public boolean isSet(@NotNull String path) {
        ConfigurationNode subNode = this.getConfiguration().getNode(path.split("\\."));
        return !subNode.isVirtual();
    }

    @Override
    public int getInt(@NotNull String path) {
        return this.getConfiguration().getNode(path.split("\\.")).getInt();
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return this.getConfiguration().getNode(path.split("\\.")).getInt(def);
    }

    @Override
    public boolean isInt(@NotNull String path) {
        Object value = this.getConfiguration().getNode(path.split("\\.")).getValue();
        return value instanceof Integer;
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return this.getConfiguration().getNode(path.split("\\.")).getBoolean();
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        return this.getConfiguration().getNode(path.split("\\.")).getBoolean(def);
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        Object value = this.getConfiguration().getNode(path.split("\\.")).getValue();
        return value instanceof Boolean;
    }

    @Override
    public double getDouble(@NotNull String path) {
        return this.getConfiguration().getNode(path.split("\\.")).getDouble();
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return this.getConfiguration().getNode(path.split("\\.")).getDouble(def);
    }

    @Override
    public boolean isDouble(@NotNull String path) {
        Object value = this.getConfiguration().getNode(path.split("\\.")).getValue();
        return value instanceof Double;
    }

    @Override
    public long getLong(@NotNull String path) {
        return this.getConfiguration().getNode(path.split("\\.")).getLong();
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        return this.getConfiguration().getNode(path.split("\\.")).getLong(def);
    }

    @Override
    public boolean isLong(@NotNull String path) {
        Object value = this.getConfiguration().getNode(path.split("\\.")).getValue();
        return value instanceof Long;
    }

    @Override
    public @Nullable ConfigurationSection getConfigurationSection(@NotNull String path) {
        if (this.sectionCache.containsKey(path)) {
            return this.sectionCache.get(path);
        }
        ConfigurationNode node = this.configuration.getNode(path.split("\\."));
        if (node.isVirtual()) {
            return null;
        }
        ConfigurationSection section = new ConfigurationSection(node);
        this.sectionCache.put(path, section);
        return section;
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path) throws ObjectMappingException {
        if (!isSet(path)) return new ArrayList<>();
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Object.class));
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path, @Nullable List<?> def) throws ObjectMappingException {
        if (!isSet(path)) return def;
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Object.class));
    }

    @Override
    public boolean isList(@NotNull String path) {
        Object value = this.getConfiguration().getNode(path.split("\\.")).getValue();
        return value instanceof List;
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(String.class));
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Integer.class));
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Boolean.class));
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Double.class));
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Float.class));
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Long.class));
    }

    @Override
    public @NotNull List<Byte> getByteList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Byte.class));
    }

    @Override
    public @NotNull List<Character> getCharacterList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Character.class));
    }

    @Override
    public @NotNull List<Short> getShortList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Short.class));
    }

    @Override
    public List<Map> getMapList(@NotNull String path) throws ObjectMappingException {
        return this.getConfiguration().getNode(path.split("\\.")).getList(TypeToken.of(Map.class));
    }

    @Override
    public <T> @Nullable T getObject(@NotNull String path, @NotNull Class<T> def) {
        return (T) this.getConfiguration().getNode(path.split("\\.")).getValue(def);
    }

    @Override
    public Object getObject(@NotNull String path) {
        return this.getConfiguration().getNode(path.split("\\.")).getValue();
    }

}
