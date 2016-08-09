package net.quedex.client.market;

import com.google.common.collect.ImmutableList;
import net.quedex.client.pgp.BcPublicKey;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class MessageProcessorTest {

    @Mock private OrderBookListener orderBookListener;
    @Mock private QuotesListener quotesListener;
    @Mock private SessionStateListener sessionStateListener;
    @Mock private TradeListener tradeListener;
    @Mock private StreamFailureListener streamFailureListener;

    private MessageProcessor messageProcessor;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        messageProcessor = new MessageProcessor(new BcPublicKey(Fixtures.PUB_KEY));
        messageProcessor.registerStreamFailureListener(streamFailureListener);
    }

    @Test
    public void testOrderBookProcessing() throws Exception {

        // given
        Registration reg = messageProcessor.registerOrderBookListener(orderBookListener);

        // when
        reg.subscribe(1);
        messageProcessor.processMessage(Fixtures.ORDER_BOOK_STR);

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
        Registration reg = messageProcessor.registerOrderBookListener(orderBookListener);

        // when
        reg.subscribe(2).subscribe(1).unsubscribe(1);
        messageProcessor.processMessage(Fixtures.ORDER_BOOK_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(orderBookListener, never()).onOrderBook(any());
    }

    @Test
    public void testQuotesProcessing() throws Exception {

        // given
        Registration reg = messageProcessor.registerQuotesListener(quotesListener);

        // when
        reg.subscribe(1);
        messageProcessor.processMessage(Fixtures.QUOTES_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(quotesListener).onQuotes(new Quotes(1, $("0.00142858"), 1, null, null, $("0.00166944"), 3, 2, 0));
    }

    @Test
    public void testQuotesAreNotProcessedIfNotSubscribedForInstrument() throws Exception {

        // given
        Registration reg = messageProcessor.registerQuotesListener(quotesListener);

        // when
        reg.subscribe(2).subscribe(1).unsubscribe(1);
        messageProcessor.processMessage(Fixtures.QUOTES_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(quotesListener, never()).onQuotes(any());
    }

    @Test
    public void testTradeProcessing() throws Exception {

        // given
        Registration reg = messageProcessor.registerTradeListener(tradeListener);

        // when
        reg.subscribe(1);
        messageProcessor.processMessage(Fixtures.TRADE_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(tradeListener).onTrade(argThat(isFieldByFiledEqual(
                new Trade(1, 70, 1470681720788L, $("0.00166666"), 1, Trade.LiquidityProvider.SELLER)
        )));
    }

    @Test
    public void testTradeIsNoProcessedIfNotSubscribedForInstrument() throws Exception {

        // given
        Registration reg = messageProcessor.registerTradeListener(tradeListener);

        // when
        reg.subscribe(2).subscribe(1).unsubscribe(1);
        messageProcessor.processMessage(Fixtures.TRADE_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(tradeListener, never()).onTrade(any());
    }

    @Test
    public void testSessionStateProcessing() throws Exception {

        // when
        messageProcessor.registerAndSubscribeSessionStateListener(sessionStateListener);
        messageProcessor.processMessage(Fixtures.SESSION_STATE_STR);

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
        verify(sessionStateListener).onSessionState(SessionState.AUCTION);
    }

    @Test
    public void testStreamFailureJsonProcessingError() throws Exception {

        // when
        messageProcessor.processMessage("BOMBA");

        // then
        verify(streamFailureListener).onStreamFailure(isA(CommunicationException.class));
    }

    @Test
    public void testStreamFailureSignatureError() throws Exception {

        // when
        messageProcessor.processMessage(Fixtures.SESSION_STATE_STR_WRONG_SIG);

        // then
        verify(streamFailureListener).onStreamFailure(isA(CommunicationException.class));
    }

    @Test
    public void testMaintenanceErrorProcessing() throws Exception {

        // when
        messageProcessor.processMessage(Fixtures.ERROR_MAINTENANCE_STR);

        // then
        verify(streamFailureListener).onStreamFailure(isA(CommunicationException.class));
    }

    private static PriceQuantity pq(String price, int qty) {
        return new PriceQuantity($(price), qty);
    }

    private static BigDecimal $(String price) {
        return new BigDecimal(price);
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