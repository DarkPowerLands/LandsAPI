
package ru.landsproject.api.configuration.abstractconfigure.commented;

import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationOptions;
import ru.landsproject.api.configuration.abstractconfigure.SimpleConfigurationNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class SimpleCommentedConfigurationNode extends SimpleConfigurationNode implements CommentedConfigurationNode {
    private final AtomicReference<String> comment = new AtomicReference<>();

    public static SimpleCommentedConfigurationNode root() {
        return root(ConfigurationOptions.defaults());
    }

    public static SimpleCommentedConfigurationNode root(ConfigurationOptions options) {
        return new SimpleCommentedConfigurationNode(null, null, options);
    }

    protected SimpleCommentedConfigurationNode(Object path, SimpleConfigurationNode parent, ConfigurationOptions options) {
        super(path, parent, options);
    }

    @Override
    public Optional<String> getComment() {
        return Optional.ofNullable(comment.get());
    }

    @Override
    public SimpleCommentedConfigurationNode setComment(String comment) {
        attachIfNecessary();
        this.comment.set(comment);
        return this;
    }

    @Override
    public SimpleCommentedConfigurationNode getParent() {
        return (SimpleCommentedConfigurationNode) super.getParent();
    }

    @Override
    protected SimpleCommentedConfigurationNode createNode(Object path) {
        return new SimpleCommentedConfigurationNode(path, this, getOptions());
    }

    @Override
    public SimpleCommentedConfigurationNode setValue(Object value) {
        if (value instanceof CommentedConfigurationNode && ((CommentedConfigurationNode) value).getComment().isPresent()) {
            setComment(((CommentedConfigurationNode) value).getComment().get());
        }
        return (SimpleCommentedConfigurationNode)super.setValue(value);
    }

    @Override
    public SimpleCommentedConfigurationNode mergeValuesFrom(ConfigurationNode other) {
        if (other instanceof CommentedConfigurationNode) {
            Optional<String> otherComment = ((CommentedConfigurationNode) other).getComment();
            if (otherComment.isPresent()) {
                comment.compareAndSet(null, otherComment.get());
            }
        }
        return (SimpleCommentedConfigurationNode) super.mergeValuesFrom(other);
    }

    @Override
    public SimpleCommentedConfigurationNode getNode(Object... path) {
        return (SimpleCommentedConfigurationNode)super.getNode(path);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<? extends SimpleCommentedConfigurationNode> getChildrenList() {
        return (List<SimpleCommentedConfigurationNode>) super.getChildrenList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Object, ? extends SimpleCommentedConfigurationNode> getChildrenMap() {
        return (Map<Object, SimpleCommentedConfigurationNode>) super.getChildrenMap();
    }

    @Override
    public SimpleCommentedConfigurationNode getAppendedNode() {
        return (SimpleCommentedConfigurationNode) super.getAppendedNode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleCommentedConfigurationNode)) return false;
        if (!super.equals(o)) return false;

        SimpleCommentedConfigurationNode that = (SimpleCommentedConfigurationNode) o;

        if (!comment.equals(that.comment)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + comment.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SimpleCommentedConfigurationNode{" +
                "super=" + super.toString() +
                "comment=" + comment +
                '}';
    }
}
