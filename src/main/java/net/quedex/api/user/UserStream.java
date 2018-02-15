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
