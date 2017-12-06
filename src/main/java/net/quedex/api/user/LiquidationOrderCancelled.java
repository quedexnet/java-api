package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Notification of a cancelled liquidation order.
 *
 * @see LiquidationOrderPlaced
 */
public class LiquidationOrderCancelled {

    private final long systemOrderId;

    @JsonCreator
    public LiquidationOrderCancelled(@JsonProperty("system_order_id") long systemOrderId) {
        this.systemOrderId = systemOrderId;
    }

    public long getSystemOrderId() {
        return systemOrderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiquidationOrderCancelled that = (LiquidationOrderCancelled) o;
        return systemOrderId == that.systemOrderId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(systemOrderId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("systemOrderId", systemOrderId)
                .toString();
    }
}
