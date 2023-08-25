package ru.landsproject.api.configuration.abstractconfigure;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.util.Collections;

class ScalarConfigValue extends ConfigValue {
    private volatile Object value;

    ScalarConfigValue(SimpleConfigurationNode holder) {
        super(holder);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        Preconditions.checkNotNull(value);

        if (!holder.getOptions().acceptsType(value.getClass())) {
            throw new IllegalArgumentException("Configuration does not accept objects of type " + value
                    .getClass());
        }

        this.value = value;
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
       this.value = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScalarConfigValue that = (ScalarConfigValue) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
