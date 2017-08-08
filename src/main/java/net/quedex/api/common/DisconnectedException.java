package net.quedex.api.common;

/**
 * Indicates lost connection to the exchange in which case a reconnect should be attempted.
 */
public class DisconnectedException extends CommunicationException {

    public DisconnectedException() {
    }

    public DisconnectedException(final String message) {
        super(message);
    }

    public DisconnectedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DisconnectedException(final Throwable cause) {
        super(cause);
    }
}
