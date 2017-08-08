package net.quedex.api.common;

import net.quedex.api.market.StreamFailureListener;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;

import javax.net.ssl.SSLContext;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;

public class WebsocketStream<T extends MessageReceiver> {

    private final Logger logger;

    protected final WebSocketClient webSocketClient;
    private final ExecutorService webSocketClientFactoryExec;
    protected final T messageReceiver;

    private volatile StreamFailureListener streamFailureListener;

    protected WebsocketStream(
            Logger logger,
            String streamUrl,
            T messageReceiver
    ) {
        // TODO: Java-Websockets holds infinitely growing queues
        webSocketClient = new WebSocketClient(URI.create(streamUrl), new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                logger.info(
                        "Websocket opened with url={}, httpStatus={}, httpStatusMessage={}",
                        streamUrl, handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage()
                );
            }

            @Override
            public void onMessage(String message) {
                WebsocketStream.this.processMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (remote) {
                    WebsocketStream.this.onError(
                            new DisconnectedException("Websocket closed with code=" + code + ", reason=" + reason)
                    );
                } else {
                    logger.info("Websocket closed with code={}, reason={}", code, reason);
                }
            }

            @Override
            public void onError(Exception ex) {
                WebsocketStream.this.onError(new CommunicationException("Websocket error", ex));
            }
        };

        webSocketClientFactoryExec = Executors.newSingleThreadExecutor();
        if (streamUrl.startsWith("wss")) {
            initSsl();
        }

        this.messageReceiver = checkNotNull(messageReceiver, "null messageReceiver");
        this.logger = checkNotNull(logger, "null logger");
    }

    private void initSsl() {
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

    public void registerStreamFailureListener(StreamFailureListener streamFailureListener) {
        this.streamFailureListener = streamFailureListener;
        messageReceiver.registerStreamFailureListener(streamFailureListener);
    }

    public void start() throws CommunicationException {
        logger.trace("Starting");
        try {
            webSocketClient.connectBlocking();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
        logger.info("Started");
    }

    public void stop() throws CommunicationException {
        logger.trace("Stopping");
        // has to be closed this way because of incompatibilities in WS protocol
        webSocketClient.close();
        webSocketClient.getConnection().closeConnection(1000, "");
        try {
            webSocketClient.closeBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        webSocketClientFactoryExec.shutdown();
        logger.info("Stopped");
    }

    private void processMessage(String message) {
        messageReceiver.processMessage(message);
    }

    private void onError(Exception e) {
        StreamFailureListener streamFailureListener = this.streamFailureListener;
        if (streamFailureListener != null) {
            streamFailureListener.onStreamFailure(e);
        }
    }
}
