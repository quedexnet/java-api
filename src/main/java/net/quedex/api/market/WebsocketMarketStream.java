package net.quedex.api.market;

import net.quedex.api.common.Config;
import net.quedex.api.common.SessionStateListener;
import net.quedex.api.common.WebsocketStream;
import net.quedex.api.pgp.BcPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketMarketStream extends WebsocketStream<MarketMessageReceiver> implements MarketStream
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketMarketStream.class);

    public WebsocketMarketStream(final String marketStreamUrl, final BcPublicKey qdxPublicKey)
    {
        super(LOGGER, marketStreamUrl, new MarketMessageReceiver(qdxPublicKey));
    }

    public WebsocketMarketStream(final Config config)
    {
        this(config.getMarketStreamUrl(), config.getQdxPublicKey());
    }

    @Override
    public Registration registerOrderBookListener(final OrderBookListener orderBookListener)
    {
        return messageReceiver.registerOrderBookListener(orderBookListener);
    }

    @Override
    public Registration registerTradeListener(final TradeListener tradeListener)
    {
        return messageReceiver.registerTradeListener(tradeListener);
    }

    @Override
    public Registration registerQuotesListener(final QuotesListener quotesListener)
    {
        return messageReceiver.registerQuotesListener(quotesListener);
    }

    @Override
    public void registerAndSubscribeSessionStateListener(final SessionStateListener sessionStateListener)
    {
        messageReceiver.registerAndSubscribeSessionStateListener(sessionStateListener);
    }
}
