package net.quedex.api.user;

import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.StreamFailureListener;

import java.util.List;

/**
 * Represents the stream of realtime private data streamed from and trading commands which may be sent to Quedex.
 * Allows registering and subscribing for particular data types. The registered listeners will be called (in a single
 * thread) for every event that arrives. Trading commands include placing, canceling and modifying orders - batching of
 * these commands is possible via {@link #batch} methods and should be used whenever possible. The data exchanged on the
 * stream has the form of PGP-encrypted messages - all the encryption/decryption and serialization/deserialization is
 * handled by the implementations and the interaction with the stream is based on Java objects.
 * <p>
 * The stream gives some guarantees about the order of received events useful for state initialisation - see
 * documentation of {@link #subscribeListeners}.
 * <p>
 * To handle all errors properly, always {@link #registerStreamFailureListener} before {@link #start}ing the stream.
 */
public interface UserStream {

    void registerStreamFailureListener(StreamFailureListener streamFailureListener);

    void start() throws CommunicationException;

    void registerOrderListener(OrderListener orderListener);

    void registerOpenPositionListener(OpenPositionListener openPositionListener);

    void registerAccountStateListener(AccountStateListener accountStateListener);

    void registerInternalTransferListener(InternalTransferListener listener);

    void registerTimerListener(TimerListener listener);

    /**
     * Subscribes previously registered listeners. Causes a welcome package to be sent to the listeners. The welcome
     * package includes:
     * <ul>
     *     <li> an {@link OrderPlaced} item for each pending order </li>
     *     <li> an {@link OpenPosition} item for each opened position </li>
     *     <li> an initial {@link AccountState} </li>
     * </ul>
     *
     * The welcome package constitutes an initial state that will be modified by the subsequent events received by the
     * listeners.
     * <p>
     * The first received {@link AccountState} marks the end of the welcome package and may be used to detect the end
     * of initialisation.
     */
    void subscribeListeners();

    /**
     * Sends the given {@link LimitOrderSpec} to the exchange. This method is asynchronous - the fact that it returned
     * does not guarantee that the command has been received nor processed by the exchange.
     */
    void placeOrder(LimitOrderSpec limitOrderSpec);

    /**
     * Sends the given {@link OrderCancelSpec} to the exchange. This method is asynchronous - the fact that it returned
     * does not guarantee that the command has been received nor processed by the exchange.
     */
    void cancelOrder(OrderCancelSpec orderCancelSpec);

    /**
     * Sends command to cancel all pending orders for an user to the exchange. This method is asynchronous - the fact
     * that it returned does not guarantee that the command has ben received nor processed by the exchange.
     */
    void cancelAllOrders();

    /**
     * Sends the given {@link OrderModificationSpec} to the exchange. This method is asynchronous - the fact that it
     * returned does not guarantee that the command has been received nor processed by the exchange.
     */
    void modifyOrder(OrderModificationSpec orderModificationSpec);

    /**
     * Returns an object (not thread-safe) which may be used fluently to send a batch of {@link OrderSpec}s to the
     * exchange. Calling {@link Batch#send()} sends batched {@link OrderSpec}s to the exchange. This method is
     * asynchronous - the fact that it returned does not guarantee that the commands have been received nor processed by
     * the exchange.
     */
    Batch batch();

    /**
     * Sends the given list of {@link OrderSpec}s to the exchange. This method is asynchronous - the fact that it
     * returned does not guarantee that the commands have been received nor processed by the exchange.
     */
    void batch(List<? extends OrderSpec> batch);

    /**
     * <p>
     *     Returns an object (not thread-safe) which may be used fluently to create a time triggered batch of {@link OrderSpec}s.
     *     Calling {@link Batch#send()} sends this batch of {@link OrderSpec}s to the exchange.
     * </p>
     * <p>
     *     When a time triggered batch is received by the exchange engine, a new timer is registered.
     *     Based on the timer configuration, at some point in the future (between <i>executionStartTimestamp</i> and <i>executionExpirationTimestamp</i>),
     *     all the carried order commands are processed, one by one, in the creation order.
     * </p>
     * <p>
     *     Please refer to the API documentation for detailed explanation of creating timers.
     * </p>
     * This method is asynchronous - the fact that it returned does not guarantee that the commands have been received
     * nor processed by the exchange.
     *
     * @param timerId a user defined timer identifier, can be used to cancel or update batch
     * @param executionStartTimestamp the defined batch will not be executed before this timestamp
     * @param executionExpirationTimestamp the defined batch will not ne executed after this timestamp
     */
    Batch timeTriggeredBatch(long timerId, long executionStartTimestamp, long executionExpirationTimestamp);

