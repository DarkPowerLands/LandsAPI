package ru.landsproject.api.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.landsproject.api.configuration.Configuration;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Logger;

public final class DatabaseManager {

    private final Logger logger;
    private final Configuration config;
    private final Database database;

    private final ConnectionSource connectionSource;
    private final ExecutorService asyncExecutorService;

    public DatabaseManager(@NotNull Plugin plugin, @NotNull Configuration config, @NotNull Database database) throws SQLException {
        this.logger = plugin.getLogger();
        this.config = config;
        this.database = database;

        this.connectionSource = database.establishConnection();
        this.asyncExecutorService = Executors.newCachedThreadPool();
    }

    public Dao<?,?> createDao(Class<?> clazz) throws SQLException {
        return DaoManager.createDao(connectionSource, clazz);
    }
    public void createTable(Class<?> clazz) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, clazz);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void shutdown() {
        if (asyncExecutorService != null)
            asyncExecutorService.shutdown();

        if (connectionSource != null)
            connectionSource.closeQuietly();
    }

    public @NotNull DatabaseType getDatabaseType() {
        return database.getDatabaseType();
    }


    private <T, ID> @NotNull CompletableFuture<Integer> transferDataFrom(
            @NotNull DatabaseManager sourceStorage,
            @NotNull Function<DatabaseManager, Dao<T, ID>> daoExtractor
    ) {
        return supplyAsync(() -> {
            Dao<T, ID> sourceDao = daoExtractor.apply(sourceStorage);
            Dao<T, ID> destinationDao = daoExtractor.apply(this);

            List<T> entries = sourceDao.queryForAll();
            for (T entry : entries) {
                destinationDao.createIfNotExists(entry);
            }

            return entries.size();
        });
    }

    // --- async proxied methods
    private @NotNull CompletableFuture<Void> runAsync(@NotNull ThrowableRunnable task) {
        return CompletableFuture.runAsync(() -> {
            try {
                task.run();
            } catch (SQLException ex) {
                handleThrowable(ex);
            }
        }, asyncExecutorService);
    }

    private <T> @NotNull CompletableFuture<T> supplyAsync(@NotNull ThrowableSupplier<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.supply();
            } catch (SQLException ex) {
                handleThrowable(ex);
                return null;
            }
        }, asyncExecutorService);
    }

    private void handleThrowable(@NotNull Throwable throwable) {
        logger.severe("An error has occurred when this plugin tried to handle an SQL statement!");
        Throwable cause = throwable;
        while (cause != null) {
            logger.severe(cause.toString());
            cause = cause.getCause();
        }
    }

    public boolean isUuidIdentificationEnabled() {
        return config.getBoolean("identify-by-uuid", false);
    }

    @FunctionalInterface
    private interface ThrowableRunnable {
        void run() throws SQLException;
    }

    @FunctionalInterface
    private interface ThrowableSupplier<T> {
        @Nullable T supply() throws SQLException;
    }

}
