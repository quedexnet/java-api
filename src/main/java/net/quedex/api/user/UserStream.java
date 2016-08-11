package net.quedex.api.user;

import net.quedex.api.common.CommunicationException;
import net.quedex.api.market.StreamFailureListener;

import java.util.List;

public interface UserStream {

    void registerStreamFailureListener(StreamFailureListener streamFailureListener);

    void start() throws CommunicationException;

    void registerOrderListener(OrderListener orderListener);

    void registerOpenPositionListener(OpenPositionListener openPositionListener);

    void registerAccountStateListener(AccountStateListener accountStateListener);

    void subscribeListeners();

    void placeOrder(LimitOrderSpec limitOrderSpec);

    void cancelOrder(OrderCancelSpec orderCancelSpec);

    void modifyOrder(OrderModificationSpec orderModificationSpec);

    /**
     * MEMO: the returned object is not thread-safe
     */
    Batch batch();

    void batch(List<OrderSpec> batch);

    void stop() throws CommunicationException;

    interface Batch {

        Batch placeOrder(LimitOrderSpec limitOrderSpec);

        Batch placeOrders(List<LimitOrderSpec> limitOrderSpecs);

        Batch cancelOrder(OrderCancelSpec orderCancelSpec);

        Batch cancelOrders(List<OrderCancelSpec> orderCancelSpecs);

        Batch modifyOrder(OrderModificationSpec orderModificationSpec);

        Batch modifyOrders(List<OrderModificationSpec> orderModificationSpec);

        void send();
    }
}
