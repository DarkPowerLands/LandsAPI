/**
 * Configurate
 * Copyright (C) zml and Configurate contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.landsproject.api.configuration.abstractconfigure;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.DefaultObjectMapperFactory;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.ObjectMapperFactory;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.serialize.TypeSerializerCollection;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.serialize.TypeSerializers;
import ru.landsproject.api.configuration.abstractconfigure.util.MapFactories;
import ru.landsproject.api.configuration.abstractconfigure.util.MapFactory;

import java.util.Set;
public class ConfigurationOptions {
    private final MapFactory mapSupplier;
    private final String header;
    private final TypeSerializerCollection serializers;
    private final ImmutableSet<Class<?>> acceptedTypes;
    private final ObjectMapperFactory objectMapperFactory;
    private final boolean shouldCopyDefaults;

    private ConfigurationOptions(MapFactory mapSupplier, String header,
                                 TypeSerializerCollection serializers, Set<Class<?>> acceptedTypes, ObjectMapperFactory objectMapperFactory, boolean shouldCopyDefaults) {
        this.mapSupplier = mapSupplier;
        this.header = header;
        this.serializers = serializers;
        this.acceptedTypes = acceptedTypes == null ? null : ImmutableSet.copyOf(acceptedTypes);
        this.objectMapperFactory = objectMapperFactory;
        this.shouldCopyDefaults = shouldCopyDefaults;
    }
    public static ConfigurationOptions defaults() {
        return new ConfigurationOptions(MapFactories.<SimpleConfigurationNode>insertionOrdered(), null, TypeSerializers
                .getDefaultSerializers(), null, DefaultObjectMapperFactory.getInstance(), false);
    }
    public MapFactory getMapFactory() {
        return mapSupplier;
    }
    public ConfigurationOptions setMapFactory(MapFactory factory) {
        Preconditions.checkNotNull(factory, "factory");
        return new ConfigurationOptions(factory, header, serializers, acceptedTypes, objectMapperFactory, shouldCopyDefaults);
    }
    public String getHeader() {
        return this.header;
    }
    public ConfigurationOptions setHeader(String header) {
        return new ConfigurationOptions(mapSupplier, header, serializers, acceptedTypes, objectMapperFactory, shouldCopyDefaults);
    }

    public TypeSerializerCollection getSerializers() {
        return this.serializers;
    }
    public ConfigurationOptions setSerializers(TypeSerializerCollection serializers) {
        return new ConfigurationOptions(mapSupplier, header, serializers, acceptedTypes, objectMapperFactory, shouldCopyDefaults);
    }
    public ObjectMapperFactory getObjectMapperFactory() {
        return this.objectMapperFactory;
    }
    public ConfigurationOptions setObjectMapperFactory(ObjectMapperFactory factory) {
        Preconditions.checkNotNull(factory, "factory");
        return new ConfigurationOptions(mapSupplier, header, serializers, acceptedTypes, factory, shouldCopyDefaults);
    }
    public boolean acceptsType(Class<?> type) {
        if (this.acceptedTypes == null) {
            return true;
        }
        if (this.acceptedTypes.contains(type)) {
            return true;
        }

        for (Class<?> clazz : this.acceptedTypes) {
            if (clazz.isAssignableFrom(type)) {
                return true;
            }
        }

        return false;
    }
    public ConfigurationOptions setAcceptedTypes(Set<Class<?>> acceptedTypes) {
        return new ConfigurationOptions(mapSupplier, header, serializers, acceptedTypes, objectMapperFactory, shouldCopyDefaults);
    }
    public boolean shouldCopyDefaults() {
        return shouldCopyDefaults;
    }
    public ConfigurationOptions setShouldCopyDefaults(boolean shouldCopyDefaults) {
        return new ConfigurationOptions(mapSupplier, header, serializers, acceptedTypes, objectMapperFactory, shouldCopyDefaults);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurationOptions)) return false;
        ConfigurationOptions that = (ConfigurationOptions) o;
        return Objects.equal(shouldCopyDefaults, that.shouldCopyDefaults) &&
                Objects.equal(mapSupplier, that.mapSupplier) &&
                Objects.equal(header, that.header) &&
                Objects.equal(serializers, that.serializers) &&
                Objects.equal(acceptedTypes, that.acceptedTypes) &&
                Objects.equal(objectMapperFactory, that.objectMapperFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mapSupplier, header, serializers, acceptedTypes, objectMapperFactory, shouldCopyDefaults);
    }
}
