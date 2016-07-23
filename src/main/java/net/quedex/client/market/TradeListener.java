package net.quedex.client.market;

@FunctionalInterface
public interface TradeListener {

    void onTrade(Trade trade);
}
