package net.quedex.client.market;

import com.google.common.io.Resources;
import net.quedex.client.pgp.BcPublicKey;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * An integration test with live Quedex Websocket. To run it:
 * <ol>
 *     <li> Copy {@code market.properties.example} to {@code market.properties} in test resources. </li>
 *     <li> Fill in the details in the properties file. </li>
 *     <li> Enable the test method. </li>
 *     <li> Run the test. </li>
 * </ol>
 */
public class WebsocketMarketStreamIT {

    @Mock private OrderBookListener orderBookListener;
    @Mock private QuotesListener quotesListener;
    @Mock private SessionStateListener sessionStateListener;
    @Mock private TradeListener tradeListener;
    @Mock private StreamFailureListener streamFailureListener;

    private MarketStream marketStream;
    private MarketData marketData;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Properties props = new Properties();
        props.load(Resources.getResource("market.properties").openStream());
        BcPublicKey pubKey = new BcPublicKey(props.getProperty("pubKey"));
        marketStream = new WebsocketMarketStream(props.getProperty("wsUrl"), pubKey);
        marketData = new HttpMarketData(props.getProperty("instrumentsUrl"), pubKey);
    }

    @Test(enabled = false)
    public void testIntegrationWithLiveWS() throws Exception {

        Collection<Integer> instrumentIds = marketData.getInstruments().keySet();

        System.out.println("instrumentIds = " + instrumentIds); // for debugging

        marketStream.registerStreamFailureListener(streamFailureListener);
        marketStream.registerAndSubscribeSessionStateListener(sessionStateListener);
        marketStream.registerOrderBookListener(orderBookListener).subscribe(instrumentIds);
        marketStream.registerQuotesListener(quotesListener).subscribe(instrumentIds);
        marketStream.registerTradeListener(tradeListener).subscribe(instrumentIds);

        marketStream.start();

        instrumentIds
                .forEach(id -> verify(orderBookListener, timeout(1000)).onOrderBook(argThat(obHasInstrumentId(id))));
        instrumentIds
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