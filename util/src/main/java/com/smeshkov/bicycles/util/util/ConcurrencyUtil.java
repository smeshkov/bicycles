package com.smeshkov.bicycles.util.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author smeshkov
 * @since 13/02/15
 */
@Slf4j
public final class ConcurrencyUtil {

    public static final long TIMEOUT = 500;

    private ConcurrencyUtil() {
        // statics only
    }

    public static void stopExecutor(ExecutorService pool) {
        stopExecutor(pool, TIMEOUT);
    }

    public static void stopExecutor(ExecutorService pool, long timeout) {
        if (pool != null) {
            pool.shutdown(); // Disable new tasks from being submitted
            try {
                // Wait a while for existing tasks to terminate
                if (!pool.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                    pool.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if (!pool.awaitTermination(timeout, TimeUnit.MILLISECONDS))
                        log.error("Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                // (Re-)Cancel if current thread also interrupted
                pool.shutdownNow();
                // Preserve interrupt status
                ExcHandler.ex(ie);
            }
        }
    }

    public static int numThreads() {
        return (Runtime.getRuntime().availableProcessors() + 1) / 2;
    }

}