    /**
     * <p>
     *     Sends a time triggered batch with the given list of {@link OrderSpec}s to the exchange.
     * </p>
     * <p>
     *     When a time triggered batch is received by the exchange engine, a new timer is registered.
     *     Based on the timer configuration, at some point in the future (between <i>executionStartTimestamp</i> and <i>executionExpirationTimestamp</i>),
     *     all the carried order commands are processed, one by one, in the creation order.
     * </p>
     * <p>
     *     Please refer to the API documentation for detailed explanation of creating timers.
     * </p>
     * This method is asynchronous - the fact that it returned does not guarantee that the commands have been received
     * nor processed by the exchange.
     *
     * @param timerId a user defined batch identifier, can be used to cancel or update batch
     * @param executionStartTimestamp the defined batch will not be executed before this timestamp
     * @param executionExpirationTimestamp the defined batch will not be executed after this timestamp
     * @param batch list of {@link OrderSpec}s to be executed
     */
    void timeTriggeredBatch(long timerId,
                            long executionStartTimestamp,
                            long executionExpirationTimestamp,
                            List<? extends OrderSpec> batch);

    /**
     * Returns an object (not thread-safe) which may be used fluently to update an already existing batch of {@link OrderSpec}s on the
     * exchange. At least one of the following must be modified:
     * <ul>
     *     <li>executionStartTimestamp</li>
     *     <li>executionExpirationTimestamp</li>
     *     <li>commands</li>
     * </ul>
     * <p>
     *     Specified commands replace commands registered during the timer creation.
     *     Calling {@link Batch#send()} sends modified batch of {@link OrderSpec}s to the exchange.
     * </p>
     * <p>
     *     Please refer to the API documentation for detailed explanation of updating timers.
     * </p>
     * This method is asynchronous - the fact that it returned does not guarantee that the commands have been received
     * nor processed by the exchange.
     *
     * @param timerId a user defined timer identifier, the same as used when creating the batch
     * @param executionStartTimestamp new value of executionStartTimestamp (optional)
     * @param executionExpirationTimestamp new value of executionExpirationTimestamp (optional)
     */
    Batch updateTimeTriggeredBatch(long timerId, Long executionStartTimestamp, Long executionExpirationTimestamp);

    /**
     *
     * Sends the modified batch to the exchange. At least one of the following must be modified:
     * <ul>
     *     <li>executionStartTimestamp</li>
     *     <li>executionExpirationTimestamp</li>
     *     <li>batch</li>
     * </ul>
     * <p>
     *     Specified commands replace commands registered during the timer creation.
     * </p>
     * <p>
     *     Please refer to the API documentation for detailed explanation of updating timers.
     * </p>
     * This method is asynchronous - the fact that it returned does not guarantee that the commands have been received
     * nor processed by the exchange.
     *
     * @param timerId a user defined timer identifier, the same as used when creating the batch
     * @param executionStartTimestamp new value of executionStartTimestamp (optional)
     * @param executionExpirationTimestamp new value of executionExpirationTimestamp (optional)
     * @param batch new value of batch (optional)
     */
    void updateTimeTriggeredBatch(long timerId,
                                  Long executionStartTimestamp,
                                  Long executionExpirationTimestamp,
                                  List<? extends OrderSpec> batch);

    /**
     * Sends command to cancel an existing time triggered batch.
     * @param timerId a user defined batch identifier, the same as used when creating the batch
     */
    void cancelTimeTriggeredBatch(long timerId);

    void executeInternalTransfer(InternalTransfer internalTransfer);

    void stop() throws CommunicationException;

    interface Batch {

        Batch placeOrder(LimitOrderSpec limitOrderSpec);

        Batch placeOrders(List<LimitOrderSpec> limitOrderSpecs);

        Batch cancelOrder(OrderCancelSpec orderCancelSpec);

        Batch cancelOrders(List<OrderCancelSpec> orderCancelSpecs);

        Batch cancelAllOrders();

        Batch modifyOrder(OrderModificationSpec orderModificationSpec);

        Batch modifyOrders(List<OrderModificationSpec> orderModificationSpec);

        void send();
    }
}
