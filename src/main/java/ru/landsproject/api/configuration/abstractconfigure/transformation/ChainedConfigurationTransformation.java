
package ru.landsproject.api.configuration.abstractconfigure.transformation;

import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;

import java.util.Arrays;

public class ChainedConfigurationTransformation extends ConfigurationTransformation {
    private final ConfigurationTransformation[] transformations;
    public ChainedConfigurationTransformation(ConfigurationTransformation[] transformations) {
        this.transformations = Arrays.copyOf(transformations, transformations.length);
    }

    @Override
    public void apply(ConfigurationNode node) {
        for (ConfigurationTransformation transformation : transformations) {
            transformation.apply(node);
        }
    }
}
