package ru.landsproject.api.database.credentials;

import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.configuration.ConfigurationSection;
import ru.landsproject.api.database.DatabaseType;
import ru.landsproject.api.database.credentials.local.LocalDatabaseCredentials;
import ru.landsproject.api.database.credentials.remote.RemoteDatabaseCredentials;
import ru.landsproject.api.exception.CredentialsParseException;
import ru.landsproject.api.exception.DriverLoadException;
import ru.landsproject.api.exception.DriverNotFoundException;

import java.sql.SQLException;

public interface DatabaseCredentials {

    static @NotNull DatabaseCredentials parse(
            @NotNull Plugin plugin,
            @NotNull ConfigurationSection config,
            @NotNull DatabaseType databaseType
    ) throws CredentialsParseException {
        return DatabaseCredentialsParser.parse(plugin, config, databaseType);
    }

    @NotNull DatabaseType getDatabaseType();

    @NotNull String getConnectionUrl();

    @NotNull ConnectionSource getConnectionSource() throws SQLException;

    void loadDriver(@NotNull Plugin plugin) throws DriverNotFoundException, DriverLoadException;

    default boolean isAuthRequired() {
        return this instanceof AuthDatabaseCredentials;
    }

    default boolean isLocalDatabase() {
        return this instanceof LocalDatabaseCredentials;
    }

    default boolean isRemoteDatabase() {
        return this instanceof RemoteDatabaseCredentials;
    }

}
