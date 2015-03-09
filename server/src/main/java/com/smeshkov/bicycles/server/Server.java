package com.smeshkov.bicycles.server;

/**
 * @author smeshkov
 * @since 12/02/15
 */
public interface Server {

    void start();

    void stop();

    boolean isListening();

}
