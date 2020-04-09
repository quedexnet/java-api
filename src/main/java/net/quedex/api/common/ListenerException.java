package net.quedex.api.common;

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

