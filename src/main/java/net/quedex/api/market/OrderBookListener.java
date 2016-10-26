package net.quedex.api.market;

@FunctionalInterface
public interface OrderBookListener
{
    void onOrderBook(OrderBook orderBook);
}
