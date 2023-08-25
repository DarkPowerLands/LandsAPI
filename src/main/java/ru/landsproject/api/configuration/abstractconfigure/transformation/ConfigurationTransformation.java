package ru.landsproject.api.configuration.abstractconfigure.transformation;

import com.google.common.collect.Iterators;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class ConfigurationTransformation {
    public static final Object WILDCARD_OBJECT = new Object();

    public abstract void apply(ConfigurationNode node);

    public static class NodePath implements Iterable<Object> {
        Object[] arr;
        NodePath() {
        }

        public Object get(int i) {
            return arr[i];
        }

        public int size() {
            return arr.length;
        }

        public Object[] getArray() {
            return Arrays.copyOf(arr, arr.length);
        }

        @Override
        public Iterator<Object> iterator() {
            return Iterators.forArray(arr);
        }
    }

    public static final class Builder {
        private MoveStrategy strategy = MoveStrategy.OVERWRITE;
        private final SortedMap<Object[], TransformAction> actions;

        protected Builder() {
            this.actions = new TreeMap<>(new NodePathComparator());
        }

        public Builder addAction(Object[] path, TransformAction action) {
            actions.put(path, action);
            return this;
        }

        public MoveStrategy getMoveStrategy() {
            return strategy;
        }

        public Builder setMoveStrategy(MoveStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public ConfigurationTransformation build() {
            return new SingleConfigurationTransformation(actions, strategy);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class VersionedBuilder {
        private Object[] versionKey = new Object[] {"version"};
        private final SortedMap<Integer, ConfigurationTransformation> versions = new TreeMap<>();

        protected VersionedBuilder() {}

        public VersionedBuilder setVersionKey(Object... versionKey) {
            this.versionKey = Arrays.copyOf(versionKey, versionKey.length, Object[].class);
            return this;
        }

        public VersionedBuilder addVersion(int version, ConfigurationTransformation transformation) {
            versions.put(version, transformation);
            return this;
        }

        public ConfigurationTransformation build() {
            return new VersionedTransformation(versionKey, versions);
        }
    }

    public static VersionedBuilder versionedBuilder() {
        return new VersionedBuilder();
    }

    public static ConfigurationTransformation chain(ConfigurationTransformation... transformations) {
        return new ChainedConfigurationTransformation(transformations);
    }
}
