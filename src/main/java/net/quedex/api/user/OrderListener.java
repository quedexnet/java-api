package net.quedex.api.user;

public interface OrderListener {

    void onOrderPlaced(OrderPlaced orderPlaced);

    void onOrderPlaceFailed(OrderPlaceFailed orderPlaceFailed);

    void onOrderCancelled(OrderCancelled orderCancelled);

    void onOrderForcefullyCancelled(OrderForcefullyCancelled orderForcefullyCancelled);

    void onOrderCancelFailed(OrderCancelFailed orderCancelFailed);

    void onOrderModified(OrderModified orderModified);

    void onOrderModificationFailed(OrderModificationFailed orderModificationFailed);

    void onOrderFilled(OrderFilled orderFilled);

    void onLiquidationOrderPlaced(LiquidationOrderPlaced liquidationOrderPlaced);

    void onLiquidationOrderCancelled(LiquidationOrderCancelled liquidationOrderCancelled);
}
