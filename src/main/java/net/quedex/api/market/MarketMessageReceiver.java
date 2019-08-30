package net.quedex.api.market;

import com.fasterxml.jackson.databind.JsonNode;
import net.quedex.api.common.MessageReceiver;
import net.quedex.api.pgp.BcPublicKey;
import net.quedex.api.pgp.BcSignatureVerifier;
import net.quedex.api.pgp.PGPExceptionBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

class MarketMessageReceiver extends MessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketMessageReceiver.class);

    private final BcSignatureVerifier bcSignatureVerifier;

    private volatile InstrumentsListener instrumentsListener;
    private final Object instrumentsMonitor = new Object();
    private Map<Integer, Instrument> instrumentsCached;

    private volatile OrderBookListener orderBookListener;
    private final Set<Integer> orderBookSubscriptions = new HashSet<>(64, 0.75f);
    private final Map<Integer, OrderBook> orderBookCache = new HashMap<>(64, 0.75f);

    private volatile TradeListener tradeListener;
    private final Set<Integer> tradeSubscriptions = new HashSet<>(64, 0.75f);
    private final Map<Integer, Trade> tradeCache = new HashMap<>(64, 0.75f);

    private volatile QuotesListener quotesListener;
    private final Set<Integer> quotesSubscriptions = new HashSet<>(64, 0.75f);
    private final Map<Integer, Quotes> quotesCache = new HashMap<>(64, 0.75f);

    private volatile SessionStateListener sessionStateListener;
    private final Object sessionStateMonitor = new Object();
    private SessionState sessionStateCached;

    private volatile SpotDataListener spotDataListener;
    private final Object spotDataMonitor = new Object();
    private SpotDataWrapper spotDataWrapperCached;

    MarketMessageReceiver(BcPublicKey qdxPublicKey) {
        super(LOGGER);
        this.bcSignatureVerifier = new BcSignatureVerifier(qdxPublicKey);
    }

    void registerInstrumentsListener(InstrumentsListener instrumentsListener) {
        this.instrumentsListener = instrumentsListener;
        synchronized (instrumentsMonitor) {
            Map<Integer, Instrument> instrumentsCached = this.instrumentsCached;
            if (instrumentsListener != null && instrumentsCached != null) {
                instrumentsListener.onInstruments(instrumentsCached);
            }
        }
    }

    Registration registerOrderBookListener(OrderBookListener orderBookListener) {
        this.orderBookListener = orderBookListener;
        return new CachedRegistration<OrderBook>(orderBookSubscriptions, orderBookCache) {
            @Override
            void onSubscribe(OrderBook element) {
                if (orderBookListener != null) {
                    orderBookListener.onOrderBook(element);
                }
            }
        };
    }

    Registration registerTradeListener(TradeListener tradeListener) {
        this.tradeListener = tradeListener;
        return new CachedRegistration<Trade>(tradeSubscriptions, tradeCache) {
            @Override
            void onSubscribe(Trade element) {
                if (tradeListener != null) {
                    tradeListener.onTrade(element);
                }
            }
        };
    }

    Registration registerQuotesListener(QuotesListener quotesListener) {
        this.quotesListener = quotesListener;
        return new CachedRegistration<Quotes>(quotesSubscriptions, quotesCache) {
            @Override
            void onSubscribe(Quotes element) {
                if (quotesListener != null) {
                    quotesListener.onQuotes(element);
                }
            }
        };
    }

    void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener) {
        this.sessionStateListener = sessionStateListener;
        synchronized (sessionStateMonitor) {
            SessionState sessionStateCached = this.sessionStateCached;
            if (sessionStateListener != null && sessionStateCached != null) {
                sessionStateListener.onSessionState(sessionStateCached);
            }
        }
    }

    void registerSpotDataListener(SpotDataListener spotDataListener) {
        this.spotDataListener = spotDataListener;
        synchronized (spotDataMonitor) {
            SpotDataWrapper spotDataWrapperCached = this.spotDataWrapperCached;
            if (spotDataListener != null && this.spotDataWrapperCached != null) {
                spotDataListener.onSpotData(spotDataWrapperCached);
            }
        }
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
            case "instrument_data":
                onInstrumentData(OBJECT_MAPPER.treeToValue(dataJson.get("data"), InstrumentsMap.class));
                break;
            case "spot_data":
                onSpotDataWrapper(OBJECT_MAPPER.treeToValue(dataJson, SpotDataWrapper.class));
            default:
                // no-op
                break;
        }
    }

    private void onInstrumentData(Map<Integer, Instrument> instruments) {
        synchronized (instrumentsMonitor) {
            instrumentsCached = instruments;
            InstrumentsListener instrumentsListener = this.instrumentsListener;
            if (instrumentsListener != null) {
                instrumentsListener.onInstruments(instruments);
            }
        }
    }

    private void onOrderBook(OrderBook orderBook) {
        synchronized (orderBookCache) {
            orderBookCache.put(orderBook.getInstrumentId(), orderBook);
            OrderBookListener orderBookListener = this.orderBookListener;
            if (orderBookListener != null && orderBookSubscriptions.contains(orderBook.getInstrumentId())) {
                orderBookListener.onOrderBook(orderBook);
            }
        }
    }

    private void onQuotes(Quotes quotes) {
        synchronized (quotesCache) {
            quotesCache.put(quotes.getInstrumentId(), quotes);
            QuotesListener quotesListener = this.quotesListener;
            if (quotesListener != null && quotesSubscriptions.contains(quotes.getInstrumentId())) {
                quotesListener.onQuotes(quotes);
            }
        }
    }

    private void onTrade(Trade trade) {
        synchronized (tradeCache) {
            tradeCache.put(trade.getInstrumentId(), trade);
            TradeListener tradeListener = this.tradeListener;
            if (tradeListener != null && tradeSubscriptions.contains(trade.getInstrumentId())) {
                tradeListener.onTrade(trade);
            }
        }
    }

    private void onSessionState(SessionState sessionState) {
        synchronized (sessionStateMonitor) {
            sessionStateCached = sessionState;
            SessionStateListener sessionStateListener = this.sessionStateListener;
            if (sessionStateListener != null) {
                sessionStateListener.onSessionState(sessionState);
            }
        }
    }

    private void onSpotDataWrapper(SpotDataWrapper spotDataWrapper) {
        synchronized (spotDataMonitor) {
            spotDataWrapperCached = spotDataWrapper;
            SpotDataListener spotDataListener = this.spotDataListener;
            if (spotDataListener != null) {
                spotDataListener.onSpotData(spotDataWrapper);
            }
        }
    }

    private abstract static class CachedRegistration<T> implements Registration {

        final Set<Integer> subscriptions;
        final Map<Integer, T> cache;

        CachedRegistration(Set<Integer> subscriptions, Map<Integer, T> cache) {
            this.subscriptions = checkNotNull(subscriptions, "null subscriptions");
            this.cache = checkNotNull(cache, "null cache");
        }

        abstract void onSubscribe(T element);

        @Override
        public CachedRegistration subscribe(int instrumentId) {
            synchronized (cache) {
                subscriptions.add(instrumentId);
                T element = cache.get(instrumentId);
                if (element != null) {
                    onSubscribe(element);
                }
            }
            return this;
        }

        @Override
        public CachedRegistration subscribe(Collection<Integer> instrumentIds) {
            instrumentIds.forEach(this::subscribe);
            return this;
        }

        @Override
        public CachedRegistration unsubscribe(int instrumentId) {
            subscriptions.remove(instrumentId);
            return this;
        }

        @Override
        public CachedRegistration unsubscribe(Collection<Integer> instrumentIds) {
            instrumentIds.forEach(this::unsubscribe);
            return this;
        }

        @Override
        public Registration unsubscribeAll() {
            subscriptions.clear();
            return this;
        }
    }

    public static class InstrumentsMap extends HashMap<Integer, Instrument> {}
}
