package ru.landsproject.api.util.interfaces;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Command {


    @NotNull
    String name();
    @NotNull
    String identifier() default "landsapi";
    @Nullable
    String[] aliases() default "";
}
