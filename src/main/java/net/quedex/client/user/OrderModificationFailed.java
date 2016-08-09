package net.quedex.client.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class OrderModificationFailed {

    enum Cause {
        INVALID_ORDER_ID,
        INVALID_INSTRUMENT_ID,
        NONPOSITIVE_QUANTITY,
        NONPOSITIVE_PRICE,
        SESSION_NOT_ACTIVE,
        INVALID_TICK_SIZE,
        INSUFFICIENT_FUNDS,
        MARGIN_CALL,
        NOT_FOUND;

        @JsonCreator
        private static Cause deserialize(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    private final long clientOrderId;
    private final Cause cause;

    public OrderModificationFailed(
            @JsonProperty("client_order_id") long clientOrderId,
            @JsonProperty("cause") Cause cause
    ) {
        this.clientOrderId = clientOrderId;
        this.cause = checkNotNull(cause, "null cause");
    }

    public long getClientOrderId() {
        return clientOrderId;
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderModificationFailed that = (OrderModificationFailed) o;
        return clientOrderId == that.clientOrderId &&
                cause == that.cause;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientOrderId, cause);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clientOrderId", clientOrderId)
                .add("cause", cause)
                .toString();
    }
}
