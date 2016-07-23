package net.quedex.client.market;

public interface MarketStream {

    void registerOrderBookListener(OrderBookListener orderBookListener);

    void subscribeOrderBookListener(int instrumentId);

    void unsubscribeOrderBookListener(int instrumentId);

    void registerTradeListener(TradeListener tradeListener);

    void subscribeTradeListener(int instrumentId);

    void unsubscribeTradeListener(int instrumentId);

    void registerQuotesListener(QuotesListener quotesListener);

    void subscribeQuotesListener(int instrumentId);

    void unsubscribeQuotesListener(int instrumentId);

    void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener);

    void registerStreamFailureListener(StreamFailureListener streamFailureListener);

    /**
     * Starts this market stream TODO: ...
     * This method is idempotent.
     *
     * @throws CommunicationException
     */
    void start() throws CommunicationException;

    void stop() throws CommunicationException;
}
