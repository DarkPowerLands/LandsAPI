package ru.landsproject.api.database.credentials.local;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.database.DatabaseType;
import ru.landsproject.api.database.credentials.AbstractDatabaseCredentials;
import ru.landsproject.api.database.credentials.CredentialField;

import java.sql.SQLException;

public abstract class AbstractLocalDatabaseCredentials extends AbstractDatabaseCredentials implements LocalDatabaseCredentials {

    protected final Plugin plugin;

    @CredentialField("file")
    protected String filePath;

    protected AbstractLocalDatabaseCredentials(@NotNull Plugin plugin, @NotNull DatabaseType databaseType) {
        super(databaseType);
        this.plugin = plugin;
    }

    public @NotNull String getFilePath() {
        return plugin.getDataFolder().toPath().resolve(filePath).toAbsolutePath().toString();
    }

    @Override
    public @NotNull ConnectionSource getConnectionSource() throws SQLException {
        return new JdbcConnectionSource(getConnectionUrl());
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
