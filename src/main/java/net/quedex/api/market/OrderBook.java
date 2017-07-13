package net.quedex.api.market;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class OrderBook {

    private final int instrumentId;
    private final List<PriceQuantity> bids;
    private final List<PriceQuantity> asks;

    @JsonCreator
    public OrderBook(
            @JsonProperty("instrument_id") int instrumentId,
            @JsonProperty("bids") List<PriceQuantity> bids,
            @JsonProperty("asks") List<PriceQuantity> asks
    ) {
        this.instrumentId = instrumentId;
        this.bids = checkNotNull(bids, "null bids");
        this.asks = checkNotNull(asks, "null asks");
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public List<PriceQuantity> getBids() {
        return bids;
    }

    public List<PriceQuantity> getAsks() {
        return asks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBook orderBook = (OrderBook) o;
        return instrumentId == orderBook.instrumentId &&
                Objects.equal(bids, orderBook.bids) &&
                Objects.equal(asks, orderBook.asks);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(instrumentId, bids, asks);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instrumentId", instrumentId)
                .add("bids", bids)
                .add("asks", asks)
                .toString();
    }

}
