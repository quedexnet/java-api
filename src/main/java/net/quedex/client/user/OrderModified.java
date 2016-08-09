package net.quedex.client.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class OrderModified {

    private final long clientOrderId;

    @JsonCreator
    public OrderModified(@JsonProperty("client_order_id") long clientOrderId) {
        this.clientOrderId = clientOrderId;
    }

    public long getClientOrderId() {
        return clientOrderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderModified that = (OrderModified) o;
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
