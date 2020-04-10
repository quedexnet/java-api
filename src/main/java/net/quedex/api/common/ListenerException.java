package net.quedex.api.common;

/**
 * Indicates problems with listener implementation provided by user. Listener failed to handle incoming data and threw
 * an exception. No listener should throw any exceptions.
 */
public class ListenerException extends Exception {

    public ListenerException() {
    }

    public ListenerException(String message) {
        super(message);
    }

    public ListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ListenerException(Throwable cause) {
        super(cause);
    }
}

