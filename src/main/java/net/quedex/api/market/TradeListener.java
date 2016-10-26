package net.quedex.api.market;

@FunctionalInterface
public interface TradeListener
{
    void onTrade(Trade trade);
}
