package ru.landsproject.api.database.credentials.local;

import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.database.credentials.DatabaseCredentials;

public interface LocalDatabaseCredentials extends DatabaseCredentials {

    @NotNull String getFilePath();

}
