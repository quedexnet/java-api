package net.quedex.client.market;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebsocketMarketStream implements MarketStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketMarketStream.class);

    private final WebSocketClient webSocketClient;
    private final ExecutorService webSocketClientFactoryExec;

    private volatile OrderBookListener orderBookListener;
    private final Set<Integer> orderBookSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile TradeListener tradeListener;
    private final Set<Integer> tradeSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile StreamFailureListener streamFailureListener;

    public WebsocketMarketStream(String marketStreamUrl) {

        webSocketClient = new WebSocketClient(URI.create(marketStreamUrl), new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                LOGGER.info(
                        "Websocket opened with url={}, httpStatus={}, httpStatusMessage={}",
                        marketStreamUrl, handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage()
                );
            }

            @Override
            public void onMessage(String message) {
                WebsocketMarketStream.this.processMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (remote) {
                    WebsocketMarketStream.this.onError(
                            new CommunicationException("Websocket closed with code=" + code + ", reason=" + reason)
                    );
                } else {
                    LOGGER.info("Websocket closed with code={}, reason={}", code, reason);
                }
            }

            @Override
            public void onError(Exception ex) {
                WebsocketMarketStream.this.onError(new CommunicationException("Websocket error", ex));
            }
        };

        webSocketClientFactoryExec = Executors.newSingleThreadExecutor();
        try {
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(null, null, null);
            DefaultSSLWebSocketClientFactory webSocketClientFactory =
                    new DefaultSSLWebSocketClientFactory(ssl, webSocketClientFactoryExec);
            webSocketClient.setWebSocketFactory(webSocketClientFactory);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Error initialising SSL", e);
        }
    }

    @Override
    public void registerOrderBookListener(OrderBookListener orderBookListener) {
        this.orderBookListener = orderBookListener;
    }

    @Override
    public void subscribeOrderBookListener(int instrumentId) {
        orderBookSubscriptions.add(instrumentId);
    }

    @Override
    public void unsubscribeOrderBookListener(int instrumentId) {
        orderBookSubscriptions.remove(instrumentId);
    }

    @Override
    public void registerTradeListener(TradeListener tradeListener) {

    }

    @Override
    public void subscribeTradeListener(int instrumentId) {

    }

    @Override
    public void unsubscribeTradeListener(int instrumentId) {

    }

    @Override
    public void registerQuotesListener(QuotesListener quotesListener) {

    }

    @Override
    public void subscribeQuotesListener(int instrumentId) {

    }

    @Override
    public void unsubscribeQuotesListener(int instrumentId) {

    }

    @Override
    public void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener) {

    }

    @Override
    public void registerStreamFailureListener(StreamFailureListener streamFailureListener) {
        this.streamFailureListener = streamFailureListener;
    }

    @Override
    public void start() throws CommunicationException {
        LOGGER.trace("Starting");
        try {
            webSocketClient.connectBlocking();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
        LOGGER.info("Started");
    }

    @Override
    public void stop() throws CommunicationException {
        LOGGER.trace("Stopping");
        // TODO: comment
        webSocketClient.close();
        webSocketClient.getConnection().closeConnection(1000, "");
        try {
            webSocketClient.closeBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        webSocketClientFactoryExec.shutdown();
        LOGGER.info("Stopped");
    }

    private void processMessage(String message) {
        System.out.println("message = " + message);
    }

    private void onError(Exception e) {
        StreamFailureListener streamFailureListener = this.streamFailureListener;
        if (streamFailureListener != null) {
            streamFailureListener.onStreamFailure(e);
        }
    }

    // TODO: remove
    public static void main(String... args) throws Exception {

        MarketStream ms = new WebsocketMarketStream("wss://quedex.net:63002/market_stream");

        ms.start();

        new BufferedReader(new InputStreamReader(System.in)).readLine();

        ms.stop();
    }
}
