
package ru.landsproject.api.configuration.json;

import com.fasterxml.jackson.core.*;
import com.google.common.collect.ImmutableSet;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationOptions;
import ru.landsproject.api.configuration.abstractconfigure.commented.CommentedConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.commented.SimpleCommentedConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.loader.AbstractConfigurationLoader;
import ru.landsproject.api.configuration.abstractconfigure.loader.CommentHandler;
import ru.landsproject.api.configuration.abstractconfigure.loader.CommentHandlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JSONConfigurationLoader extends AbstractConfigurationLoader<ConfigurationNode> {

    private final JsonFactory factory;
    private final int indent;
    private final FieldValueSeparatorStyle fieldValueSeparatorStyle;

    public static class Builder extends AbstractConfigurationLoader.Builder<Builder> {

        private final JsonFactory factory = new JsonFactory();
        private int indent = 4;
        private FieldValueSeparatorStyle fieldValueSeparatorStyle = FieldValueSeparatorStyle.SPACE_AFTER;

        protected Builder() {
            factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
            factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
            factory.enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);
            factory.enable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);
            factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
            factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
            factory.enable(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS);
            factory.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        }

        public JsonFactory getFactory() {
            return this.factory;
        }

        public Builder setIndent(int indent) {
            this.indent = indent;
            return this;
        }

        public Builder setFieldValueSeparatorStyle(FieldValueSeparatorStyle style) {
            this.fieldValueSeparatorStyle = style;
            return this;
        }

        @Override
        public JSONConfigurationLoader build() {
            return new JSONConfigurationLoader(this);
        }

        public int getIndent() {
            return indent;
        }

        public FieldValueSeparatorStyle getFieldValueSeparatorStyle() {
            return fieldValueSeparatorStyle;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    protected JSONConfigurationLoader(Builder builder) {
        super(builder, new CommentHandler[]{CommentHandlers.DOUBLE_SLASH, CommentHandlers.SLASH_BLOCK,
            CommentHandlers.HASH});
        this.factory = builder.getFactory();
        this.indent = builder.getIndent();
        this.fieldValueSeparatorStyle = builder.getFieldValueSeparatorStyle();
    }

    @Override
    protected void loadInternal(ConfigurationNode node, BufferedReader reader) throws IOException {
        try (JsonParser parser = factory.createParser(reader)) {
            parser.nextToken();
            parseValue(parser, node);
        }
    }

    private void parseValue(JsonParser parser, ConfigurationNode node) throws IOException {
        JsonToken token = parser.getCurrentToken();
        switch (token) {
            case START_OBJECT:
                parseObject(parser, node);
                break;
            case START_ARRAY:
                parseArray(parser, node);
                break;
            case VALUE_NUMBER_FLOAT:
                double doubleVal = parser.getDoubleValue();
                if ((float) doubleVal != doubleVal) {
                    node.setValue(parser.getDoubleValue());
                } else {
                    node.setValue(parser.getFloatValue());
                }
                break;
            case VALUE_NUMBER_INT:
                long longVal = parser.getLongValue();
                if ((int) longVal != longVal) {
                    node.setValue(parser.getLongValue());
                } else {
                    node.setValue(parser.getIntValue());
                }
                break;
            case VALUE_STRING:
                node.setValue(parser.getText());
                break;
            case VALUE_TRUE:
            case VALUE_FALSE:
                node.setValue(parser.getBooleanValue());
                break;
            case VALUE_NULL: // Ignored values
            case FIELD_NAME:
                break;
            default:
                throw new IOException("Unsupported token type: " + token + " (at " + parser.getTokenLocation() +
                        ")");
        }
    }

    private void parseArray(JsonParser parser, ConfigurationNode node) throws IOException {
        JsonToken token;
        while ((token = parser.nextToken()) != null) {
            switch (token) {
                case END_ARRAY:
                    return;
                default:
                    parseValue(parser, node.getAppendedNode());
            }
        }
        throw new JsonParseException("Reached end of stream with unclosed array!", parser.getCurrentLocation());

    }

    private void parseObject(JsonParser parser, ConfigurationNode node) throws IOException {
        JsonToken token;
        while ((token = parser.nextToken()) != null) {
            switch (token) {
                case END_OBJECT:
                    return;
                default:
                    parseValue(parser, node.getNode(parser.getCurrentName()));
            }
        }
        throw new JsonParseException(parser, "Reached end of stream with unclosed array!", parser.getCurrentLocation());
    }

    @Override
    public void saveInternal(ConfigurationNode node, Writer writer) throws IOException {
        try (JsonGenerator generator = factory.createGenerator(writer)) {
            generator.setPrettyPrinter(new ConfiguratePrettyPrinter(indent, fieldValueSeparatorStyle));
            generateValue(generator, node);
            generator.flush();
            writer.write(SYSTEM_LINE_SEPARATOR); // Jackson doesn't add a newline at the end of files by default
        }
    }

    @Override
    public CommentedConfigurationNode createEmptyNode(ConfigurationOptions options) {
        options = options.setAcceptedTypes(ImmutableSet.of(Map.class, List.class, Double.class, Float.class,
                Long.class, Integer.class, Boolean.class, String.class, byte[].class));
        return SimpleCommentedConfigurationNode.root(options);
    }

    private void generateValue(JsonGenerator generator, ConfigurationNode node) throws IOException {
        if (node.hasMapChildren()) {
            generateObject(generator, node);
        } else if (node.hasListChildren()) {
            generateArray(generator, node);
        } else {
            Object raw = node.getValue();
            if (raw instanceof Double) {
                generator.writeNumber((double) raw);
            } else if (raw instanceof Float) {
                generator.writeNumber((float) raw);
            } else if (raw instanceof Long) {
                generator.writeNumber((long) raw);
            } else if (raw instanceof Integer) {
                generator.writeNumber((int) raw);
            } else if (raw instanceof Boolean) {
                generator.writeBoolean((boolean) raw);
            } else if (raw instanceof byte[]) {
                generator.writeBinary((byte[]) raw);
            } else {
                generator.writeString(raw.toString());
            }
        }
    }

    private void generateComment(JsonGenerator generator, ConfigurationNode node, boolean inArray) throws IOException {
        if (node instanceof CommentedConfigurationNode) {
            CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode) node;
            final Optional<String> comment = commentedConfigurationNode.getComment();
            if (comment.isPresent()) {
                if (indent == 0) {
                    generator.writeRaw("/*");
                    generator.writeRaw(comment.get().replaceAll("\\* /", ""));
                    generator.writeRaw("* /");
                } else {
                    generator.getPrettyPrinter().beforeObjectEntries(generator);
                    for (Iterator<String> it = LINE_SPLITTER.split(comment.get()).iterator(); it.hasNext();) {
                        generator.writeRaw("// ");
                        generator.writeRaw(it.next());
                        if (it.hasNext()) {
                            generator.writeRaw(SYSTEM_LINE_SEPARATOR);
                        }
                    }
                    if (inArray) {
                        generator.writeRaw(SYSTEM_LINE_SEPARATOR);
                    }

                }
            }
        }
    }

    private void generateObject(JsonGenerator generator, ConfigurationNode node) throws IOException {
        if (!node.hasMapChildren()) {
            throw new IOException("Node passed to generateObject does not have map children!");
        }
        generator.writeStartObject();
        for (Map.Entry<Object, ? extends ConfigurationNode> ent : node.getChildrenMap().entrySet()) {
            generateComment(generator, ent.getValue(), false);
            generator.writeFieldName(ent.getKey().toString());
            generateValue(generator, ent.getValue());
        }
        generator.writeEndObject();

    }

    private void generateArray(JsonGenerator generator, ConfigurationNode node) throws IOException {
        if (!node.hasListChildren()) {
            throw new IOException("Node passed to generateArray does not have list children!");
        }
        List<? extends ConfigurationNode> children = node.getChildrenList();
        generator.writeStartArray(children.size());
        for (ConfigurationNode child : children) {
            generateComment(generator, child, true);
            generateValue(generator, child);
        }
        generator.writeEndArray();
    }
}
