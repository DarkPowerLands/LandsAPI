package ru.landsproject.api.configuration.abstractconfigure.transformation;

import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;

import java.util.SortedMap;

class VersionedTransformation extends ConfigurationTransformation {
    private final Object[] versionPath;
    private final SortedMap<Integer, ConfigurationTransformation> versionTransformations;

    VersionedTransformation(Object[] versionPath, SortedMap<Integer, ConfigurationTransformation> versionTransformations) {
        this.versionPath = versionPath;
        this.versionTransformations = versionTransformations;
    }

    @Override
    public void apply(ConfigurationNode node) {
        ConfigurationNode versionNode = node.getNode(versionPath);
        int currentVersion = versionNode.getInt(-1);
        for (SortedMap.Entry<Integer, ConfigurationTransformation> entry : versionTransformations.entrySet()) {
            if (entry.getKey() <= currentVersion) {
                continue;
            }
            entry.getValue().apply(node);
            currentVersion = entry.getKey();
        }
        versionNode.setValue(currentVersion);
    }
}
