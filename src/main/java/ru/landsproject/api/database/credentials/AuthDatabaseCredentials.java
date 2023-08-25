package ru.landsproject.api.database.credentials;

import org.jetbrains.annotations.NotNull;

public interface AuthDatabaseCredentials extends DatabaseCredentials {

    @NotNull String getUsername();

    @NotNull String getPassword();

}
