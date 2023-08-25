
package ru.landsproject.api.configuration.abstractconfigure.commented;

import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentedConfigurationNode extends ConfigurationNode {
    Optional<String> getComment();
    CommentedConfigurationNode setComment(String comment);

    // Methods from superclass overridden to have correct return types

    CommentedConfigurationNode getParent();
    @Override
    List<? extends CommentedConfigurationNode> getChildrenList();
    @Override
    Map<Object, ? extends CommentedConfigurationNode> getChildrenMap();
    @Override
    CommentedConfigurationNode setValue(Object value);
    @Override
    CommentedConfigurationNode mergeValuesFrom(ConfigurationNode other);
    @Override
    CommentedConfigurationNode getAppendedNode();
    @Override
    CommentedConfigurationNode getNode(Object... path);
}
