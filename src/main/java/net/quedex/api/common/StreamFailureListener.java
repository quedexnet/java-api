package net.quedex.api.common;

@FunctionalInterface
public interface StreamFailureListener {

    /**
     * @param exception indicates that an error happened during operations of a stream;
     *                  If this exception is an instance of {@link net.quedex.api.common.DisconnectedException}
     *                  this means that the stream got disconnected (due to network errors, exchange going down
     *                  for maintenance, etc.) and the stream should be reconnected.
     *                  If this exception is an instance of {@link net.quedex.api.common.ListenerException} this means
     *                  that an error from one of listeners provided by user was thrown.
     */
    void onStreamFailure(Exception exception);
}
