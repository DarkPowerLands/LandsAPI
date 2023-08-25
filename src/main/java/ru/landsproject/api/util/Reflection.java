package ru.landsproject.api.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Reflection {


    private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();

    private static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");

    private static String VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");

    private static Pattern MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");

    public static @Nullable Method getMethod(@NotNull Class<?> type, @NotNull String methodName, @Nullable Class<?>... parameters) {
        try {
            return type.getMethod(methodName, parameters);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static @Nullable Method getDeclaredMethod(@NotNull Class<?> type, @NotNull String methodName, @Nullable Class<?>... parameters) {
        try {
            Method method = type.getDeclaredMethod(methodName, parameters);
            method.setAccessible(true);
            return method;
        } catch (Exception ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> @NotNull Optional<T> invokeMethod(@Nullable Method method, @Nullable Object instance, @Nullable Object... parameters) {
        try {
            T returnedValue = (T) method.invoke(instance, parameters);
            return Optional.ofNullable(returnedValue);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public static boolean invokeVoidMethod(@Nullable Method method, @Nullable Object instance, @Nullable Object... parameters) {
        try {
            method.invoke(instance, parameters);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
        return getField(target, name, fieldType, 0);
    }

    public static <T> FieldAccessor<T> getField(String className, String name, Class<T> fieldType) {
        return getField(getClass(className), name, fieldType, 0);
    }

    public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
        return getField(target, null, fieldType, index);
    }

    public static <T> FieldAccessor<T> getField(String className, Class<T> fieldType, int index) {
        return getField(getClass(className), fieldType, index);
    }
    private static Class<?> getCanonicalClass(String canonicalName) {
        try {
            return Class.forName(canonicalName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find " + canonicalName, e);
        }
    }

    public static Class<?> getClass(String lookupName) {
        return getCanonicalClass(expandVariables(lookupName));
    }
    private static String expandVariables(String name) {
        StringBuffer output = new StringBuffer();
        Matcher matcher = MATCH_VARIABLE.matcher(name);
        while (matcher.find()) {
            String replacement, variable = matcher.group(1);
            if ("nms".equalsIgnoreCase(variable)) {
                replacement = NMS_PREFIX;
            } else if ("obc".equalsIgnoreCase(variable)) {
                replacement = OBC_PREFIX;
            } else if ("version".equalsIgnoreCase(variable)) {
                replacement = VERSION;
            } else {
                throw new IllegalArgumentException("Unknown variable: " + variable);
            }
            if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.')
                replacement = replacement + ".";
            matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(output);
        return output.toString();
    }

    private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return new FieldAccessor<T>() {
                    public T get(Object target) {
                        try {
                            return (T)field.get(target);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    public void set(Object target, Object value) {
                        try {
                            field.set(target, value);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    public boolean hasField(Object target) {
                        return field.getDeclaringClass().isAssignableFrom(target.getClass());
                    }
                };
            }
        }
        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    public static @NotNull Set<Method> findAllMethods(@NotNull Class<?> type) {
        Set<Method> methods = new LinkedHashSet<>();
        walkClassTree(type, t -> Collections.addAll(methods, t.getMethods()));
        return methods;
    }

    public static @NotNull Set<Method> findAllDeclaredMethods(@NotNull Class<?> type) {
        Set<Method> methods = new LinkedHashSet<>();
        walkClassTree(type, t -> Collections.addAll(methods, t.getDeclaredMethods()));
        return methods;
    }

    public static @NotNull Set<Field> findAllFields(@NotNull Class<?> type) {
        Set<Field> fields = new LinkedHashSet<>();
        walkClassTree(type, t -> Collections.addAll(fields, t.getFields()));
        return fields;
    }

    public static @NotNull Set<Field> findAllDeclaredFields(@NotNull Class<?> type) {
        Set<Field> fields = new LinkedHashSet<>();
        walkClassTree(type, t -> Collections.addAll(fields, t.getDeclaredFields()));
        return fields;
    }

    public static <A extends Annotation> @NotNull Map<Method, A> findAnnotatedMethods(@NotNull Class<?> type, @NotNull Class<A> annotationType) {
        return extractAnnotations(findAllMethods(type), annotationType, Method::getAnnotation);
    }

    public static <A extends Annotation> @NotNull Map<Method, A> findAnnotatedDeclaredMethods(@NotNull Class<?> type, @NotNull Class<A> annotationType) {
        return extractAnnotations(findAllDeclaredMethods(type), annotationType, Method::getAnnotation);
    }

    public static <A extends Annotation> @NotNull Map<Field, A> findAnnotatedFields(@NotNull Class<?> type, @NotNull Class<A> annotationType) {
        return extractAnnotations(findAllFields(type), annotationType, Field::getAnnotation);
    }

    public static <A extends Annotation> @NotNull Map<Field, A> findAnnotatedDeclaredFields(@NotNull Class<?> type, @NotNull Class<A> annotationType) {
        return extractAnnotations(findAllDeclaredFields(type), annotationType, Field::getAnnotation);
    }

    public static <T, A extends Annotation> @NotNull Map<T, A> extractAnnotations(
            @NotNull Collection<T> collection,
            @NotNull Class<A> annotationType,
            @NotNull BiFunction<T, Class<A>, A> annotationExtractor
    ) {
        Map<T, A> annotatedElements = new LinkedHashMap<>();
        for(T element : collection) {
            A annotation = annotationExtractor.apply(element, annotationType);
            if(annotation != null) {
                annotatedElements.put(element, annotation);
            }
        }

        return annotatedElements;
    }

    private static void walkClassTree(@NotNull Class<?> type, @NotNull Consumer<Class<?>> typeConsumer) {
        Class<?> clazz = type;
        while(clazz != null) {
            typeConsumer.accept(clazz);
            clazz = clazz.getSuperclass();
        }
    }

    public static interface ConstructorInvoker {
        Object invoke(Object... param1VarArgs);
    }

    public static interface MethodInvoker {
        Object invoke(Object param1Object, Object... param1VarArgs);
    }
    public static interface FieldAccessor<T> {
        T get(Object param1Object);

        void set(Object param1Object1, Object param1Object2);

        boolean hasField(Object param1Object);
    }

}
