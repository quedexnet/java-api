package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class OrderCanceled
{
    private final long clientOrderId;

    @JsonCreator
    public OrderCanceled(@JsonProperty("client_order_id") final long clientOrderId)
    {
        this.clientOrderId = clientOrderId;
    }

    public long getClientOrderId()
    {
        return clientOrderId;
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
        final OrderCanceled that = (OrderCanceled) o;
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
