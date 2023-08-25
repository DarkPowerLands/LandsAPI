package ru.landsproject.api.exception;

import lombok.Getter;
import ru.landsproject.api.database.DatabaseType;

import java.io.IOException;

@Getter
public final class DriverLoadException extends Exception {

    private DatabaseType databaseType;

    public DriverLoadException(String message, DatabaseType databaseType) {
        this(message, null, databaseType);
    }

    public DriverLoadException(Throwable cause, DatabaseType databaseType) {
        this(cause.getMessage(), cause, databaseType);
    }

    public DriverLoadException(String message, Throwable cause, DatabaseType databaseType) {
        super(message, cause, false, false);
        this.databaseType = databaseType;
    }

    public DriverLoadException(IOException ex, DatabaseType databaseType) {
        ex.printStackTrace();
    }
}
