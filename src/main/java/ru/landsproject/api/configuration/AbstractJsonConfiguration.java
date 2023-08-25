
package ru.landsproject.api.configuration;

import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.ObjectMapper;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.ObjectMappingException;
import ru.landsproject.api.configuration.json.JSONConfigurationLoader;
import ru.landsproject.api.util.interfaces.Initable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractJsonConfiguration implements AutoCloseable, Initable {

    private static final Logger LOG = Logger.getLogger(AbstractJsonConfiguration.class.getName());

    protected final File configFile;
    protected ObjectMapper<?> objectMapper;
    protected ConfigurationNode configNode;

    public AbstractJsonConfiguration(String path) {
        this.configFile = new File(path);
    }

    @Override
    public void init() {
        try {
            this.objectMapper = ObjectMapper.forClass(this.getClass());
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }

        if (configFile.exists()) {
            try {
                loadConfiguration();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Failed to load configuration, create default...", ex);
                createDefaultConfiguration();
            }
        } else {
            createDefaultConfiguration();
        }
    }

    protected void createDefaultConfiguration() {
        JSONConfigurationLoader loader = JSONConfigurationLoader.builder()
                .setPath(configFile.toPath())
                .build();

        ConfigurationNode rootNode = loader.createEmptyNode();

        try {
            ObjectMapper<AbstractJsonConfiguration>.BoundInstance boundInstance = ObjectMapper.forObject(this);
            boundInstance.serialize(rootNode);
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }

        try {
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfiguration() throws Exception {
        JSONConfigurationLoader loader = JSONConfigurationLoader.builder()
                .setPath(configFile.toPath())
                .build();

        configNode = loader.load();
        ObjectMapper<AbstractJsonConfiguration>.BoundInstance boundInstance = ObjectMapper.forObject(this);
        boundInstance.populate(configNode);
    }

    public void saveConfiguration() throws Exception {
        createDefaultConfiguration();
//        JSONConfigurationLoader loader = JSONConfigurationLoader.builder()
//                .setPath(configFile.toPath())
//                .build();
//
//        ConfigurationNode rootNode = loader.createEmptyNode();
//        loader.save(rootNode);
    }

    @Override
    public void close() {
        try {
            saveConfiguration();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Failed to load configuration, create default...", ex);
        }
    }
}
