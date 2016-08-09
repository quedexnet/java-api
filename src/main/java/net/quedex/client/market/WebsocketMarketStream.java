package net.quedex.client.market;

import net.quedex.client.pgp.BcPublicKey;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO: Java-Websockets holds infinitely growing queues
 */
public class WebsocketMarketStream implements MarketStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketMarketStream.class);

    private final WebSocketClient webSocketClient;
    private final ExecutorService webSocketClientFactoryExec;
    private final MessageProcessor messageProcessor;

    private volatile StreamFailureListener streamFailureListener;

    public WebsocketMarketStream(String marketStreamUrl, BcPublicKey publicKey) {

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

        messageProcessor = new MessageProcessor(publicKey);
    }

    @Override
    public Registration registerOrderBookListener(OrderBookListener orderBookListener) {
        return messageProcessor.registerOrderBookListener(orderBookListener);
    }

    @Override
    public Registration registerTradeListener(TradeListener tradeListener) {
        return messageProcessor.registerTradeListener(tradeListener);
    }

    @Override
    public Registration registerQuotesListener(QuotesListener quotesListener) {
        return messageProcessor.registerQuotesListener(quotesListener);
    }

    @Override
    public void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener) {
        messageProcessor.registerAndSubscribeSessionStateListener(sessionStateListener);
    }

    @Override
    public void registerStreamFailureListener(StreamFailureListener streamFailureListener) {
        this.streamFailureListener = streamFailureListener;
        messageProcessor.registerStreamFailureListener(streamFailureListener);
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
        // has to be closed this way because of incompatibilities in WS protocol
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
        messageProcessor.processMessage(message);
    }

    private void onError(Exception e) {
        StreamFailureListener streamFailureListener = this.streamFailureListener;
        if (streamFailureListener != null) {
            streamFailureListener.onStreamFailure(e);
        }
    }
}
