
package ru.landsproject.api.configuration.abstractconfigure;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.ObjectMappingException;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.serialize.TypeSerializer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
public interface ConfigurationNode {
    int NUMBER_DEF = 0;
    Object getKey();
    Object[] getPath();
    ConfigurationNode getParent();
    ConfigurationOptions getOptions();
    default Object getValue() {
        return getValue((Object) null);
    }
    Object getValue(Object def);

    Object getValue(Supplier<Object> defSupplier);

    default <T> T getValue(Function<Object, T> transformer) {
        return getValue(transformer, (T) null);
    }
    <T> T getValue(Function<Object, T> transformer, T def);
    <T> T getValue(Function<Object, T> transformer, Supplier<T> defSupplier);
    <T> List<T> getList(Function<Object, T> transformer);
    <T> List<T> getList(Function<Object, T> transformer, List<T> def);
    <T> List<T> getList(Function<Object, T> transformer, Supplier<List<T>> defSupplier);
    default <T> List<T> getList(TypeToken<T> type) throws ObjectMappingException {
        return getList(type, ImmutableList.of());
    }
    <T> List<T> getList(TypeToken<T> type, List<T> def) throws ObjectMappingException;

    <T> List<T> getList(TypeToken<T> type, Supplier<List<T>> defSupplier) throws ObjectMappingException;

    default String getString() {
        return getString(null);
    }

    default String getString(String def) {
        return getValue(Types::asString, def);
    }

    default float getFloat() {
        return getFloat(NUMBER_DEF);
    }

    default float getFloat(float def) {
        return getValue(Types::asFloat, def);
    }

    default double getDouble() {
        return getDouble(NUMBER_DEF);
    }

    default double getDouble(double def) {
        return getValue(Types::asDouble, def);
    }

    default int getInt() {
        return getInt(NUMBER_DEF);
    }

    default int getInt(int def) {
        return getValue(Types::asInt, def);
    }

    default long getLong() {
        return getLong(NUMBER_DEF);
    }

    default long getLong(long def) {
        return getValue(Types::asLong, def);
    }

    default boolean getBoolean() {
        return getBoolean(false);
    }

    default boolean getBoolean(boolean def) {
        return getValue(Types::asBoolean, def);
    }

    ConfigurationNode setValue(Object value);

    default <T> T getValue(TypeToken<T> type) throws ObjectMappingException {
        return getValue(type, (T) null);
    }

    <T> T getValue(TypeToken<T> type, T def) throws ObjectMappingException;

    <T> T getValue(TypeToken<T> type, Supplier<T> defSupplier) throws ObjectMappingException;
    default <T> ConfigurationNode setValue(TypeToken<T> type, T value) throws ObjectMappingException {
        if (value == null) {
            setValue(null);
            return this;
        }
        TypeSerializer serial = getOptions().getSerializers().get(type);
        if (serial != null) {
            serial.serialize(type, value, this);
        } else if (getOptions().acceptsType(value.getClass())) {
            setValue(value); // Just write if no applicable serializer exists?
        } else {
            throw new ObjectMappingException("No serializer available for type " + type);
        }
        return this;
    }

    ConfigurationNode mergeValuesFrom(ConfigurationNode other);

    boolean hasListChildren();
    boolean hasMapChildren();
    List<? extends ConfigurationNode> getChildrenList();
    Map<Object, ? extends ConfigurationNode> getChildrenMap();
    boolean removeChild(Object key);
    ConfigurationNode getAppendedNode();
    ConfigurationNode getNode(Object... path);
    boolean isVirtual();
}
