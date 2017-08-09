package net.quedex.api.market;

import net.quedex.api.common.CommunicationException;

public interface MarketStream {

    void registerStreamFailureListener(StreamFailureListener streamFailureListener);

    void start() throws CommunicationException;

    void registerInstrumentsListener(InstrumentsListener instrumentsListener);

    Registration registerOrderBookListener(OrderBookListener orderBookListener);

    Registration registerTradeListener(TradeListener tradeListener);

    Registration registerQuotesListener(QuotesListener quotesListener);

    void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener);

    void stop() throws CommunicationException;
}
