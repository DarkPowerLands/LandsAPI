
package ru.landsproject.api.configuration.abstractconfigure.loader;

import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationOptions;

import java.io.IOException;

public interface ConfigurationLoader<NodeType extends ConfigurationNode> {
    ConfigurationOptions getDefaultOptions();

    default NodeType load() throws IOException {
        return load(getDefaultOptions());
    }

    NodeType load(ConfigurationOptions options) throws IOException;

    void save(ConfigurationNode node) throws IOException;

    default NodeType createEmptyNode() {
        return createEmptyNode(getDefaultOptions());
    }

    NodeType createEmptyNode(ConfigurationOptions options);
}
