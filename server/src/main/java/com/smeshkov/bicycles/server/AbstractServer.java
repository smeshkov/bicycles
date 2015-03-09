package com.smeshkov.bicycles.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author smeshkov
 * @since 12/02/15
 */
public abstract class AbstractServer implements Server {

    protected final int port;
    protected final ExecutorService pool;
    protected final AtomicBoolean listening = new AtomicBoolean(false);

    protected AbstractServer(int port) {
        this.port = port;
        this.pool = Executors.newCachedThreadPool();
    }

    @Override
    public boolean isListening() {
        return listening.get();
    }

}
