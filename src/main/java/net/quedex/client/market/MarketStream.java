package net.quedex.client.market;

import net.quedex.client.commons.CommunicationException;
import net.quedex.client.commons.SessionStateListener;

public interface MarketStream {

    void registerStreamFailureListener(StreamFailureListener streamFailureListener);

    void start() throws CommunicationException;

    Registration registerOrderBookListener(OrderBookListener orderBookListener);

    Registration registerTradeListener(TradeListener tradeListener);

    Registration registerQuotesListener(QuotesListener quotesListener);

    void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener);

    void stop() throws CommunicationException;
}
