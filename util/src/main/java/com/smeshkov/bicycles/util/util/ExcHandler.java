package com.smeshkov.bicycles.util.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;

/**
 * @author smeshkov
 * @since 10/02/15
 */
@SuppressWarnings("UnusedDeclaration")
public final class ExcHandler {

    private static final Logger log =
            LoggerFactory.getLogger(ExcHandler.class);

    // To send ERRORs via email, requires server support and SMTP configuration (see log4j2-appenders.xml > SMTP appender)
    private static final Logger logMail = LoggerFactory.getLogger("MailError");

    private static final String DEFAULT_ERROR = "Something went wrong -> ";
    private static final String DEFAULT_INTERRUPTION_ERROR = "Was interrupted";

    static {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                ex(e); // Uncaught
                logMail.error(String.format("%s thread: [%s]", DEFAULT_ERROR, t.getName()), e);
            } catch (Throwable x) {
                ex(x); // Mail logger error
            }
        });
    }

    private ExcHandler() {
        // Static usage only
    }

    // ---------------------------------------------- HANDLING --------------------------------------------

    /**
     * Helps not to lose original StackTrace for new exception.
     *
     * @param fromExc     - exception to get StackTrace from.
     * @param newExcClass - new exception class.
     * @param <E>         - new exception type.
     * @return new exception with original StackTrace.
     */
    public static <E extends Throwable> E fromEx(Throwable fromExc, Class<E> newExcClass) {
        try {
            E newExc = newExcClass.newInstance();
            newExc.setStackTrace(fromExc.getStackTrace());
            return newExc;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Helps not to lose original StackTrace.
     *
     * @param fromExc - exception to get StackTrace from.
     * @param toExc   - new exception to set StackTrace to.
     * @param <E>     - new exception type.
     * @return new exception with original StackTrace.
     */
    public static <E extends Throwable> E fromEx(Throwable fromExc, E toExc) {
        toExc.setStackTrace(fromExc.getStackTrace());
        return toExc;
    }

    // -------------------------------------------------- LOG ------------------------------------------------------

    public static void handle(Throwable t, String message) {
        log.error(message, t);
    }

    public static void interruption(InterruptedException e) {
        handle(e, DEFAULT_INTERRUPTION_ERROR);
        Thread.currentThread().interrupt();
    }

    public static void ex(Throwable t) {
        if (t instanceof InterruptedException) {
            interruption((InterruptedException) t);
        } else {
            handle(t, DEFAULT_ERROR);
        }
    }

    // -------------------------------------------------- THROW UNCHECKED ------------------------------------------------------

    public static UnsupportedOperationException newUnsupported(String operationName) {
        return new UnsupportedOperationException(String.format("[%s] operation not supported!", operationName));
    }

    public static UnsupportedOperationException newUnsupported() {
        return newUnsupported("this");
    }

    public static void throwNotSupported(String operationName) {
        throw newUnsupported(operationName);
    }

    public static void throwNotSupported() {
        throwNotSupported("this");
    }

    // -------------------------------------------------- THROW CHECKED ------------------------------------------------------

    public static void throwNotSupportedChecked() throws OperationNotSupportedException {
        throwNotSupportedChecked("this");
    }

    public static void throwNotSupportedChecked(String operationName) throws OperationNotSupportedException {
        throw new OperationNotSupportedException(String.format("[%s] operation not supported!", operationName));
    }
}
