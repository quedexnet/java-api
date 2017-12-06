package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Liquidation market order placed automatically by the exchange during liquidation.
 */
public class LiquidationOrderPlaced {

    private final long systemOrderId;
    private final int instrumentId;
    @JsonIgnore
    private final OrderType type;
    private final OrderSide side;
    private final int quantity;
    private final int initialQuantity;

    @JsonCreator
    public LiquidationOrderPlaced(
            @JsonProperty("system_order_id") long systemOrderId,
            @JsonProperty("instrument_id") int instrumentId,
            @JsonProperty("side") OrderSide side,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("initial_quantity") int initialQuantity
    ) {
        checkArgument(systemOrderId > 0, "systemOrderId=%s <= 0", systemOrderId);
        checkArgument(instrumentId > 0, "instrumentId=%s <= 0", instrumentId);
        checkArgument(quantity > 0, "quantity=%s <= 0", quantity);
        checkArgument(initialQuantity > 0, "initialQuantity=%s <= 0", initialQuantity);
        this.systemOrderId = systemOrderId;
        this.instrumentId = instrumentId;
        this.type = OrderType.MARKET;
        this.side = checkNotNull(side, "null side");
        this.quantity = quantity;
        this.initialQuantity = initialQuantity;
    }

    public long getSystemOrderId() {
        return systemOrderId;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public OrderType getType() {
        return type;
    }

    public OrderSide getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiquidationOrderPlaced that = (LiquidationOrderPlaced) o;
        return systemOrderId == that.systemOrderId &&
                instrumentId == that.instrumentId &&
                quantity == that.quantity &&
                initialQuantity == that.initialQuantity &&
                type == that.type &&
                side == that.side;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(systemOrderId, instrumentId, type, side, quantity, initialQuantity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("systemOrderId", systemOrderId)
                .add("instrumentId", instrumentId)
                .add("type", type)
                .add("side", side)
                .add("quantity", quantity)
                .add("initialQuantity", initialQuantity)
                .toString();
    }
}
