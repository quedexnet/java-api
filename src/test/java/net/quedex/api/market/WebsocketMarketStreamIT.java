package net.quedex.api.market;

import net.quedex.api.common.Config;
import net.quedex.api.common.SessionStateListener;
import net.quedex.api.testcommons.Utils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
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
public class WebsocketMarketStreamIT
{
    private static final boolean TEST_ENABLED = false; // enable to run

    @Mock private OrderBookListener orderBookListener;
    @Mock private QuotesListener quotesListener;
    @Mock private SessionStateListener sessionStateListener;
    @Mock private TradeListener tradeListener;
    @Mock private StreamFailureListener streamFailureListener;

    private MarketStream marketStream;
    private MarketData marketData;

    @BeforeMethod
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        final Config config = Config.fromResource(Utils.getKeyPassphraseFromProps());
        marketStream = new WebsocketMarketStream(config);
        marketData = new HttpMarketData(config);
    }

    @Test(enabled = TEST_ENABLED)
    public void testIntegrationWithLiveWS() throws Exception
    {
        final Collection<Integer> instrumentIds = marketData.getInstruments().keySet();
        assertThat(instrumentIds).isNotEmpty();

        System.out.println("instrumentIds = " + instrumentIds); // for debugging

        marketStream.start();

        marketStream.registerStreamFailureListener(streamFailureListener);
        marketStream.registerAndSubscribeSessionStateListener(sessionStateListener);
        marketStream.registerOrderBookListener(orderBookListener).subscribe(instrumentIds);
        marketStream.registerQuotesListener(quotesListener).subscribe(instrumentIds);
        marketStream.registerTradeListener(tradeListener).subscribe(instrumentIds);

        instrumentIds
            .forEach(id -> verify(orderBookListener, timeout(1000)).onOrderBook(argThat(obHasInstrumentId(id))));
        instrumentIds
            .forEach(id -> verify(quotesListener, timeout(1000)).onQuotes(argThat(quotesHaveInstrumentId(id))));
        verify(sessionStateListener, timeout(1000)).onSessionState(any());

        marketStream.stop();

        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    private static Matcher<OrderBook> obHasInstrumentId(final int id)
    {
        return new BaseMatcher<OrderBook>()
        {
            @Override
            public boolean matches(final Object o)
            {
                if (o instanceof OrderBook)
                {
                    return ((OrderBook) o).getInstrumentId() == id;
                }
                else
                {
                    return false;
                }
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("OrderBook with instrumentId").appendValue(id);
            }
        };
    }

    private static Matcher<Quotes> quotesHaveInstrumentId(final int id)
    {
        return new BaseMatcher<Quotes>()
        {
            @Override
            public boolean matches(final Object o)
            {
                if (o instanceof Quotes)
                {
                    return ((Quotes) o).getInstrumentId() == id;
                }
                else
                {
                    return false;
                }
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("Wuotes with instrumentId").appendValue(id);
            }
        };
    }
}