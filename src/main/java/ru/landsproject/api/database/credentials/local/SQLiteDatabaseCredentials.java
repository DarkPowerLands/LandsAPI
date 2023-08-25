package ru.landsproject.api.database.credentials.local;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.database.DatabaseType;
import ru.landsproject.api.exception.DriverNotFoundException;

public final class SQLiteDatabaseCredentials extends AbstractLocalDatabaseCredentials {

    public static final String DRIVER_CLASS = "org.sqlite.JDBC";
    public static final String URL_PATTERN = "jdbc:sqlite:%s%s";

    public SQLiteDatabaseCredentials(@NotNull Plugin plugin) {
        super(plugin, DatabaseType.SQLITE);
    }
    @Override
    public @NotNull DatabaseType getDatabaseType() {
        return DatabaseType.SQLITE;
    }
    @Override
    public @NotNull String getConnectionUrl() {
        return String.format(URL_PATTERN, getFilePath(), formatParameters());
    }

    @Override
    public void loadDriver(@NotNull Plugin plugin) throws DriverNotFoundException {
        checkDriver(plugin, DRIVER_CLASS);
    }

}
