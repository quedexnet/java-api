package net.quedex.client.market;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Trade {

    public enum LiquidityProvider {
        BUYER, SELLER, AUCTION
    }

    private final int instrumentId;
    private final long tradeId;
    private final long timestamp;
    private final BigDecimal price;
    private final int quantity;

    private final LiquidityProvider liquidityProvider;

    @JsonCreator
    public Trade(
            @JsonProperty("instrument_id") int instrumentId,
            @JsonProperty("trade_id") long tradeId,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("price") BigDecimal price,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("liquidity_provider") String liquidityProvider
    ) {
        checkArgument(quantity > 0, "quantity=%s <= 0", quantity);
        this.instrumentId = instrumentId;
        this.tradeId = tradeId;
        this.timestamp = timestamp;
        this.price = checkNotNull(price, "null price");
        this.quantity = quantity;
        this.liquidityProvider =
                LiquidityProvider.valueOf(checkNotNull(liquidityProvider, "null liquidityProvider").toUpperCase());
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public long getTradeId() {
        return tradeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public LiquidityProvider getLiquidityProvider() {
        return liquidityProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return tradeId == trade.tradeId;
    }

    public boolean equalsFieldByField(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return instrumentId == trade.instrumentId &&
                tradeId == trade.tradeId &&
                timestamp == trade.timestamp &&
                quantity == trade.quantity &&
                Objects.equal(price, trade.price) &&
                liquidityProvider == trade.liquidityProvider;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tradeId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instrumentId", instrumentId)
                .add("tradeId", tradeId)
                .add("price", price)
                .add("quantity", quantity)
                .add("liquidityProvider", liquidityProvider)
                .toString();
    }
}
