package ru.landsproject.api.configuration.abstractconfigure.transformation;

import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;

public enum MoveStrategy {
    MERGE {
        @Override
        public void move(ConfigurationNode source, ConfigurationNode target) {
            target.mergeValuesFrom(source);
        }
    },
    OVERWRITE {
        @Override
        public void move(ConfigurationNode source, ConfigurationNode target) {
            target.setValue(source);
        }
    };

    public abstract void move(ConfigurationNode source, ConfigurationNode target);
}
