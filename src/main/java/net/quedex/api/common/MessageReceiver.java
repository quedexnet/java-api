package net.quedex.api.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.quedex.api.market.StreamFailureListener;
import net.quedex.api.pgp.PGPExceptionBase;
import org.slf4j.Logger;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class MessageReceiver {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final String KEEPALIVE_STR = "keepalive";

    private final Logger logger;

    private volatile StreamFailureListener streamFailureListener;

    protected MessageReceiver(Logger logger) {
        this.logger = checkNotNull(logger, "null logger");
    }

    protected abstract void processData(String data) throws IOException, PGPExceptionBase;

    public final void processMessage(String message) {

        if (KEEPALIVE_STR.equals(message)) {
            logger.trace(KEEPALIVE_STR);
            return;
        }

        try {
            JsonNode metaJson = OBJECT_MAPPER.readTree(message);

            switch (metaJson.get("type").asText()) {
                case "data":
                    processData(metaJson.get("data").asText());
                    break;
                case "error":
                    processError(metaJson.get("error_code").asText());
                    break;
                default:
                    // no-op
                    break;
            }
        } catch (IOException e) {
            onError(new CommunicationException("Error parsing json entity on message=" + message, e));
        } catch (PGPExceptionBase e) {
            onError(new CommunicationException("PGP error on message=" + message, e));
        } catch (RuntimeException e) {
            onError(new CommunicationException("Error processing message=" + message, e));
        }
    }

    private void processError(String errorCode) {
        logger.trace("processError({})", errorCode);
        if ("maintenance".equals(errorCode)) {
            onError(new MaintenanceException());
        } else {
            onError(new CommunicationException("Received server processing error: " + errorCode));
        }
    }

    private void onError(Exception e) {
        logger.warn("onError({})", e);
        StreamFailureListener streamFailureListener = this.streamFailureListener;
        if (streamFailureListener != null) {
            streamFailureListener.onStreamFailure(e);
        }
    }

    public final void registerStreamFailureListener(StreamFailureListener streamFailureListener) {
        this.streamFailureListener = streamFailureListener;
    }
}
