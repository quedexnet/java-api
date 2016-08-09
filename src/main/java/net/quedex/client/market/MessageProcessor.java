package net.quedex.client.market;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.quedex.client.pgp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Parses JSON and PGP-signed messages and manages subscriptions.
 */
class MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final BcSignatureVerifier bcSignatureVerifier;

    private volatile OrderBookListener orderBookListener;
    private final Set<Integer> orderBookSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile TradeListener tradeListener;
    private final Set<Integer> tradeSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile QuotesListener quotesListener;
    private final Set<Integer> quotesSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile SessionStateListener sessionStateListener;

    private volatile StreamFailureListener streamFailureListener;

    MessageProcessor(BcPublicKey publicKey) {
        this.bcSignatureVerifier = new BcSignatureVerifier(publicKey);
    }

    Registration registerOrderBookListener(OrderBookListener orderBookListener) {
        this.orderBookListener = orderBookListener;
        return new DefaultRegistration(orderBookSubscriptions);
    }

    Registration registerTradeListener(TradeListener tradeListener) {
        this.tradeListener = tradeListener;
        return new DefaultRegistration(tradeSubscriptions);
    }

    Registration registerQuotesListener(QuotesListener quotesListener) {
        this.quotesListener = quotesListener;
        return new DefaultRegistration(quotesSubscriptions);
    }

    void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener) {
        this.sessionStateListener = sessionStateListener;
    }

    void registerStreamFailureListener(StreamFailureListener streamFailureListener) {
        this.streamFailureListener = streamFailureListener;
    }

    void processMessage(String message) {
        try {
            JsonNode metaJson = OBJECT_MAPPER.readTree(message);

            switch (metaJson.get("type").asText()) {
                case "data":
                    processData(metaJson.get("data").asText());
                    break;
                case "error":
                    processError(metaJson.get("error_code").asText());
                    break;
                default:
                    // no-op
                    break;
            }
        } catch (IOException e) {
            onError(new CommunicationException("Error parsing json entity on message=" + message, e));
        } catch (PGPExceptionBase e) {
            onError(new CommunicationException("Error verifying signature on message=" + message, e));
        }
    }

    private void processData(String data)
            throws PGPInvalidSignatureException, PGPSignatureVerificationException, IOException {
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

    private void processError(String error_code) {
        LOGGER.trace("processError({})", error_code);
        if ("maintenance".equals(error_code)) {
            onError(new CommunicationException("Exchange going down for maintenance"));
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

    private void onError(Exception e) {
        LOGGER.warn("onError({})", e);
        StreamFailureListener streamFailureListener = this.streamFailureListener;
        if (streamFailureListener != null) {
            streamFailureListener.onStreamFailure(e);
        }
    }

    private static class DefaultRegistration implements Registration {

        private final Set<Integer> subscriptions;

        private DefaultRegistration(Set<Integer> subscriptions) {
            this.subscriptions = checkNotNull(subscriptions, "null subscriptions");
        }

        @Override
        public DefaultRegistration subscribe(int instrumentId) {
            subscriptions.add(instrumentId);
            return this;
        }

        @Override
        public DefaultRegistration subscribe(Collection<Integer> instrumentIds) {
            instrumentIds.forEach(this::subscribe);
            return this;
        }

        @Override
        public DefaultRegistration unsubscribe(int instrumentId) {
            subscriptions.remove(instrumentId);
            return this;
        }

        @Override
        public DefaultRegistration unsubscribe(Collection<Integer> instrumentIds) {
            instrumentIds.forEach(this::unsubscribe);
            return this;
        }
    }
}
