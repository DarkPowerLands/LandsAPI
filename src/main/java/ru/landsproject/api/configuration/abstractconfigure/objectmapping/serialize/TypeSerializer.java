
package ru.landsproject.api.configuration.abstractconfigure.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.ObjectMappingException;

public interface TypeSerializer<T> {

    T deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException;

    void serialize(TypeToken<?> type, T obj, ConfigurationNode value) throws ObjectMappingException;
}
