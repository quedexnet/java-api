package net.quedex.client.market;

import com.fasterxml.jackson.databind.JsonNode;
import net.quedex.client.commons.MessageReceiver;
import net.quedex.client.commons.SessionStateListener;
import net.quedex.client.pgp.BcPublicKey;
import net.quedex.client.pgp.BcSignatureVerifier;
import net.quedex.client.pgp.PGPExceptionBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class MarketMessageReceiver extends MessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);

    private final BcSignatureVerifier bcSignatureVerifier;

    private volatile OrderBookListener orderBookListener;
    private final Set<Integer> orderBookSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile TradeListener tradeListener;
    private final Set<Integer> tradeSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile QuotesListener quotesListener;
    private final Set<Integer> quotesSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile SessionStateListener sessionStateListener;

    MarketMessageReceiver(BcPublicKey qdxPublicKey) {
        super(LOGGER);
        this.bcSignatureVerifier = new BcSignatureVerifier(qdxPublicKey);
    }

    Registration registerOrderBookListener(OrderBookListener orderBookListener) {
        this.orderBookListener = orderBookListener;
        return new RegistrationImpl(orderBookSubscriptions);
    }

    Registration registerTradeListener(TradeListener tradeListener) {
        this.tradeListener = tradeListener;
        return new RegistrationImpl(tradeSubscriptions);
    }

    Registration registerQuotesListener(QuotesListener quotesListener) {
        this.quotesListener = quotesListener;
        return new RegistrationImpl(quotesSubscriptions);
    }

    void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener) {
        this.sessionStateListener = sessionStateListener;
    }

    @Override
    protected void processData(String data) throws IOException, PGPExceptionBase {
        LOGGER.trace("processData({})", data);

        String verified = bcSignatureVerifier.verifySignature(data);
        JsonNode dataJson = OBJECT_MAPPER.readTree(verified);

        switch (dataJson.get("type").asText()) {
            case "order_book":
                onOrderBook(OBJECT_MAPPER.treeToValue(dataJson, OrderBook.class));
                break;
            case "quotes":
                onQuotes(OBJECT_MAPPER.treeToValue(dataJson, Quotes.class));
                break;
            case "trade":
                onTrade(OBJECT_MAPPER.treeToValue(dataJson, Trade.class));
                break;
            case "session_state":
                onSessionState(SessionState.valueOf(dataJson.get("state").textValue().toUpperCase()));
                break;
            default:
                // no-op
                break;
        }
    }

    private void onOrderBook(OrderBook orderBook) {
        OrderBookListener orderBookListener = this.orderBookListener;
        if (orderBookListener != null && orderBookSubscriptions.contains(orderBook.getInstrumentId())) {
            orderBookListener.onOrderBook(orderBook);
        }
    }

    private void onQuotes(Quotes quotes) {
        QuotesListener quotesListener = this.quotesListener;
        if (quotesListener != null && quotesSubscriptions.contains(quotes.getInstrumentId())) {
            quotesListener.onQuotes(quotes);
        }
    }

    private void onTrade(Trade trade) {
        TradeListener tradeListener = this.tradeListener;
        if (tradeListener != null && tradeSubscriptions.contains(trade.getInstrumentId())) {
            tradeListener.onTrade(trade);
        }
    }

    private void onSessionState(SessionState sessionState) {
        SessionStateListener sessionStateListener = this.sessionStateListener;
        if (sessionStateListener != null) {
            sessionStateListener.onSessionState(sessionState);
        }
    }

    private static class RegistrationImpl implements Registration {

        private final Set<Integer> subscriptions;

        private RegistrationImpl(Set<Integer> subscriptions) {
            this.subscriptions = checkNotNull(subscriptions, "null subscriptions");
        }

        @Override
        public RegistrationImpl subscribe(int instrumentId) {
            subscriptions.add(instrumentId);
            return this;
        }

        @Override
        public RegistrationImpl subscribe(Collection<Integer> instrumentIds) {
            instrumentIds.forEach(this::subscribe);
            return this;
        }

        @Override
        public RegistrationImpl unsubscribe(int instrumentId) {
            subscriptions.remove(instrumentId);
            return this;
        }

        @Override
        public RegistrationImpl unsubscribe(Collection<Integer> instrumentIds) {
            instrumentIds.forEach(this::unsubscribe);
            return this;
        }
    }
}
