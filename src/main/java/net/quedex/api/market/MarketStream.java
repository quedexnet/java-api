package net.quedex.api.market;

import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.StreamFailureListener;

/**
 * Represents the stream of realtime public trade data streamed from Quedex and allows registering and subscribing for
 * particular data types. The registered listeners will be called (in a single thread) for every event that arrives. The
 * data come in form of PGP-clearsigned JSON messages - all the verification and deserialization is handled by the
 * implementations and the listeners receive Java objects.
 * <p>
 * The stream gives the following guarantees useful for state initialisation:
 * <ul>
 *     <li>
 *         {@link InstrumentsListener} will be always called immediately after connection is established, before any
 *         other listener
 *     </li>
 *     <li>
 *         after the connection is established, every listener is guaranteed to be called at least once
 *     </li>
 * </ul>
 * <p>
 * To handle all errors properly, always {@link #registerStreamFailureListener} before {@link #start}ing the stream.
 */
public interface MarketStream {

    void registerStreamFailureListener(StreamFailureListener streamFailureListener);

    void start() throws CommunicationException;

    void registerInstrumentsListener(InstrumentsListener instrumentsListener);

    Registration registerOrderBookListener(OrderBookListener orderBookListener);

    Registration registerTradeListener(TradeListener tradeListener);

    Registration registerQuotesListener(QuotesListener quotesListener);

    void registerSpotDataListener(SpotDataListener spotDataListener);

    void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener);

    void stop() throws CommunicationException;
}
