package ru.landsproject.api.database.credentials.remote;

import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.database.credentials.DatabaseCredentials;

public interface RemoteDatabaseCredentials extends DatabaseCredentials {

    @NotNull String getHostname();

    int getPort();

}
