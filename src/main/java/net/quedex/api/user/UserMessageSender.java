package net.quedex.api.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.DisconnectedException;
import net.quedex.api.common.MessageReceiver;
import net.quedex.api.common.StreamFailureListener;
import net.quedex.api.pgp.BcEncryptor;
import net.quedex.api.pgp.BcPrivateKey;
import net.quedex.api.pgp.BcPublicKey;
import net.quedex.api.pgp.PGPExceptionBase;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

class UserMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMessageSender.class);
    private static final ObjectMapper OBJECT_MAPPER = MessageReceiver.OBJECT_MAPPER;
    private static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writer();

    private final WebSocketClient webSocketClient;
    private final BcEncryptor encryptor;
    private final long accountId;
    private final int nonceGroup;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // single threaded for sequencing

    private volatile StreamFailureListener streamFailureListener;
    private volatile long nonce;

    UserMessageSender(
            WebSocketClient webSocketClient,
            long accountId,
            int nonceGroup,
            BcPublicKey publicKey,
            BcPrivateKey privateKey
    ) {
        checkArgument(nonceGroup >= 0, "nonceGroup=%s < 0", nonceGroup);
        checkArgument(accountId > 0, "accountId=%s <= 0", accountId);
        this.webSocketClient = checkNotNull(webSocketClient, "null webSocketClient");
        this.encryptor = new BcEncryptor(publicKey, privateKey);
        this.accountId = accountId;
        this.nonceGroup = nonceGroup;
    }

    void registerStreamFailureListener(StreamFailureListener streamFailureListener) {
        this.streamFailureListener = streamFailureListener;
    }

    void setStartNonce(long startNonce) {
        LOGGER.debug("setStartNonce({})", startNonce);
        nonce = startNonce;
    }

    void sendGetLastNonce() throws CommunicationException {
        try {
            sendMessage(
                OBJECT_MAPPER.createObjectNode()
                    .put("type", "get_last_nonce")
                    .put("nonce_group", nonceGroup)
                    .put("account_id", accountId)
            );
        } catch (PGPExceptionBase | JsonProcessingException e) {
            throw new CommunicationException("Error sending get_last_nonce", e);
        }
    }

    void sendSubscribe() {
        sendMessageQueued(() -> addNonceAccountId(OBJECT_MAPPER.createObjectNode().put("type", "subscribe")));
    }

    void sendOrderSpec(OrderSpec orderSpec) {
        sendMessageQueued(() -> addNonceAccountId(OBJECT_MAPPER.valueToTree(orderSpec)));
    }

    void sendBatch(List<? extends OrderSpec> batch) {
        sendMessageQueued(() -> {
            JsonNode batchJson = OBJECT_MAPPER.valueToTree(batch);
            for (final JsonNode node : batchJson) {
                checkState(node instanceof ObjectNode, "Expected ObjectNode");
                addNonceAccountId((ObjectNode) node);
            }
            return OBJECT_MAPPER.createObjectNode()
                .put("type", "batch")
                .put("account_id", accountId)
                .set("batch", batchJson);
        });
    }

    void stop() {
        executor.shutdown();
    }

    private void sendMessageQueued(Supplier<JsonNode> supplier) {
        executor.execute(() -> {
            try {
                sendMessage(supplier.get());
            } catch (WebsocketNotConnectedException e) {
                onError(new DisconnectedException(e));
            } catch (Exception e) {
                onError(new CommunicationException("Error sending message", e));
            }
        });
    }

    private ObjectNode addNonceAccountId(ObjectNode jsonMessage) {
        return jsonMessage
            .put("account_id", accountId)
            .put("nonce", getNonce())
            .put("nonce_group", nonceGroup);
    }

    private long getNonce() {
        return ++nonce;
    }

    private void sendMessage(JsonNode jsonMessage) throws JsonProcessingException, PGPExceptionBase {
        String messageStr = OBJECT_WRITER.writeValueAsString(jsonMessage);
        webSocketClient.send(encryptor.encrypt(messageStr, true));

        LOGGER.trace("sendMessage({})", messageStr);
    }

    private void onError(Exception e) {
        LOGGER.warn("onError({})", e);
        StreamFailureListener streamFailureListener = this.streamFailureListener;
        if (streamFailureListener != null) {
            streamFailureListener.onStreamFailure(e);
        }
    }
}
