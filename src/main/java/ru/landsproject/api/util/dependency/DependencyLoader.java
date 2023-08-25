package ru.landsproject.api.util.dependency;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public interface DependencyLoader {
    boolean load(File dependencyFile) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException, NoSuchFieldException;

}
