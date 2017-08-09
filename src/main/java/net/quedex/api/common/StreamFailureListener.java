package net.quedex.api.common;

@FunctionalInterface
public interface StreamFailureListener {

    /**
     * @param exception indicates that an error happened during operations of a stream; if this exception is an instance
     *                  of {@link net.quedex.api.common.DisconnectedException} this means that the stream got
     *                  disconnected (due to network errors, exchange going down for maintenance, etc.) and the stream
     *                  should be reconnected
     */
    void onStreamFailure(Exception exception);
}
