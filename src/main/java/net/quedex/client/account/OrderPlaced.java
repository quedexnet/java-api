package net.quedex.client.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class OrderPlaced {

    public enum OrderType {
        LIMIT
    }

    private final long clientOrderId;
    private final int instrumentId;
    private final OrderType type;
    private final BigDecimal price;
    private final OrderSide side;
    private final int quantity;
    private final int initialQuantity;

    @JsonCreator
    public OrderPlaced(
            @JsonProperty("client_order_id") long clientOrderId,
            @JsonProperty("instrument_id") int instrumentId,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("side") OrderSide side,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("initial_quantity") int initialQuantity
    ) {
        checkArgument(clientOrderId > 0, "clientOrderId=%s <= 0", clientOrderId);
        checkArgument(instrumentId > 0, "instrumentId=%s <= 0", instrumentId);
        checkArgument(price.compareTo(BigDecimal.ZERO) > 0, "price=%s <= 0", price);
        checkArgument(quantity > 0, "quantity=%s <= 0", quantity);
        checkArgument(initialQuantity > 0, "initialQuantity=%s <= 0", initialQuantity);
        this.clientOrderId = clientOrderId;
        this.instrumentId = instrumentId;
        this.type = OrderType.LIMIT;
        this.price = price;
        this.side = checkNotNull(side, "null side");
        this.quantity = quantity;
        this.initialQuantity = initialQuantity;
    }


    public long getClientOrderId() {
        return clientOrderId;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public OrderType getType() {
        return type;
    }

    public BigDecimal getPrice() {
        return price;
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
        OrderPlaced that = (OrderPlaced) o;
        return clientOrderId == that.clientOrderId &&
                instrumentId == that.instrumentId &&
                quantity == that.quantity &&
                initialQuantity == that.initialQuantity &&
                type == that.type &&
                Objects.equal(price, that.price) &&
                side == that.side;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientOrderId, instrumentId, type, price, side, quantity, initialQuantity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clientOrderId", clientOrderId)
                .add("instrumentId", instrumentId)
                .add("type", type)
                .add("price", price)
                .add("side", side)
                .add("quantity", quantity)
                .add("initialQuantity", initialQuantity)
                .toString();
    }
}
