
package ru.landsproject.api.configuration.abstractconfigure.objectmapping;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import ru.landsproject.api.configuration.abstractconfigure.ConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.commented.CommentedConfigurationNode;
import ru.landsproject.api.configuration.abstractconfigure.objectmapping.serialize.TypeSerializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ObjectMapper<T> {
    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final Map<String, FieldData> cachedFields = new HashMap<>();


    @SuppressWarnings("unchecked")
    public static <T> ObjectMapper<T> forClass(Class<T> clazz) throws ObjectMappingException {
        return DefaultObjectMapperFactory.getInstance().getMapper(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> ObjectMapper<T>.BoundInstance forObject(T obj) throws ObjectMappingException {
        Preconditions.checkNotNull(obj);
        return forClass((Class<T>) obj.getClass()).bind(obj);
    }

    /**
     * Holder for field-specific information
     */
    protected static class FieldData {
        private final Field field;
        private final TypeToken<?> fieldType;
        private final String comment;

        public FieldData(Field field, String comment) throws
                ObjectMappingException {
            this.field = field;
            this.comment = comment;
            this.fieldType = TypeToken.of(field.getGenericType());
        }

        public void deserializeFrom(Object instance, ConfigurationNode node) throws ObjectMappingException {
            TypeSerializer<?> serial = node.getOptions().getSerializers().get(this.fieldType);
            if (serial == null) {
                throw new ObjectMappingException("No TypeSerializer found for field " + field.getName() + " of type "
                        + this.fieldType);
            }
            Object newVal = node.isVirtual() ? null : serial.deserialize(this.fieldType, node);
            try {
                if (newVal == null) {
                    Object existingVal = field.get(instance);
                    if (existingVal != null) {
                        serializeTo(instance, node);
                    }
                } else {
                    field.set(instance, newVal);
                }
            } catch (IllegalAccessException e) {
                throw new ObjectMappingException("Unable to deserialize field " + field.getName(), e);
            }

        }

        @SuppressWarnings("rawtypes")
        public void serializeTo(Object instance, ConfigurationNode node) throws ObjectMappingException {
            try {
                Object fieldVal = this.field.get(instance);
                if (fieldVal == null) {
                    node.setValue(null);
                } else {
                    TypeSerializer serial = node.getOptions().getSerializers().get(this.fieldType);
                    if (serial == null) {
                        throw new ObjectMappingException("No TypeSerializer found for field " + field.getName() + " of type "
                                + this.fieldType);
                    }
                    serial.serialize(this.fieldType, fieldVal, node);
                }

                if (node instanceof CommentedConfigurationNode && this.comment != null && !this.comment.isEmpty()) {
                    CommentedConfigurationNode commentNode = ((CommentedConfigurationNode) node);
                    if (!commentNode.getComment().isPresent()) {
                        commentNode.setComment(this.comment);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new ObjectMappingException("Unable to serialize field " + field.getName(), e);
            }

        }
    }

    /**
     * Represents an object mapper bound to a certain instance of the object
     */
    public class BoundInstance {
        private final T boundInstance;

        protected BoundInstance(T boundInstance) {
            this.boundInstance = boundInstance;
        }

        /**
         * Populate the annotated fields in a pre-created object
         * @param source The source to get data from
         * @return The object provided, for easier chaining
         * @throws ObjectMappingException If an error occurs while populating data
         */
        public T populate(ConfigurationNode source) throws ObjectMappingException {
            for (Map.Entry<String, FieldData> ent : cachedFields.entrySet()) {
                ConfigurationNode node = source.getNode(ent.getKey());
                ent.getValue().deserializeFrom(boundInstance, node);
            }
            return boundInstance;
        }

        /**
         * Serialize the data contained in annotated fields to the configuration node.
         *
         * @param target The target node to serialize to
         * @throws ObjectMappingException if serialization was not possible due to some error.
         */
        public void serialize(ConfigurationNode target) throws ObjectMappingException {
            for (Map.Entry<String, FieldData> ent : cachedFields.entrySet()) {
                ConfigurationNode node = target.getNode(ent.getKey());
                ent.getValue().serializeTo(boundInstance, node);
            }
        }

        /**
         * Return the instance this mapper is bound to.
         *
         * @return The active instance
         */
        public T getInstance() {
            return boundInstance;
        }
    }

    /**
     * Create a new object mapper of a given type
     *
     * @param clazz The type this object mapper will work with
     * @throws ObjectMappingException if the provided class is in someway invalid
     */
    protected ObjectMapper(Class<T> clazz) throws ObjectMappingException {
        this.clazz = clazz;
        Constructor<T> constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException ignore) {
        }
        this.constructor = constructor;
        Class<? super T> collectClass = clazz;
        do {
            collectFields(cachedFields, collectClass);
        } while (!(collectClass = collectClass.getSuperclass()).equals(Object.class));
    }

    protected void collectFields(Map<String, FieldData> cachedFields, Class<? super T> clazz) throws ObjectMappingException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Setting.class)) {
                Setting setting = field.getAnnotation(Setting.class);
                String path = setting.value();
                if (path.isEmpty()) {
                    path = field.getName();
                }

                FieldData data = new FieldData(field, setting.comment());
                field.setAccessible(true);
                if (!cachedFields.containsKey(path)) {
                    cachedFields.put(path, data);
                }
            }
        }
    }

    /**
     * Create a new instance of an object of the appropriate type. This method is not
     * responsible for any population.
     *
     * @return The new object instance
     * @throws ObjectMappingException If constructing a new instance was not possible
     */
    protected T constructObject() throws ObjectMappingException {
        if (constructor == null) {
            throw new ObjectMappingException("No zero-arg constructor is available for class " + clazz + " but is required to construct new instances!");
        }
        try {
            return constructor.newInstance();
        } catch (InstantiationException e) { // JDK6 compat
            throw new ObjectMappingException("Unable to create instance of target class " + clazz, e);
        } catch (IllegalAccessException e) {
            throw new ObjectMappingException("Unable to create instance of target class " + clazz, e);
        } catch (InvocationTargetException e) {
            throw new ObjectMappingException("Unable to create instance of target class " + clazz, e);
        }
    }

    /**
     * Returns whether this object mapper can create new object instances. This may be
     * false if the provided class has no zero-argument constructors.
     *
     * @return Whether new object instances can be created
     */
    public boolean canCreateInstances() {
        return constructor != null;
    }

    /**
     * Return a view on this mapper that is bound to a single object instance
     *
     * @param instance The instance to bind to
     * @return A view referencing this mapper and the bound instance
     */
    public BoundInstance bind(T instance) {
        return new BoundInstance(instance);
    }

    /**
     * Returns a view on this mapper that is bound to a newly created object instance
     *
     * @see #bind(Object)
     * @return Bound mapper attached to a new object instance
     * @throws ObjectMappingException If the object could not be constructed correctly
     */
    public BoundInstance bindToNew() throws ObjectMappingException {
        return new BoundInstance(constructObject());
    }

    public Class<T> getMappedType() {
        return this.clazz;
    }
}
