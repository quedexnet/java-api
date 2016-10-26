package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class OrderCancelSpec implements OrderSpec
{
    private final long clientOrderId;

    public OrderCancelSpec(final long clientOrderId)
    {
        this.clientOrderId = clientOrderId;
    }

    @JsonProperty("client_order_id")
    @Override
    public long getClientOrderId()
    {
        return clientOrderId;
    }

    @JsonProperty("type")
    private String getType()
    {
        return "cancel_order";
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final OrderCancelSpec that = (OrderCancelSpec) o;
        return clientOrderId == that.clientOrderId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(clientOrderId);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
            .add("clientOrderId", clientOrderId)
            .toString();
    }
}
