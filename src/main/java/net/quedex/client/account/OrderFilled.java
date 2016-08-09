package net.quedex.client.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;

public class OrderFilled {

    private final long clientOrderId;
    private final int filledQuantity;

    @JsonCreator
    public OrderFilled(
            @JsonProperty("client_order_id") long clientOrderId,
            @JsonProperty("filled_quantity") int filledQuantity
    ) {
        checkArgument(filledQuantity > 0, "filledQuantity=%s <= 0", filledQuantity);
        this.clientOrderId = clientOrderId;
        this.filledQuantity = filledQuantity;
    }

    public long getClientOrderId() {
        return clientOrderId;
    }

    public int getFilledQuantity() {
        return filledQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderFilled that = (OrderFilled) o;
        return clientOrderId == that.clientOrderId &&
                filledQuantity == that.filledQuantity;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientOrderId, filledQuantity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clientOrderId", clientOrderId)
                .add("filledQuantity", filledQuantity)
                .toString();
    }
}
