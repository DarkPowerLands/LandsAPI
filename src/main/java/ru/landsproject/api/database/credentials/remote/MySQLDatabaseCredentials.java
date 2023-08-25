package ru.landsproject.api.database.credentials.remote;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.database.DatabaseType;
import ru.landsproject.api.exception.DriverNotFoundException;

public class MySQLDatabaseCredentials extends AbstractRemoteDatabaseCredentials {

    public static final String LEGACY_DRIVER_CLASS = "com.mysql.jdbc.Driver";
    public static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    public static final String URL_PATTERN = "jdbc:mysql://%s:%d/%s%s";

    public MySQLDatabaseCredentials() {
        super(DatabaseType.MYSQL);
    }

    @Override
    public @NotNull String getConnectionUrl() {
        return String.format(URL_PATTERN, hostname, port, databaseName, formatParameters());
    }
    @Override
    public @NotNull DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
    }

    @Override
    public void loadDriver(@NotNull Plugin plugin) throws DriverNotFoundException {
        try {
            checkDriver(plugin, DRIVER_CLASS);
        } catch (DriverNotFoundException ignored) {
            checkDriver(plugin, LEGACY_DRIVER_CLASS);
        }
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public int getPort() {
        return port;
    }
}
