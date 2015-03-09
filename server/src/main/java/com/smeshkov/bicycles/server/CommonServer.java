package com.smeshkov.bicycles.server;

import com.smeshkov.bicycles.server.exception.ServerStartException;
import com.smeshkov.bicycles.util.util.ConcurrencyUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * @author smeshkov
 * @since 12/02/15
 */
@Slf4j
public class CommonServer extends AbstractServer {

    protected final Selector acceptSelector;
    private int keysAdded;

    protected CommonServer(int port) {
        super(port);
        try {
            // Selector for incoming time requests
            acceptSelector = SelectorProvider.provider().openSelector();
            keysAdded = 0;

            // Create a new server socket and set to non blocking mode
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);

            // Bind the server socket to the local host and port
            InetAddress lh = InetAddress.getLocalHost();
            InetSocketAddress isa = new InetSocketAddress(lh, port);
            ssc.socket().bind(isa);

            // Register accepts on the server socket with the selector. This
            // step tells the selector that the socket wants to be put on the
            // ready list when accept operations occur, so allowing multiplexed
            // non-blocking I/O to take place.
            ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            throw new IllegalStateException(String.format("Could not listen on port %d", port));
        }
    }

    @Override
    public void start() {
        try {
            // Here's where everything happens. The select method will
            // return when any operations registered above have occurred, the
            // thread has been interrupted, etc.
            while ((keysAdded = acceptSelector.select()) > 0 && listening.get()) {
                // Someone is ready for I/O, get the ready keys
                Set readyKeys = acceptSelector.selectedKeys();
                Iterator i = readyKeys.iterator();

                // Walk through the ready keys collection and process date requests.
                while (i.hasNext()) {
                    SelectionKey sk = (SelectionKey) i.next();
                    i.remove();
                    // The key indexes into the selector so you
                    // can retrieve the socket that's ready for I/O
                    ServerSocketChannel nextReady = (ServerSocketChannel) sk.channel();
                    // Accept request
                    pool.execute(new ServerRunner(this, nextReady.accept().socket()));
                }
            }
        } catch (IOException e) {
            throw new ServerStartException(String.format("Could not accept connections on port %d", port), e);
        }
    }

    @Override
    public void stop() {
        for (; ; ) {
            if (listening.compareAndSet(true, false)) {
                ConcurrencyUtil.stopExecutor(pool);
                break;
            }
        }
    }

}
