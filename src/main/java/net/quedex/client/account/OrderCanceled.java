package net.quedex.client.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class OrderCanceled {

    private final long clientOrderId;

    @JsonCreator
    public OrderCanceled(@JsonProperty("client_order_id") long clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public long getClientOrderId() {
        return clientOrderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCanceled that = (OrderCanceled) o;
        return clientOrderId == that.clientOrderId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientOrderId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clientOrderId", clientOrderId)
                .toString();
    }
}
