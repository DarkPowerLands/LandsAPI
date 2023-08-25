package ru.landsproject.api.database;

import com.j256.ormlite.field.DataPersister;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.configuration.Configuration;
import ru.landsproject.api.configuration.ConfigurationSection;
import ru.landsproject.api.database.credentials.DatabaseCredentials;
import ru.landsproject.api.database.credentials.DatabaseCredentialsParser;
import ru.landsproject.api.exception.CredentialsParseException;
import ru.landsproject.api.exception.DriverLoadException;
import ru.landsproject.api.exception.DriverNotFoundException;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public final class Database {

    private final DatabaseType databaseType;
    private final DatabaseCredentials databaseCredentials;
    private final ConnectionSource bootstrapConnection;

    private final Set<Class<?>> registeredTables;

    public Database(@NotNull Plugin plugin, @NotNull Configuration config) throws
            CredentialsParseException,
            DriverNotFoundException,
            DriverLoadException,
            SQLException
    {
        this(plugin, config, parseDatabaseType(config));
    }

    public Database(@NotNull Plugin plugin, @NotNull Configuration config, @NotNull DatabaseType databaseType) throws
            CredentialsParseException,
            DriverNotFoundException,
            DriverLoadException,
            SQLException
    {
        this.registeredTables = new LinkedHashSet<>();
        this.databaseType = databaseType;

        ConfigurationSection credentialsConfig = config.getConfigurationSection("database." + databaseType.getKey());
        this.databaseCredentials = DatabaseCredentialsParser.parse(plugin, credentialsConfig, databaseType);
        this.databaseCredentials.loadDriver(plugin);

        this.bootstrapConnection = establishConnection();
    }

    private static @NotNull DatabaseType parseDatabaseType(@NotNull Configuration config) throws IllegalArgumentException {
        String rawType = config.getString("database.type", "");
        DatabaseType databaseType = DatabaseType.getByKey(rawType);

        if(databaseType.isUnknown())
            throw new IllegalArgumentException(String.format("Unknown database type '%s'!", rawType));

        return databaseType;
    }

    public @NotNull DatabaseType getDatabaseType() {
        return databaseType;
    }

    public @NotNull ConnectionSource getBootstrapConnection() {
        if(bootstrapConnection == null)
            throw new IllegalStateException("The database instance wasn't initialized correctly!");

        return bootstrapConnection;
    }

    public @NotNull ConnectionSource establishConnection() throws SQLException {
        return databaseCredentials.getConnectionSource();
    }

    public @NotNull Database complete() throws SQLException {
        ConnectionSource bootstrapConnection = getBootstrapConnection();
        if(!registeredTables.isEmpty())
            for(Class<?> registeredTable : registeredTables)
                TableUtils.createTableIfNotExists(bootstrapConnection, registeredTable);

        bootstrapConnection.closeQuietly();
        return this;
    }

    public @NotNull Database registerTable(@NotNull Class<?> tableClass) {
        registeredTables.add(tableClass);
        return this;
    }

    public @NotNull Database registerPersister(@NotNull DataPersister dataPersister) {
        DataPersisterManager.registerDataPersisters(dataPersister);
        return this;
    }

}
