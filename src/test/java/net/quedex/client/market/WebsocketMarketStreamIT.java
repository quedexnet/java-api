package net.quedex.client.market;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import net.quedex.client.pgp.BcPublicKey;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * An integration test with live Quedex Websocket. To run it:
 * <ol>
 *     <li> Copy {@code marketStreamWS.properties.example} to {@code marketStreamWS.properties} in test resources. </li>
 *     <li> Fill in the details in the properties file. </li>
 *     <li> Enable the test method. </li>
 *     <li> Fill in instrument ids so that they match instrument ids of currently trades instruments. </li>
 *     <li> Run the test. </li>
 * </ol>
 */
public class WebsocketMarketStreamIT {

    private static final List<Integer> INSTRUMENT_IDS = ImmutableList.of(47, 48, 49);

    @Mock private OrderBookListener orderBookListener;
    @Mock private QuotesListener quotesListener;
    @Mock private SessionStateListener sessionStateListener;
    @Mock private TradeListener tradeListener;
    @Mock private StreamFailureListener streamFailureListener;

    private MarketStream marketStream;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Properties props = new Properties();
        props.load(Resources.getResource("marketStreamWS.properties").openStream());
        marketStream = new WebsocketMarketStream(
                props.getProperty("url"),
                new BcPublicKey(props.getProperty("pubKey"))
        );
    }

    @Test(enabled = true)
    public void testIntegrationWithLiveWS() throws Exception {

        marketStream.registerStreamFailureListener(streamFailureListener);
        marketStream.registerAndSubscribeSessionStateListener(sessionStateListener);
        marketStream.registerOrderBookListener(orderBookListener).subscribe(INSTRUMENT_IDS);
        marketStream.registerQuotesListener(quotesListener).subscribe(INSTRUMENT_IDS);
        marketStream.registerTradeListener(tradeListener).subscribe(INSTRUMENT_IDS);

        marketStream.start();

        INSTRUMENT_IDS
                .forEach(id -> verify(orderBookListener, timeout(1000)).onOrderBook(argThat(obHasInstrumentId(id))));
        INSTRUMENT_IDS
                .forEach(id -> verify(quotesListener, timeout(1000)).onQuotes(argThat(quotesHaveInstrumentId(id))));
        verify(sessionStateListener, timeout(1000)).onSessionState(any());

        marketStream.stop();

        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    private static Matcher<OrderBook> obHasInstrumentId(int id) {
        return new BaseMatcher<OrderBook>() {
            @Override
            public boolean matches(Object o) {
                if (o instanceof OrderBook) {
                    return ((OrderBook) o).getInstrumentId() == id;
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("OrderBook with instrumentId").appendValue(id);
            }
        };
    }

    private static Matcher<Quotes> quotesHaveInstrumentId(int id) {
        return new BaseMatcher<Quotes>() {
            @Override
            public boolean matches(Object o) {
                if (o instanceof Quotes) {
                    return ((Quotes) o).getInstrumentId() == id;
                } else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Wuotes with instrumentId").appendValue(id);
            }
        };
    }
}