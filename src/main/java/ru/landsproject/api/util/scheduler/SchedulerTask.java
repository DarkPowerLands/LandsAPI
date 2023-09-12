package ru.landsproject.api.util.scheduler;

import lombok.Getter;
import ru.landsproject.api.util.interfaces.Initable;

import java.util.concurrent.*;

public final class SchedulerTask implements Initable {
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private final ScheduledThreadPoolExecutor asyncTaskExecutor
            = new ScheduledThreadPoolExecutor(MAX_THREADS);

    public void scheduleTask(Runnable task, long delay, TimeUnit timeUnit) {
        asyncTaskExecutor.schedule(task, delay, timeUnit);
    }

    @Getter
    private volatile boolean destructed;

    @Override
    public void init() {
        destructed = false;
    }


    @Override
    public void destruct() {
        asyncTaskExecutor.shutdown();
        destructed = true;
    }
}