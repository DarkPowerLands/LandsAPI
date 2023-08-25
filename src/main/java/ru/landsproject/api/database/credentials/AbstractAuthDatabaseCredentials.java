package ru.landsproject.api.database.credentials;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.landsproject.api.database.DatabaseType;

@Getter
public abstract class AbstractAuthDatabaseCredentials extends AbstractDatabaseCredentials implements AuthDatabaseCredentials {

    @CredentialField("username")
    protected String username;

    @CredentialField("password")
    protected String password;

    protected AbstractAuthDatabaseCredentials(@NotNull DatabaseType databaseType) {
        super(databaseType);
    }

}
