package ru.landsproject.api.configuration.abstractconfigure.objectmapping;
public interface ObjectMapperFactory {
    <T> ObjectMapper<T> getMapper(Class<T> type) throws ObjectMappingException;
}
