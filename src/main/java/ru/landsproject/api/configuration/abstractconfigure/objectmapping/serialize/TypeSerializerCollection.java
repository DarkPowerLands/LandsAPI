
package ru.landsproject.api.configuration.abstractconfigure.objectmapping.serialize;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class TypeSerializerCollection {
    private final TypeSerializerCollection parent;
    private final Map<TypeToken<?>, TypeSerializer<?>> typeMatches = new ConcurrentHashMap<>();
    private final Map<Predicate<TypeToken<?>>, TypeSerializer<?>> functionMatches = new ConcurrentHashMap<>();

    TypeSerializerCollection(TypeSerializerCollection parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    public <T> TypeSerializer<T> get(TypeToken<T> type) {
        Preconditions.checkNotNull(type, "type");
        type = type.wrap();
        TypeSerializer<?> serial = typeMatches.get(type);
        if (serial == null) {
            for (Map.Entry<TypeToken<?>, TypeSerializer<?>> ent : typeMatches.entrySet()) {
                if (ent.getKey().isSupertypeOf(type)) {
                    serial = ent.getValue();
                    typeMatches.put(type, serial);
                    break;
                }
            }
        }

        if (serial == null) {
            for (Map.Entry<Predicate<TypeToken<?>>, TypeSerializer<?>> ent : functionMatches.entrySet()) {
                if (ent.getKey().test(type)) {
                    serial = ent.getValue();
                    typeMatches.put(type, serial);
                    break;
                }
            }
        }

        if (serial == null && parent != null) {
            serial = parent.get(type);
        }

        return (TypeSerializer) serial;
    }

    /**
     * Register a type serializer for a given type. Serializers registered will match all subclasses of the provided
     * type, as well as unwrapped primitive equivalents of the type.
     *
     * @param type The type to accept
     * @param serializer The serializer that will be serialized with
     * @param <T> The type to generify around
     * @return this
     */
    public <T> TypeSerializerCollection registerType(TypeToken<T> type, TypeSerializer<? super T> serializer) {
        Preconditions.checkNotNull(type, "type");
        Preconditions.checkNotNull(serializer, "serializer");
        typeMatches.put(type, serializer);
        return this;
    }

    /**
     * Register a type serializer matching against a given predicate.
     *
     * @param type The predicate to match types against
     * @param serializer The serializer to serialize matching types with
     * @param <T> The type parameter
     * @return this
     */
    @SuppressWarnings("unchecked")
    public <T> TypeSerializerCollection registerPredicate(Predicate<TypeToken<T>> type, TypeSerializer<? super T>
            serializer) {
        Preconditions.checkNotNull(type, "type");
        Preconditions.checkNotNull(serializer, "serializer");
        functionMatches.put((Predicate) type, serializer);
        return this;
    }

    public TypeSerializerCollection newChild() {
        return new TypeSerializerCollection(this);
    }

}
