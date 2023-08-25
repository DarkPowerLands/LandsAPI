
package ru.landsproject.api.configuration.abstractconfigure.objectmapping;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;


public class DefaultObjectMapperFactory implements ObjectMapperFactory {
    private static final ObjectMapperFactory INSTANCE = new DefaultObjectMapperFactory();
    private final LoadingCache<Class<?>, ObjectMapper<?>> mapperCache = CacheBuilder.newBuilder().weakKeys()
            .maximumSize(500).build(new CacheLoader<>() {
                @Override
                public ObjectMapper<?> load(Class<?> key) throws Exception {
                    return new ObjectMapper<>(key);
                }
            });

    @Override
    @SuppressWarnings("unchecked")
    public <T> ObjectMapper<T> getMapper(Class<T> type) throws ObjectMappingException {
        Preconditions.checkNotNull(type, "type");
        try {
            return (ObjectMapper<T>) mapperCache.get(type);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ObjectMappingException) {
                throw (ObjectMappingException) e.getCause();
            } else {
                throw new ObjectMappingException(e);
            }
        }
    }

    public static ObjectMapperFactory getInstance() {
        return INSTANCE;
    }
}
