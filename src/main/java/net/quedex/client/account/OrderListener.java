package net.quedex.client.account;

public interface OrderListener {

    void onOrderPlaced(OrderPlaced orderPlaced);

    void onOrderPlaceFailed(OrderPlaceFailed orderPlaceFailed);

    void onOrderCanceled(OrderCanceled orderCanceled);

    void onOrderCancelFailed(OrderCancelFailed orderCancelFailed);

    void onOrderModified(OrderModified orderModified);

    void onOrderModificationFailed(OrderModificationFailed orderModificationFailed);

    void onOrderFilled(OrderFilled orderFilled);
}
