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

public class WebsocketStream<T extends MessageReceiver>
{
    private final Logger logger;

    protected final WebSocketClient webSocketClient;
    private final ExecutorService webSocketClientFactoryExec;
    protected final T messageReceiver;

    private volatile StreamFailureListener streamFailureListener;

    protected WebsocketStream(
        final Logger logger,
        final String streamUrl,
        final T messageReceiver)
    {
        // TODO: Java-Websockets holds infinitely growing queues
        webSocketClient = new WebSocketClient(URI.create(streamUrl), new Draft_17())
        {
            @Override
            public void onOpen(final ServerHandshake handshakedata)
            {
                logger.info(
                    "Websocket opened with url={}, httpStatus={}, httpStatusMessage={}",
                    streamUrl, handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage()
                );
            }

            @Override
            public void onMessage(final String message)
            {
                WebsocketStream.this.processMessage(message);
            }

            @Override
            public void onClose(final int code, final String reason, final boolean remote)
            {
                if (remote)
                {
                    WebsocketStream.this.onError(
                        new CommunicationException("Websocket closed with code=" + code + ", reason=" + reason)
                    );
                }
                else
                {
                    logger.info("Websocket closed with code={}, reason={}", code, reason);
                }
            }

            @Override
            public void onError(final Exception ex)
            {
                WebsocketStream.this.onError(new CommunicationException("Websocket error", ex));
            }
        };

        webSocketClientFactoryExec = Executors.newSingleThreadExecutor();

        try
        {
            final SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(null, null, null);
            final DefaultSSLWebSocketClientFactory webSocketClientFactory =
                new DefaultSSLWebSocketClientFactory(ssl, webSocketClientFactoryExec);
            webSocketClient.setWebSocketFactory(webSocketClientFactory);
        }
        catch (NoSuchAlgorithmException | KeyManagementException e)
        {
            throw new IllegalStateException("Error initialising SSL", e);
        }

        this.messageReceiver = checkNotNull(messageReceiver, "null messageReceiver");
        this.logger = checkNotNull(logger, "null logger");
    }

    public void registerStreamFailureListener(final StreamFailureListener streamFailureListener)
    {
        this.streamFailureListener = streamFailureListener;
        messageReceiver.registerStreamFailureListener(streamFailureListener);
    }

    public void start() throws CommunicationException
    {
        logger.trace("Starting");

        try
        {
            webSocketClient.connectBlocking();
        }
        catch (final InterruptedException e)
        {
            Thread.interrupted();
        }

        logger.info("Started");
    }

    public void stop() throws CommunicationException
    {
        logger.trace("Stopping");
        // has to be closed this way because of incompatibilities in WS protocol
        webSocketClient.close();
        webSocketClient.getConnection().closeConnection(1000, "");

        try
        {
            webSocketClient.closeBlocking();
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        webSocketClientFactoryExec.shutdown();
        logger.info("Stopped");
    }

    private void processMessage(final String message)
    {
        messageReceiver.processMessage(message);
    }

    private void onError(final Exception e)
    {
        final StreamFailureListener streamFailureListener = this.streamFailureListener;
        if (streamFailureListener != null)
        {
            streamFailureListener.onStreamFailure(e);
        }
    }
}
