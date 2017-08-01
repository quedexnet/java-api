package net.quedex.api.user;

public interface OrderListener {

    void onOrderPlaced(OrderPlaced orderPlaced);

    void onOrderPlaceFailed(OrderPlaceFailed orderPlaceFailed);

    void onOrderCanceled(OrderCanceled orderCanceled);

    void onOrderForcefullyCancelled(OrderForcefullyCancelled orderForcefullyCancelled);

    void onOrderCancelFailed(OrderCancelFailed orderCancelFailed);

    void onOrderModified(OrderModified orderModified);

    void onOrderModificationFailed(OrderModificationFailed orderModificationFailed);

    void onOrderFilled(OrderFilled orderFilled);
}
