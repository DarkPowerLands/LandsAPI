package ru.landsproject.api.configuration.abstractconfigure;


import java.util.Collections;

class NullConfigValue extends ConfigValue {
    NullConfigValue(SimpleConfigurationNode holder) {
        super(holder);
    }
    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(Object value) {
    }

    @Override
    SimpleConfigurationNode putChild(Object key, SimpleConfigurationNode value) {
        return null;
    }

    @Override
    SimpleConfigurationNode putChildIfAbsent(Object key, SimpleConfigurationNode value) {
        return null;
    }

    @Override
    public SimpleConfigurationNode getChild(Object key) {
        return null;
    }

    @Override
    public Iterable<SimpleConfigurationNode> iterateChildren() {
        return Collections.emptySet();
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NullConfigValue;
    }

    @Override
    public int hashCode() {
        return 1009;
    }
}
