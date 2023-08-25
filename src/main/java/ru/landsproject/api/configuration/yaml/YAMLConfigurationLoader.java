package ru.landsproject.api.configuration.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationOptions;
import ru.landsproject.api.configuration.abstractconfigure.SimpleConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.loader.AbstractConfigurationLoader;
import ru.landsproject.api.configuration.abstractconfigure.loader.CommentHandler;
import ru.landsproject.api.configuration.abstractconfigure.loader.CommentHandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;


public class YAMLConfigurationLoader extends AbstractConfigurationLoader<ConfigurationNode> {
    private final ThreadLocal<Yaml> yaml;

    public static class Builder extends AbstractConfigurationLoader.Builder<Builder> {
        private final DumperOptions options = new DumperOptions();

        protected Builder() {
            setIndent(4);
        }

        public Builder setIndent(int indent) {
            options.setIndent(indent);
            return this;
        }

        public Builder setFlowStyle(DumperOptions.FlowStyle style) {
            options.setDefaultFlowStyle(style);
            return this;
        }


        @Override
        public YAMLConfigurationLoader build() {
            return new YAMLConfigurationLoader(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private YAMLConfigurationLoader(Builder builder) {
        super(builder, new CommentHandler[] {CommentHandlers.HASH});
        final DumperOptions opts = builder.options;
        this.yaml = new ThreadLocal<Yaml>() {
            @Override
            protected Yaml initialValue() {
                return new Yaml(opts);
            }
        };
    }

    @Override
    protected void loadInternal(ConfigurationNode node, BufferedReader reader) throws IOException {
        node.setValue(yaml.get().load(reader));
    }

    @Override
    protected void saveInternal(ConfigurationNode node, Writer writer) throws IOException {
        yaml.get().dump(node.getValue(), writer);
    }

    @Override
    public ConfigurationNode createEmptyNode(ConfigurationOptions options) {
        return SimpleConfigurationNode.root(options);
    }
}
