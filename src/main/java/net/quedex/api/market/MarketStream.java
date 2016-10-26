package net.quedex.api.market;

import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.SessionStateListener;

public interface MarketStream
{
    void registerStreamFailureListener(StreamFailureListener streamFailureListener);

    void start() throws CommunicationException;

    Registration registerOrderBookListener(OrderBookListener orderBookListener);

    Registration registerTradeListener(TradeListener tradeListener);

    Registration registerQuotesListener(QuotesListener quotesListener);

    void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener);

    void stop() throws CommunicationException;
}
