package ru.landsproject.api.util.interfaces;

public interface Colorful {
    //Default color method
    default String getColor(String text) {
        return text;
    }

}
