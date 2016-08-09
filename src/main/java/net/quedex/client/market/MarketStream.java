package net.quedex.client.market;

public interface MarketStream {

    Registration registerOrderBookListener(OrderBookListener orderBookListener);

    Registration registerTradeListener(TradeListener tradeListener);

    Registration registerQuotesListener(QuotesListener quotesListener);

    void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener);

    void registerStreamFailureListener(StreamFailureListener streamFailureListener);

    void start() throws CommunicationException;

    void stop() throws CommunicationException;
}
