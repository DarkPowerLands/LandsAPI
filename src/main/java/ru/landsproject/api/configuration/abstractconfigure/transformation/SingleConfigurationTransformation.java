
package ru.landsproject.api.configuration.abstractconfigure.transformation;

import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


class SingleConfigurationTransformation extends ConfigurationTransformation {
    private final MoveStrategy strategy;
    private final Map<Object[], TransformAction> actions;
    private final ThreadLocal<NodePath> sharedPath = new ThreadLocal<NodePath>() {
        @Override
        protected NodePath initialValue() {
            return new NodePath();
        }
    };

    protected SingleConfigurationTransformation(Map<Object[], TransformAction> actions, MoveStrategy strategy) {
        this.actions = actions;
        this.strategy = strategy;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void apply(ConfigurationNode node) {
        for (Map.Entry<Object[], TransformAction> ent : actions.entrySet()) {
            applySingleAction(node, ent.getKey(), 0, node, ent.getValue());
        }
    }

    protected void applySingleAction(ConfigurationNode start, Object[] path, int startIdx, ConfigurationNode node,
                                     TransformAction action) {
        for (int i = startIdx; i < path.length; ++i) {
            if (path[i] == WILDCARD_OBJECT) {
                if (node.hasListChildren()) {
                    List<? extends ConfigurationNode> children = node.getChildrenList();
                    for (int cI = 0; cI < children.size(); ++cI) {
                        path[i] = cI;
                        applySingleAction(start, path, i + 1, children.get(cI), action);
                    }
                    path[i] = WILDCARD_OBJECT;
                } else if (node.hasMapChildren()) {
                    for (Map.Entry<Object, ? extends ConfigurationNode> ent : node.getChildrenMap().entrySet()) {
                        path[i] = ent.getKey();
                        applySingleAction(start, path, i + 1, ent.getValue(), action);
                    }
                    path[i] = WILDCARD_OBJECT;
                } else {
                    // No children
                    return;
                }
                return;
            } else {
                node = node.getNode(path[i]);
                if (node.isVirtual()) {
                    return;
                }
            }
        }
        NodePath immutablePath = sharedPath.get();
        immutablePath.arr = path;
        Object[] transformedPath = action.visitPath(immutablePath, node);
        if (transformedPath != null && !Arrays.equals(path, transformedPath)) {
            this.strategy.move(node, start.getNode(transformedPath));
            node.setValue(null);
        }
    }
}
