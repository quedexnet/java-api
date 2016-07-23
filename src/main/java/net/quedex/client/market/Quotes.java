package net.quedex.client.market;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quotes {

    private final BigDecimal last;
    private final int lastSize;
    private final BigDecimal bid;
    private final Integer bidSize;
    private final BigDecimal ask;
    private final Integer askSize;
    private final int volume;
    private final int openInterest;

    @JsonCreator
    public Quotes(
            @JsonProperty("last") BigDecimal last,
            @JsonProperty("last_size") int lastSize,
            @JsonProperty("bid") BigDecimal bid,
            @JsonProperty("bid_size") Integer bidSize,
            @JsonProperty("ask") BigDecimal ask,
            @JsonProperty("ask_size") Integer askSize,
            @JsonProperty("volume") int volume,
            @JsonProperty("open_interest") int openInterest
    ) {
        checkArgument(last.compareTo(BigDecimal.ZERO) > 0, "last=%s <= 0", last);
        checkArgument(lastSize > 0, "lastSize=%s <= 0", lastSize);
        checkArgument(volume >= 0, "volume=%s < 0", volume);
        checkArgument(bid == null || bid.compareTo(BigDecimal.ZERO) > 0, "bid=%s <= 0", bid);
        checkArgument(bidSize == null || bidSize > 0, "bidSize=%s <= 0", bidSize);
        checkArgument(ask == null || ask.compareTo(BigDecimal.ZERO) > 0, "ask=%s <= 0", ask);
        checkArgument(askSize == null || askSize > 0, "askSize=%s <= 0", askSize);
        checkArgument(openInterest >= 0, "openInterest=%s < 0", openInterest);
        this.last = last;
        this.lastSize = lastSize;
        this.bid = bid;
        this.bidSize = bidSize;
        this.ask = ask;
        this.askSize = askSize;
        this.volume = volume;
        this.openInterest = openInterest;
    }

    public PriceQuantity getLast() {
        return new PriceQuantity(last, lastSize);
    }

    public Optional<PriceQuantity> getBid() {
        return bidSize == null ? Optional.empty() : Optional.of(new PriceQuantity(bid, bidSize));
    }

    public Optional<PriceQuantity> getAsk() {
        return askSize == null ? Optional.empty() : Optional.of(new PriceQuantity(ask, askSize));
    }

    public int getVolume() {
        return volume;
    }

    public int getOpenInterest() {
        return openInterest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quotes quotes = (Quotes) o;
        return lastSize == quotes.lastSize &&
                volume == quotes.volume &&
                openInterest == quotes.openInterest &&
                Objects.equal(last, quotes.last) &&
                Objects.equal(bid, quotes.bid) &&
                Objects.equal(bidSize, quotes.bidSize) &&
                Objects.equal(ask, quotes.ask) &&
                Objects.equal(askSize, quotes.askSize);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(last, lastSize, bid, bidSize, ask, askSize, volume, openInterest);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("last", last)
                .add("lastSize", lastSize)
                .add("bid", bid)
                .add("bidSize", bidSize)
                .add("ask", ask)
                .add("askSize", askSize)
                .add("volume", volume)
                .add("openInterest", openInterest)
                .toString();
    }
}
