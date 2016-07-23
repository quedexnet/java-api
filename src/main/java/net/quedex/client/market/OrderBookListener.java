package net.quedex.client.market;

@FunctionalInterface
public interface OrderBookListener {

    void onOrderBook(OrderBook orderBook);
}
