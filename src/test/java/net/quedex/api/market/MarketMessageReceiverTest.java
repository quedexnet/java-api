package net.quedex.api.market;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import net.quedex.api.common.CommunicationException;
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
    public void testQuotesProcessing() throws Exception {

        // given
        Registration reg = messageReceiver.registerQuotesListener(quotesListener);

        // when
        reg.subscribe(1);
        messageReceiver.processMessage(Fixtures.QUOTES_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(quotesListener).onQuotes(new Quotes(1, $("0.00142858"), 1, null, null, $("0.00166944"), 3, 2, 0));
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
    public void testSessionStateProcessing() throws Exception {

        // when
        messageReceiver.registerAndSubscribeSessionStateListener(sessionStateListener);
        messageReceiver.processMessage(Fixtures.SESSION_STATE_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(sessionStateListener).onSessionState(SessionState.AUCTION);
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
        assertThat(captor.getValue().get(190)).isEqualToComparingFieldByField(new Instrument(
            "F.USD.AUG17W2",
            190,
            Instrument.Type.FUTURES,
            null,
            new BigDecimal("0.00000001"),
            1501200000000L,
            1502409600000L,
            "USD",
            1,
            new BigDecimal("0.00005000"),
            new BigDecimal("0.00025000"),
            new BigDecimal("0.05000000"),
            new BigDecimal("0.04000000"),
            null
        ));
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