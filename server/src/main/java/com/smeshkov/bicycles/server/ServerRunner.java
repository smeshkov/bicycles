package com.smeshkov.bicycles.server;

import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * @author smeshkov
 * @since 12/02/15
 */
public class ServerRunner implements Runnable {

    private final Socket socket;
    private final Server server;

    // The buffer into which we'll read data when it's available
    private final ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    public ServerRunner(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (server.isListening()) {

        }
    }
}
