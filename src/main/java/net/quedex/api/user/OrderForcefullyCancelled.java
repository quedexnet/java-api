package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Wiktor Gromniak &lt;wgromniak@quedex.net&gt;.
 */
public class OrderForcefullyCancelled {

    public enum Cause {
        LIQUIDATION,
        SETTLEMENT;

        @JsonCreator
        private static Cause deserialize(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    private final long clientOrderId;
    private final Cause cause;

    @JsonCreator
    public OrderForcefullyCancelled(@JsonProperty("client_order_id") long clientOrderId,
                                    @JsonProperty("cause") Cause cause) {
        this.clientOrderId = clientOrderId;
        this.cause = checkNotNull(cause, "cause");
    }

    public long getClientOrderId() {
        return clientOrderId;
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrderForcefullyCancelled that = (OrderForcefullyCancelled) o;
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
