package ru.landsproject.api.configuration.abstractconfigure.transformation;

import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;

@FunctionalInterface
public interface TransformAction {
    Object[] visitPath(SingleConfigurationTransformation.NodePath inputPath, ConfigurationNode valueAtPath);
}
