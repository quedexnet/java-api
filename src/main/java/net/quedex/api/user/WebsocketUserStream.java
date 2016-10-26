package net.quedex.api.user;

import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.Config;
import net.quedex.api.common.WebsocketStream;
import net.quedex.api.market.StreamFailureListener;
import net.quedex.api.pgp.BcPrivateKey;
import net.quedex.api.pgp.BcPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkState;

public class WebsocketUserStream extends WebsocketStream<UserMessageReceiver> implements UserStream
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketUserStream.class);

    private final UserMessageSender sender;

    public WebsocketUserStream(
        final String streamUrl,
        final long accountId,
        final int nonceGroup,
        final BcPublicKey qdxPublicKey,
        final BcPrivateKey userPrivateKey)
    {
        super(LOGGER, streamUrl, new UserMessageReceiver(qdxPublicKey, userPrivateKey));
        this.sender = new UserMessageSender(webSocketClient, accountId, nonceGroup, qdxPublicKey, userPrivateKey);
    }

    public WebsocketUserStream(final Config config)
    {
        this(
            config.getUserStreamUrl(),
            config.getAccountId(),
            config.getNonceGroup(),
            config.getQdxPublicKey(),
            config.getUserPrivateKey()
        );
    }

    @Override
    public void registerStreamFailureListener(final StreamFailureListener streamFailureListener)
    {
        super.registerStreamFailureListener(streamFailureListener);
        sender.registerStreamFailureListener(streamFailureListener);
    }

    @Override
    public void start() throws CommunicationException
    {
        super.start();

        sender.sendGetLastNonce();
        try
        {
            sender.setStartNonce(messageReceiver.getLastNonce());
        }
        catch (final TimeoutException e)
        {
            throw new CommunicationException("Timeout waiting for last nonce", e);
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void registerOrderListener(final OrderListener orderListener)
    {
        messageReceiver.registerOrderListener(orderListener);
    }

    @Override
    public void registerOpenPositionListener(final OpenPositionListener openPositionListener)
    {
        messageReceiver.registerOpenPositionListener(openPositionListener);
    }

    @Override
    public void registerAccountStateListener(final AccountStateListener accountStateListener)
    {
        messageReceiver.registerAccountStateListener(accountStateListener);
    }

    @Override
    public void subscribeListeners()
    {
        sender.sendSubscribe();
    }

    @Override
    public void placeOrder(final LimitOrderSpec limitOrderSpec)
    {
        sender.sendOrderSpec(limitOrderSpec);
    }

    @Override
    public void cancelOrder(final OrderCancelSpec orderCancelSpec)
    {
        sender.sendOrderSpec(orderCancelSpec);
    }

    @Override
    public void modifyOrder(final OrderModificationSpec orderModificationSpec)
    {
        sender.sendOrderSpec(orderModificationSpec);
    }

    @Override
    public Batch batch()
    {
        return new BatchImpl();
    }

    @Override
    public void batch(final List<OrderSpec> batch)
    {
        sender.sendBatch(batch);
    }

    @Override
    public void stop() throws CommunicationException
    {
        super.stop();
        sender.stop();
    }

    private class BatchImpl implements Batch
    {
        private final List<OrderSpec> batch = new ArrayList<>();
        private boolean sent;

        @Override
        public Batch placeOrder(final LimitOrderSpec limitOrderSpec)
        {
            checkState(!sent, "Batch already sent");
            batch.add(limitOrderSpec);
            return this;
        }

        @Override
        public Batch placeOrders(final List<LimitOrderSpec> limitOrderSpecs)
        {
            checkState(!sent, "Batch already sent");
            batch.addAll(limitOrderSpecs);
            return this;
        }

        @Override
        public Batch cancelOrder(final OrderCancelSpec orderCancelSpec)
        {
            checkState(!sent, "Batch already sent");
            batch.add(orderCancelSpec);
            return this;
        }

        @Override
        public Batch cancelOrders(final List<OrderCancelSpec> orderCancelSpecs)
        {
            checkState(!sent, "Batch already sent");
            batch.addAll(orderCancelSpecs);
            return this;
        }

        @Override
        public Batch modifyOrder(final OrderModificationSpec orderModificationSpec)
        {
            checkState(!sent, "Batch already sent");
            batch.add(orderModificationSpec);
            return this;
        }

        @Override
        public Batch modifyOrders(final List<OrderModificationSpec> orderModificationSpec)
        {
            checkState(!sent, "Batch already sent");
            batch.addAll(orderModificationSpec);
            return this;
        }

        @Override
        public void send()
        {
            checkState(!sent, "Batch already sent");
            sent = true;
            WebsocketUserStream.this.batch(batch);
        }
    }
}
