package net.quedex.api.user;

import com.fasterxml.jackson.databind.JsonNode;
import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.MessageReceiver;
import net.quedex.api.pgp.BcDecryptor;
import net.quedex.api.pgp.BcPrivateKey;
import net.quedex.api.pgp.BcPublicKey;
import net.quedex.api.pgp.PGPExceptionBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class UserMessageReceiver extends MessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMessageReceiver.class);

    private final BcDecryptor decryptor;
    private final CompletableFuture<Long> lastNonceFuture = new CompletableFuture<>();

    private volatile OrderListener orderListener;
    private volatile OpenPositionListener openPositionListener;
    private volatile AccountStateListener accountStateListener;
    private volatile InternalTransferListener internalTransferListener;
    private volatile TimeTriggeredBatchListener timeTriggeredBatchListener;

    UserMessageReceiver(BcPublicKey qdxPublicKey, BcPrivateKey userPrivateKey) {
        super(LOGGER);
        this.decryptor = new BcDecryptor(qdxPublicKey, userPrivateKey);
    }

    void registerOrderListener(OrderListener orderListener) {
        this.orderListener = orderListener;
    }

    void registerOpenPositionListener(OpenPositionListener openPositionListener) {
        this.openPositionListener = openPositionListener;
    }

    void registerAccountStateListener(AccountStateListener accountStateListener) {
        this.accountStateListener = accountStateListener;
    }

    void registerInternalTransferListener(InternalTransferListener listener) {
        internalTransferListener = listener;
    }

    void registerTimeTriggeredBatchListener(TimeTriggeredBatchListener timeTriggeredBatchListener) {
        this.timeTriggeredBatchListener = timeTriggeredBatchListener;
    }

    long getLastNonce() throws TimeoutException, InterruptedException {
        try {
            return lastNonceFuture.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Cannot happen", e);
        }
    }

    @Override
    protected void processData(String data) throws IOException, PGPExceptionBase {

        String decrypted = decryptor.decrypt(data);

        LOGGER.trace("processData(data={}, decrypted={})", data, decrypted);
        try {
            JsonNode dataJsonArray = OBJECT_MAPPER.readTree(decrypted);

            for (final JsonNode dataJson : dataJsonArray) {
                switch (dataJson.get("type").asText()) {
                    case "account_state":
                        onAccountState(OBJECT_MAPPER.treeToValue(dataJson, AccountState.class));
                        break;
                    case "open_position":
                        onOpenPosition(OBJECT_MAPPER.treeToValue(dataJson, OpenPosition.class));
                        break;
                    case "order_cancelled":
                        onOrderCancelled(OBJECT_MAPPER.treeToValue(dataJson, OrderCancelled.class));
                        break;
                    case "order_forcefully_cancelled":
                        onOrderForcefullyCancelled(OBJECT_MAPPER.treeToValue(dataJson, OrderForcefullyCancelled.class));
                        break;
                    case "order_cancel_failed":
                        onOrderCancelFailed(OBJECT_MAPPER.treeToValue(dataJson, OrderCancelFailed.class));
                        break;
                    case "all_orders_cancelled":
                        onAllOrdersCanceled();
                        break;
                    case "cancel_all_orders_failed":
                        onCancelAllOrdersFailed(OBJECT_MAPPER.treeToValue(dataJson, CancelAllOrdersFailed.class));
                        break;
                    case "order_filled":
                        onOrderFilled(OBJECT_MAPPER.treeToValue(dataJson, OrderFilled.class));
                        break;
                    case "order_placed":
                        onOrderPlaced(OBJECT_MAPPER.treeToValue(dataJson, OrderPlaced.class));
                        break;
                    case "order_modified":
                        onOrderModified(OBJECT_MAPPER.treeToValue(dataJson, OrderModified.class));
                        break;
                    case "order_modification_failed":
                        onOrderModificationFailed(OBJECT_MAPPER.treeToValue(dataJson, OrderModificationFailed.class));
                        break;
                    case "order_place_failed":
                        onOrderPlaceFailed(OBJECT_MAPPER.treeToValue(dataJson, OrderPlaceFailed.class));
                        break;
                    case "subscribed":
                        LOGGER.debug("Subscribed successfully");
                        break;
                    case "last_nonce":
                        onLastNonce(dataJson);
                        break;
                    case "liquidation_order_placed":
                        onLiquidationOrderPlaced(OBJECT_MAPPER.treeToValue(dataJson, LiquidationOrderPlaced.class));
                        break;
                    case "liquidation_order_cancelled":
                        onLiquidationOrderCancelled(OBJECT_MAPPER.treeToValue(dataJson, LiquidationOrderCancelled.class));
                        break;
                    case "liquidation_order_filled":
                        onLiquidationOrderFilled(OBJECT_MAPPER.treeToValue(dataJson, LiquidationOrderFilled.class));
                        break;
                    case "internal_transfer_executed":
                        onInternalTransferExecuted(OBJECT_MAPPER.treeToValue(dataJson, InternalTransferExecuted.class));
                        break;
                    case "internal_transfer_rejected":
                        onInternalTransferRejected(OBJECT_MAPPER.treeToValue(dataJson, InternalTransferRejected.class));
                        break;
                    case "internal_transfer_received":
                        onInternalTransferReceived(OBJECT_MAPPER.treeToValue(dataJson, InternalTransferReceived.class));
                        break;
                    case "timer_added":
                        onTimeTriggeredBatchAdded(OBJECT_MAPPER.treeToValue(dataJson, TimeTriggeredBatchAdded.class));
                        break;
                    case "timer_rejected":
                        onTimeTriggeredBatchRejected(OBJECT_MAPPER.treeToValue(dataJson, TimeTriggeredBatchRejected.class));
                        break;
                    case "timer_expired":
                        onTimeTriggeredBatchExpired(OBJECT_MAPPER.treeToValue(dataJson, TimeTriggeredBatchExpired.class));
                        break;
                    case "timer_triggered":
                        onTimeTriggeredBatchTriggered(OBJECT_MAPPER.treeToValue(dataJson, TimeTriggeredBatchTriggered.class));
                        break;
                    case "timer_updated":
                        onTimeTriggeredBatchUpdated(OBJECT_MAPPER.treeToValue(dataJson, TimeTriggeredBatchUpdated.class));
                        break;
                    case "timer_update_failed":
                        onTimeTriggeredBatchUpdateFailed(OBJECT_MAPPER.treeToValue(dataJson, TimeTriggeredBatchUpdateFailed.class));
                        break;
                    default:
                        // no-op
                        break;
                }
            }
        } catch (IOException e) {
            throw new CommunicationException("Error parsing json on decrypted=" + decrypted, e);
        }
    }

    private void onAccountState(AccountState accountState) {
        AccountStateListener accountStateListener = this.accountStateListener;
        if (accountStateListener != null) {
            accountStateListener.onAccountState(accountState);
        }
    }

    private void onOpenPosition(OpenPosition openPosition) {
        OpenPositionListener openPositionListener = this.openPositionListener;
        if (openPositionListener != null) {
            openPositionListener.onOpenPosition(openPosition);
        }
    }

    private void onOrderCancelled(OrderCancelled orderCancelled) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onOrderCancelled(orderCancelled);
        }
    }

    private void onOrderForcefullyCancelled(OrderForcefullyCancelled orderForcefullyCancelled) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onOrderForcefullyCancelled(orderForcefullyCancelled);
        }
    }

    private void onOrderCancelFailed(OrderCancelFailed orderCancelFailed) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onOrderCancelFailed(orderCancelFailed);
        }
    }

    private void onAllOrdersCanceled() {
        final OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onAllOrdersCancelled();
        }
    }

    private void onCancelAllOrdersFailed(final CancelAllOrdersFailed cancelAllOrdersFailed) {
        final OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onCancelAllOrdersFailed(cancelAllOrdersFailed);
        }
    }

    private void onOrderFilled(OrderFilled orderFilled) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onOrderFilled(orderFilled);
        }
    }

    private void onOrderModificationFailed(OrderModificationFailed orderModificationFailed) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onOrderModificationFailed(orderModificationFailed);
        }
    }

    private void onOrderModified(OrderModified orderModified) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onOrderModified(orderModified);
        }
    }

    private void onOrderPlaced(OrderPlaced orderPlaced) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onOrderPlaced(orderPlaced);
        }
    }

    private void onOrderPlaceFailed(OrderPlaceFailed orderPlaceFailed) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onOrderPlaceFailed(orderPlaceFailed);
        }
    }

    private void onLiquidationOrderPlaced(LiquidationOrderPlaced liquidationOrderPlaced) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onLiquidationOrderPlaced(liquidationOrderPlaced);
        }
    }

    private void onLiquidationOrderCancelled(LiquidationOrderCancelled liquidationOrderCancelled) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onLiquidationOrderCancelled(liquidationOrderCancelled);
        }
    }

    private void onLiquidationOrderFilled(LiquidationOrderFilled liquidationOrderFilled) {
        OrderListener orderListener = this.orderListener;
        if (orderListener != null) {
            orderListener.onLiquidationOrderFilled(liquidationOrderFilled);
        }
    }

    private void onInternalTransferExecuted(InternalTransferExecuted internalTransferExecuted) {
        InternalTransferListener listener = internalTransferListener;
        if (listener != null) {
            listener.onInternalTransferExecuted(internalTransferExecuted);
        }
    }

    private void onInternalTransferRejected(InternalTransferRejected internalTransferRejected) {
        InternalTransferListener listener = internalTransferListener;
        if (listener != null) {
            listener.onInternalTransferRejected(internalTransferRejected);
        }
    }

    private void onInternalTransferReceived(InternalTransferReceived internalTransferReceived) {
        InternalTransferListener listener = internalTransferListener;
        if (listener != null) {
            listener.onInternalTransferReceived(internalTransferReceived);
        }
    }

    private void onTimeTriggeredBatchAdded(TimeTriggeredBatchAdded timeTriggeredBatchAdded) {
        TimeTriggeredBatchListener listener = timeTriggeredBatchListener;
        if (listener != null) {
            listener.onTimeTriggeredBatchAdded(timeTriggeredBatchAdded);
        }
    }

    private void onTimeTriggeredBatchRejected(TimeTriggeredBatchRejected timeTriggeredBatchRejected) {
        TimeTriggeredBatchListener listener = timeTriggeredBatchListener;
        if (listener != null) {
            listener.onTimeTriggeredBatchRejected(timeTriggeredBatchRejected);
        }
    }

    private void onTimeTriggeredBatchExpired(TimeTriggeredBatchExpired timeTriggeredBatchExpired) {
        TimeTriggeredBatchListener listener = timeTriggeredBatchListener;
        if (listener != null) {
            listener.onTimeTriggeredBatchExpired(timeTriggeredBatchExpired);
        }
    }

    private void onTimeTriggeredBatchTriggered(TimeTriggeredBatchTriggered timeTriggeredBatchTriggered) {
        TimeTriggeredBatchListener listener = timeTriggeredBatchListener;
        if (listener != null) {
            listener.onTimeTriggeredBatchTriggered(timeTriggeredBatchTriggered);
        }
    }

    private void onTimeTriggeredBatchUpdated(TimeTriggeredBatchUpdated timeTriggeredBatchTriggered) {
        TimeTriggeredBatchListener listener = timeTriggeredBatchListener;
        if (listener != null) {
            listener.onTimeTriggeredBatchUpdated(timeTriggeredBatchTriggered);
        }
    }

    private void onTimeTriggeredBatchUpdateFailed(TimeTriggeredBatchUpdateFailed timeTriggeredBatchUpdateFailed) {
        TimeTriggeredBatchListener listener = timeTriggeredBatchListener;
        if (listener != null) {
            listener.onTimeTriggeredBatchUpdateFailed(timeTriggeredBatchUpdateFailed);
        }
    }

    private void onLastNonce(JsonNode dataJson) {
        lastNonceFuture.complete(dataJson.get("last_nonce").asLong());
    }
}
