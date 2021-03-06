package net.quedex.api.market;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.ListenerException;
import net.quedex.api.common.StreamFailureListener;
import net.quedex.api.pgp.BcPublicKey;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static net.quedex.api.testcommons.Utils.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MarketMessageReceiverTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock private OrderBookListener orderBookListener;
    @Mock private QuotesListener quotesListener;
    @Mock private SessionStateListener sessionStateListener;
    @Mock private TradeListener tradeListener;
    @Mock private StreamFailureListener streamFailureListener;
    @Mock private InstrumentsListener instrumentsListener;
    @Mock private SpotDataListener spotDataListener;

    private MarketMessageReceiver messageReceiver;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        messageReceiver = new MarketMessageReceiver(BcPublicKey.fromArmored(Fixtures.PUB_KEY));
        messageReceiver.registerStreamFailureListener(streamFailureListener);
    }

    @Test
    public void testOrderBookProcessing() throws Exception {

        // given
        Registration reg = messageReceiver.registerOrderBookListener(orderBookListener);

        // when
        messageReceiver.processMessage(Fixtures.ORDER_BOOK_STR);
        reg.subscribe(1);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(orderBookListener).onOrderBook(new OrderBook(
                1,
                ImmutableList.of(pq("0.00142858", 1)),
                ImmutableList.of(pq("0.00166666", 1), pq("0.00166944", 3))
        ));
    }

    @Test
    public void testOrderBookIsNoProcessedIfNotSubscribedForInstrument() throws Exception {

        // given
        Registration reg = messageReceiver.registerOrderBookListener(orderBookListener);

        // when
        reg.subscribe(2).subscribe(1).unsubscribe(1);
        messageReceiver.processMessage(Fixtures.ORDER_BOOK_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(orderBookListener, never()).onOrderBook(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderBookListenerThrowsOnSubscribe() {

        // given
        messageReceiver.processMessage(Fixtures.ORDER_BOOK_STR);
        Registration reg = messageReceiver.registerOrderBookListener(orderBookListener);

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderBookListener).onOrderBook(any());
        reg.subscribe(1);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderBookListenerThrows() {

        // given
        Registration reg = messageReceiver.registerOrderBookListener(orderBookListener);

        // when
        reg.subscribe(1);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderBookListener).onOrderBook(any());
        messageReceiver.processMessage(Fixtures.ORDER_BOOK_STR);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testQuotesProcessing() throws Exception {

        // given
        Registration reg = messageReceiver.registerQuotesListener(quotesListener);

        // when
        reg.subscribe(1);
        messageReceiver.processMessage(Fixtures.QUOTES_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(quotesListener).onQuotes(new Quotes(1, $("0.00142858"), 1, null, null, $("0.00166944"), 3, 2, 0, null, null, null));
    }

    @Test
    public void testQuotesAreNotProcessedIfNotSubscribedForInstrument() throws Exception {

        // given
        Registration reg = messageReceiver.registerQuotesListener(quotesListener);

        // when
        reg.subscribe(2).subscribe(1).unsubscribe(1);
        messageReceiver.processMessage(Fixtures.QUOTES_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(quotesListener, never()).onQuotes(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenQuotesListenerThrowsOnSubscribe() {

        // given
        messageReceiver.processMessage(Fixtures.QUOTES_STR);
        Registration reg = messageReceiver.registerQuotesListener(quotesListener);

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(quotesListener).onQuotes(any());
        reg.subscribe(1);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testErrorListenerIsCalledWhenQuotesListenerThrows() {

        // given
        Registration reg = messageReceiver.registerQuotesListener(quotesListener);

        // when
        reg.subscribe(1);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(quotesListener).onQuotes(any());
        messageReceiver.processMessage(Fixtures.QUOTES_STR);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testTradeProcessing() throws Exception {

        // given
        Registration reg = messageReceiver.registerTradeListener(tradeListener);

        // when
        messageReceiver.processMessage(Fixtures.TRADE_STR);
        reg.subscribe(1);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(tradeListener).onTrade(argThat(isFieldByFiledEqual(
                new Trade(1, 70, 1470681720788L, $("0.00166666"), 1, Trade.LiquidityProvider.SELLER)
        )));
    }

    @Test
    public void testTradeIsNoProcessedIfNotSubscribedForInstrument() throws Exception {

        // given
        Registration reg = messageReceiver.registerTradeListener(tradeListener);

        // when
        reg.subscribe(2).subscribe(1).unsubscribe(1);
        messageReceiver.processMessage(Fixtures.TRADE_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(tradeListener, never()).onTrade(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenTradeListenerThrowsOnSubscribe() {

        // given
        messageReceiver.processMessage(Fixtures.TRADE_STR);
        Registration reg = messageReceiver.registerTradeListener(tradeListener);

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(tradeListener).onTrade(any());
        reg.subscribe(1);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testErrorListenerIsCalledWhenTradeListenerThrows() {

        // given
        Registration reg = messageReceiver.registerTradeListener(tradeListener);

        // when
        reg.subscribe(1);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(tradeListener).onTrade(any());
        messageReceiver.processMessage(Fixtures.TRADE_STR);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testSessionStateProcessing() throws Exception {

        // when
        messageReceiver.registerAndSubscribeSessionStateListener(sessionStateListener);
        messageReceiver.processMessage(Fixtures.SESSION_STATE_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(sessionStateListener).onSessionState(SessionState.AUCTION);
    }

    @Test
    public void testErrorListenerIsCalledWhenStateListenerThrowsOnRegistration() {

        // given
        messageReceiver.processMessage(Fixtures.SESSION_STATE_STR);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(sessionStateListener).onSessionState(any());

        // when
        messageReceiver.registerAndSubscribeSessionStateListener(sessionStateListener);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testErrorListenerIsCalledWhenStateListenerThrows() {

        // given
        messageReceiver.registerAndSubscribeSessionStateListener(sessionStateListener);

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(sessionStateListener).onSessionState(any());
        messageReceiver.processMessage(Fixtures.SESSION_STATE_STR);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testInstrumentDataProcessing() throws Exception {

        // given
        messageReceiver.registerInstrumentsListener(instrumentsListener);

        // when
        messageReceiver.processMessage(Fixtures.INSTRUMENT_DATA_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());

        final ArgumentCaptor<MarketMessageReceiver.InstrumentsMap> captor =
            ArgumentCaptor.forClass(MarketMessageReceiver.InstrumentsMap.class);
        verify(instrumentsListener).onInstruments(captor.capture());
        assertThat(captor.getValue().get(795)).isEqualToComparingFieldByField(new Instrument(
            "F.BTCUSD.24NOV17",
            795,
            Instrument.Type.INVERSE_FUTURES,
            null,
            new BigDecimal("0.01000000"),
            1509667200000L,
            1511481600000L,
            "USD",
            1,
            new BigDecimal("0.00000000"),
            new BigDecimal("0.00000000"),
            new BigDecimal("0.04000000"),
            new BigDecimal("0.03000000"),
            null
        ));
    }

    @Test
    public void testErrorListenerIsCalledWhenInstrumentDataListenerThrowsOnRegistration() {

        // given
        messageReceiver.processMessage(Fixtures.INSTRUMENT_DATA_STR);

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(instrumentsListener).onInstruments(any());
        messageReceiver.registerInstrumentsListener(instrumentsListener);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testErrorListenerIsCalledWhenInstrumentDataListenerThrows() {

        // given
        messageReceiver.registerInstrumentsListener(instrumentsListener);

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(instrumentsListener).onInstruments(any());

        messageReceiver.processMessage(Fixtures.INSTRUMENT_DATA_STR);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testSpotDataProcessing() throws Exception {

        // given
        messageReceiver.registerSpotDataListener(spotDataListener);

        // when
        messageReceiver.processMessage(Fixtures.SPOT_DATA_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());

        verify(spotDataListener).onSpotData(new SpotDataWrapper(
            ImmutableMap.of(
                "USD",
                new SpotDataWrapper.SpotData(
                    new BigDecimal("0.00010469"),
                    new BigDecimal("-0.00000004"),
                    new BigDecimal("0.00010446"),
                    new BigDecimal("0.00000001"),
                    ImmutableList.of("Kraken","itBit","Gemini"),
                    ImmutableMap.of(
                        "Kraken", new BigDecimal("0.00010467"),
                        "itBit", new BigDecimal("0.00010469"),
                        "Gemini", new BigDecimal("0.00010470")
                    )
                )
            ),
            1567163785913L
        ));
    }

    @Test
    public void testErrorListenerIsCalledWhenSpotDataListenerThrowsOnRegistration() {

        // given
        messageReceiver.processMessage(Fixtures.SPOT_DATA_STR);

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(spotDataListener).onSpotData(any());
        messageReceiver.registerSpotDataListener(spotDataListener);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testErrorListenerIsCalledWhenSpotDataListenerThrows() {

        // given
        messageReceiver.registerSpotDataListener(spotDataListener);

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(spotDataListener).onSpotData(any());
        messageReceiver.processMessage(Fixtures.SPOT_DATA_STR);

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testStreamFailureJsonProcessingError() throws Exception {

        // when
        messageReceiver.processMessage("BOMBA");

        // then
        verify(streamFailureListener).onStreamFailure(isA(CommunicationException.class));
    }

    @Test
    public void testStreamFailureSignatureError() throws Exception {

        // when
        messageReceiver.processMessage(Fixtures.SESSION_STATE_STR_WRONG_SIG);

        // then
        verify(streamFailureListener).onStreamFailure(isA(CommunicationException.class));
    }

    @Test
    public void testMaintenanceErrorProcessing() throws Exception {

        // when
        messageReceiver.processMessage(Fixtures.ERROR_MAINTENANCE_STR);

        // then
        verify(streamFailureListener).onStreamFailure(isA(CommunicationException.class));
    }

    @Test
    public void testKeepaliveProcessing() throws Exception {
        // given
        JsonNode jsonWrapper = MAPPER.getNodeFactory().objectNode()
            .put("type", "keepalive")
            .put("timestamp", 1506958410894L);

        // when
        messageReceiver.processMessage(MAPPER.writeValueAsString(jsonWrapper));

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    private static PriceQuantity pq(String price, int qty) {
        return new PriceQuantity($(price), qty);
    }

    private static Matcher<Trade> isFieldByFiledEqual(Trade trade) {
        return new BaseMatcher<Trade>() {
            @Override
            public boolean matches(Object o) {
                return trade.equalsFieldByField(o);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(trade);
            }
        };
    }

}