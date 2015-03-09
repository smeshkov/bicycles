package com.smeshkov.bicycles.server.exception;

/**
 * Exception occurred during server start command.
 * <br/>
 * @author s.meshkov <a href="mailto:s.meshkov@oorraa.net"/>
 * @since 13/02/15
 */
public class ServerStartException extends RuntimeException {
    public ServerStartException() {
    }

    public ServerStartException(String message) {
        super(message);
    }

    public ServerStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerStartException(Throwable cause) {
        super(cause);
    }

    public ServerStartException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
