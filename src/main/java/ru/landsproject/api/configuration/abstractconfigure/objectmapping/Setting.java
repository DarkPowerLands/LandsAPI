package ru.landsproject.api.configuration.abstractconfigure.objectmapping;

import java.lang.annotation.*;

/**
 * Полями с этой аннотацией являются
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Setting {
    /**
     * Путь, по которому находится этот параметр, находится по адресу
     *
     * @return The path
     */
    public String value() default "";

    /**
     * Комментарий по умолчанию, связанный с этим узлом конфигурации
     * Это будет применено к любому загрузчику конфигурации с поддержкой комментариев
     *
     * @return The comment
     */
    public String comment() default "";
}
