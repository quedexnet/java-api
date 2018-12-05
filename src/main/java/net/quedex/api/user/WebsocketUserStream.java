package net.quedex.api.user;

import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.Config;
import net.quedex.api.common.StreamFailureListener;
import net.quedex.api.common.WebsocketStream;
import net.quedex.api.pgp.BcEncryptor;
import net.quedex.api.pgp.BcPrivateKey;
import net.quedex.api.pgp.BcPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkState;

public class WebsocketUserStream extends WebsocketStream<UserMessageReceiver> implements UserStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketUserStream.class);

    private final UserMessageSender sender;

    public WebsocketUserStream(
            String streamUrl,
            long accountId,
            int nonceGroup,
            BcPublicKey qdxPublicKey,
            BcPrivateKey userPrivateKey
    ) {
        super(LOGGER, streamUrl, new UserMessageReceiver(qdxPublicKey, userPrivateKey));
        this.sender = new UserMessageSender(
            webSocketClient,
            accountId,
            nonceGroup,
            new BcEncryptor(qdxPublicKey, userPrivateKey)
        );
    }

    public WebsocketUserStream(Config config) {
        this(
                config.getUserStreamUrl(),
                config.getAccountId(),
                config.getNonceGroup(),
                config.getQdxPublicKey(),
                config.getUserPrivateKey()
        );
    }

    @Override
    public void registerStreamFailureListener(StreamFailureListener streamFailureListener) {
        super.registerStreamFailureListener(streamFailureListener);
        sender.registerStreamFailureListener(streamFailureListener);
    }

    @Override
    public void start() throws CommunicationException {
        super.start();

        sender.sendGetLastNonce();
        try {
            sender.setStartNonce(messageReceiver.getLastNonce());
        } catch (TimeoutException e) {
            throw new CommunicationException("Timeout waiting for last nonce", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void registerOrderListener(OrderListener orderListener) {
        messageReceiver.registerOrderListener(orderListener);
    }

    @Override
    public void registerOpenPositionListener(OpenPositionListener openPositionListener) {
        messageReceiver.registerOpenPositionListener(openPositionListener);
    }

    @Override
    public void registerAccountStateListener(AccountStateListener accountStateListener) {
        messageReceiver.registerAccountStateListener(accountStateListener);
    }

    @Override
    public void registerInternalTransferListener(InternalTransferListener listener) {
        messageReceiver.registerInternalTransferListener(listener);
    }

    @Override
    public void registerTimeTriggeredBatchListener(TimeTriggeredBatchListener listener) {
        messageReceiver.registerTimeTriggeredBatchListener(listener);
    }

    @Override
    public void subscribeListeners() {
        sender.sendSubscribe();
    }

    @Override
    public void placeOrder(LimitOrderSpec limitOrderSpec) {
        sender.sendOrderSpec(limitOrderSpec);
    }

    @Override
    public void cancelOrder(OrderCancelSpec orderCancelSpec) {
        sender.sendOrderSpec(orderCancelSpec);
    }

    @Override
    public void cancelAllOrders() {
        sender.sendOrderSpec(CancelAllOrdersSpec.INSTANCE);
    }

    @Override
    public void modifyOrder(OrderModificationSpec orderModificationSpec) {
        sender.sendOrderSpec(orderModificationSpec);
    }

    @Override
    public Batch batch() {
        return new BatchImpl();
    }

    @Override
    public void batch(List<? extends OrderSpec> batch) {
        sender.sendBatch(batch);
    }

    @Override
    public Batch timeTriggeredBatch(final long batchId,
                                    final long executionStartTimestamp,
                                    final long executionExpirationTimestamp) {
        return new CreateTimeTriggeredBatchImpl(batchId, executionStartTimestamp, executionExpirationTimestamp);
    }

    @Override
    public void timeTriggeredBatch(final long batchId,
                                   final long executionStartTimestamp,
                                   final long executionExpirationTimestamp,
                                   final List<? extends OrderSpec> batch) {
        sender.sendTimeTriggeredBatch(batchId, executionStartTimestamp, executionExpirationTimestamp, batch);
    }

    @Override
    public Batch updateTimeTriggeredBatch(final long batchId,
                                          final Long executionStartTimestamp,
                                          final Long executionExpirationTimestamp) {
        return new UpdateTimeTriggeredBatchImpl(batchId, executionStartTimestamp, executionExpirationTimestamp);
    }

    @Override
    public void updateTimeTriggeredBatch(final long batchId,
                                         final Long executionStartTimestamp,
                                         final Long executionExpirationTimestamp,
                                         final List<? extends OrderSpec> batch) {
        sender.sendTimeTriggeredBatchUpdate(batchId, executionStartTimestamp, executionExpirationTimestamp, batch);
    }

    @Override
    public void cancelTimeTriggeredBatch(final long batchId) {
        sender.sendTimeTriggeredBatchCancellation(batchId);
    }

    @Override
    public void executeInternalTransfer(final InternalTransfer internalTransfer) {
        sender.sendInternalTransfer(internalTransfer);
    }

    @Override
    public void stop() throws CommunicationException {
        super.stop();
        sender.stop();
    }

    private abstract class AbstractBatch implements Batch {
        private final List<OrderSpec> batch = new ArrayList<>();
        private boolean sent;

        @Override
        public Batch placeOrder(LimitOrderSpec limitOrderSpec) {
            checkState(!sent, "Batch already sent");
            batch.add(limitOrderSpec);
            return this;
        }

        @Override
        public Batch placeOrders(List<LimitOrderSpec> limitOrderSpecs) {
            checkState(!sent, "Batch already sent");
            batch.addAll(limitOrderSpecs);
            return this;
        }

        @Override
        public Batch cancelOrder(OrderCancelSpec orderCancelSpec) {
            checkState(!sent, "Batch already sent");
            batch.add(orderCancelSpec);
            return this;
        }

        @Override
        public Batch cancelOrders(List<OrderCancelSpec> orderCancelSpecs) {
            checkState(!sent, "Batch already sent");
            batch.addAll(orderCancelSpecs);
            return this;
        }

        @Override
        public Batch cancelAllOrders() {
            checkState(!sent, "Batch already sent");
            checkState(batch.isEmpty(), "Cancel all makes sense only as first command in batch");
            batch.add(CancelAllOrdersSpec.INSTANCE);
            return this;
        }

        @Override
        public Batch modifyOrder(OrderModificationSpec orderModificationSpec) {
            checkState(!sent, "Batch already sent");
            batch.add(orderModificationSpec);
            return this;
        }

        @Override
        public Batch modifyOrders(List<OrderModificationSpec> orderModificationSpec) {
            checkState(!sent, "Batch already sent");
            batch.addAll(orderModificationSpec);
            return this;
        }

        @Override
        public void send() {
            checkState(!sent, "Batch already sent");
            sent = true;
            this.sendBatch(batch);
        }

        protected abstract void sendBatch(List<OrderSpec> batch);
    }

    private final class BatchImpl extends AbstractBatch {
        protected void sendBatch(List<OrderSpec> batch) {
            WebsocketUserStream.this.batch(batch);
        }
    }

    private abstract class TimeTiggeredBatch extends AbstractBatch {

        private final long batchId;
        private final Long executionStartTimestamp;
        private final Long executionExpirationTimestamp;

        public TimeTiggeredBatch(final long batchId,
                                 final Long executionStartTimestamp,
                                 final Long executionExpirationTimestamp) {
            this.batchId = batchId;
            this.executionStartTimestamp = executionStartTimestamp;
            this.executionExpirationTimestamp = executionExpirationTimestamp;
        }

        public long getBatchId() {
            return this.batchId;
        }

        public Long getExecutionStartTimestamp() {
            return this.executionStartTimestamp;
        }

        public Long getExecutionExpirationTimestamp() {
            return this.executionExpirationTimestamp;
        }
    }

    private final class CreateTimeTriggeredBatchImpl extends TimeTiggeredBatch {

        public CreateTimeTriggeredBatchImpl(final long batchId,
                                            final long executionStartTimestamp,
                                            final long executionExpirationTimestamp) {
            super(batchId, executionStartTimestamp, executionExpirationTimestamp);
        }

        protected void sendBatch(List<OrderSpec> batch) {
            WebsocketUserStream.this.timeTriggeredBatch(
                getBatchId(),
                getExecutionStartTimestamp(),
                getExecutionExpirationTimestamp(),
                batch
            );
        }
    }

    private final class UpdateTimeTriggeredBatchImpl extends TimeTiggeredBatch {

        public UpdateTimeTriggeredBatchImpl(final long batchId,
                                            final Long executionStartTimestamp,
                                            final Long executionExpirationTimestamp) {
            super(batchId, executionStartTimestamp, executionExpirationTimestamp);
        }

        protected void sendBatch(List<OrderSpec> batch) {
            WebsocketUserStream.this.updateTimeTriggeredBatch(
                getBatchId(),
                getExecutionStartTimestamp(),
                getExecutionExpirationTimestamp(),
                batch
            );
        }
    }

}
