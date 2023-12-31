package ru.landsproject.api.database.credentials.remote;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.database.DatabaseType;
import ru.landsproject.api.database.credentials.AbstractAuthDatabaseCredentials;
import ru.landsproject.api.database.credentials.CredentialField;


import java.sql.SQLException;

@Getter
public abstract class AbstractRemoteDatabaseCredentials extends AbstractAuthDatabaseCredentials implements RemoteDatabaseCredentials {

    @CredentialField("host")
    protected String hostname;

    @CredentialField("port")
    protected int port;

    @CredentialField("database")
    protected String databaseName;

    protected AbstractRemoteDatabaseCredentials(@NotNull DatabaseType databaseType) {
        super(databaseType);
    }

    @Override
    public @NotNull ConnectionSource getConnectionSource() throws SQLException {
        return new JdbcConnectionSource(getConnectionUrl(), getUsername(), getPassword());
    }

}
