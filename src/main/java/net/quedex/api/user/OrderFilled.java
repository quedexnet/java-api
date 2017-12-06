package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class OrderFilled {

    private final long clientOrderId;
    private final int instrumentId;
    private final BigDecimal orderLimitPrice;
    private final OrderType orderType;
    private final OrderSide side;
    private final int orderInitialQuantity;
    private final int leavesOrderQuantity;
    private final BigDecimal tradePrice;
    private final int filledQuantity;

    @JsonCreator
    public OrderFilled(@JsonProperty("client_order_id") long clientOrderId,
                       @JsonProperty("instrument_id") int instrumentId,
                       @JsonProperty("order_limit_price") BigDecimal orderLimitPrice,
                       @JsonProperty("order_side") OrderSide side,
                       @JsonProperty("order_initial_quantity") int orderInitialQuantity,
                       @JsonProperty("leaves_order_quantity") int leavesOrderQuantity,
                       @JsonProperty("trade_price") BigDecimal tradePrice,
                       @JsonProperty("trade_quantity") int filledQuantity) {
        checkArgument(filledQuantity > 0, "filledQuantity=%s <= 0", filledQuantity);
        checkArgument(tradePrice.compareTo(BigDecimal.ZERO) > 0, "tradePrice=%s <= 0", tradePrice);
        checkArgument(instrumentId > 0, "instrumentId=%s <= 0", instrumentId);
        checkArgument(orderLimitPrice.compareTo(BigDecimal.ZERO) > 0, "orderLimitPrice=%s <= 0", orderLimitPrice);
        checkArgument(orderInitialQuantity > 0, "orderInitialQuantity=%s ");
        this.clientOrderId = clientOrderId;
        this.instrumentId = instrumentId;
        this.orderLimitPrice = orderLimitPrice;
        this.orderType = OrderType.LIMIT;
        this.side = checkNotNull(side, "side");
        this.orderInitialQuantity = orderInitialQuantity;
        this.leavesOrderQuantity = leavesOrderQuantity;
        this.filledQuantity = filledQuantity;
        this.tradePrice = tradePrice;
    }

    public long getClientOrderId() {
        return clientOrderId;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public BigDecimal getOrderLimitPrice() {
        return orderLimitPrice;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public OrderSide getSide() {
        return side;
    }

    public int getOrderInitialQuantity() {
        return orderInitialQuantity;
    }

    public int getLeavesOrderQuantity() {
        return leavesOrderQuantity;
    }

    public BigDecimal getTradePrice() {
        return tradePrice;
    }

    public int getFilledQuantity() {
        return filledQuantity;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrderFilled that = (OrderFilled) o;
        return clientOrderId == that.clientOrderId &&
            instrumentId == that.instrumentId &&
            orderInitialQuantity == that.orderInitialQuantity &&
            leavesOrderQuantity == that.leavesOrderQuantity &&
            filledQuantity == that.filledQuantity &&
            Objects.equal(orderLimitPrice, that.orderLimitPrice) &&
            orderType == that.orderType &&
            side == that.side &&
            Objects.equal(tradePrice, that.tradePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
            clientOrderId,
            instrumentId,
            orderLimitPrice,
            orderType,
            side,
            orderInitialQuantity,
            leavesOrderQuantity,
            tradePrice,
            filledQuantity
        );
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("clientOrderId", clientOrderId)
            .add("instrumentId", instrumentId)
            .add("orderLimitPrice", orderLimitPrice)
            .add("orderType", orderType)
            .add("side", side)
            .add("orderInitialQuantity", orderInitialQuantity)
            .add("leavesOrderQuantity", leavesOrderQuantity)
            .add("tradePrice", tradePrice)
            .add("filledQuantity", filledQuantity)
            .toString();
    }
}
