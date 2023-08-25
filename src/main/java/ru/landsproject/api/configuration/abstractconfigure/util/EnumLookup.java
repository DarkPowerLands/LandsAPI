
package ru.landsproject.api.configuration.abstractconfigure.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;
public class EnumLookup {
    private static final LoadingCache<Class<? extends Enum<?>>, Map<String, Enum<?>>> enumFieldCache = CacheBuilder
            .newBuilder()
            .weakKeys()
            .maximumSize(512)
            .build(new CacheLoader<Class<? extends Enum<?>>, Map<String, Enum<?>>>() {
                @Override
                public Map<String, Enum<?>> load(Class<? extends Enum<?>> key) throws Exception {
                    Map<String, Enum<?>> ret = new HashMap<>();
                    for (Enum<?> field : key.getEnumConstants()) {
                        ret.put(field.name(), field);
                        ret.putIfAbsent(processKey(field.name()), field);
                    }
                    return ImmutableMap.copyOf(ret);
                }
            });

    private EnumLookup() {
        // wheeeeeeee
    }

    private static String processKey(String key) {
        checkNotNull(key, "key");
        return "🌸" + key.toLowerCase().replace("_", ""); // stick a flower at the front so processed keys are
        // different from literal keys
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> Optional<T> lookupEnum(Class<T> clazz, String key) {
        checkNotNull(clazz, "clazz");
        try {
            Map<String, Enum<?>> vals = enumFieldCache.get(clazz);
            Enum<?> possibleRet = vals.get(key);
            if (possibleRet != null) {
                return Optional.of((T) possibleRet);
            }
            return Optional.ofNullable((T) vals.get(processKey(key)));
        } catch (ExecutionException e) {
            return Optional.empty();
        }

    }
}
