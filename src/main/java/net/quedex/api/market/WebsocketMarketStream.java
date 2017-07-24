package net.quedex.api.market;

import net.quedex.api.common.Config;
import net.quedex.api.common.WebsocketStream;
import net.quedex.api.pgp.BcPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketMarketStream extends WebsocketStream<MarketMessageReceiver> implements MarketStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketMarketStream.class);

    public WebsocketMarketStream(String marketStreamUrl, BcPublicKey qdxPublicKey) {
        super(LOGGER, marketStreamUrl, new MarketMessageReceiver(qdxPublicKey));
    }

    public WebsocketMarketStream(Config config) {
        this(config.getMarketStreamUrl(), config.getQdxPublicKey());
    }

    @Override
    public Registration registerOrderBookListener(OrderBookListener orderBookListener) {
        return messageReceiver.registerOrderBookListener(orderBookListener);
    }

    @Override
    public Registration registerTradeListener(TradeListener tradeListener) {
        return messageReceiver.registerTradeListener(tradeListener);
    }

    @Override
    public Registration registerQuotesListener(QuotesListener quotesListener) {
        return messageReceiver.registerQuotesListener(quotesListener);
    }

    @Override
    public void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener) {
        messageReceiver.registerAndSubscribeSessionStateListener(sessionStateListener);
    }
}
