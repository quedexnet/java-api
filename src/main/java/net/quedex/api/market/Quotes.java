package net.quedex.api.market;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

public class Quotes {

    private final int instrumentId;
    private final BigDecimal last;
    private final int lastQuantity;
    private final BigDecimal bid;
    private final Integer bidQuantity;
    private final BigDecimal ask;
    private final Integer askQuantity;
    private final int volume;
    private final int openInterest;
    private final BigDecimal tap;
    private final BigDecimal lowerLimit;
    private final BigDecimal upperLimit;

    @JsonCreator
    public Quotes(
            @JsonProperty("instrument_id") int instrumentId,
            @JsonProperty("last") BigDecimal last,
            @JsonProperty("last_quantity") int lastQuantity,
            @JsonProperty("bid") BigDecimal bid,
            @JsonProperty("bid_quantity") Integer bidQuantity,
            @JsonProperty("ask") BigDecimal ask,
            @JsonProperty("ask_quantity") Integer askQuantity,
            @JsonProperty("volume") int volume,
            @JsonProperty("open_interest") int openInterest,
            @JsonProperty("tap") BigDecimal tap,
            @JsonProperty("lower_limit") BigDecimal lowerLimit,
            @JsonProperty("upper_limit") BigDecimal upperLimit
    ) {
        checkArgument(last.compareTo(BigDecimal.ZERO) > 0, "last=%s <= 0", last);
        checkArgument(lastQuantity >= 0, "lastQuantity=%s < 0", lastQuantity); // may be 0 when reference trade
        checkArgument(volume >= 0, "volume=%s < 0", volume);
        checkArgument(bid == null || bid.compareTo(BigDecimal.ZERO) > 0, "bid=%s <= 0", bid);
        checkArgument(bidQuantity == null || bidQuantity > 0, "bidQuantity=%s <= 0", bidQuantity);
        checkArgument(ask == null || ask.compareTo(BigDecimal.ZERO) > 0, "ask=%s <= 0", ask);
        checkArgument(askQuantity == null || askQuantity > 0, "askQuantity=%s <= 0", askQuantity);
        checkArgument(openInterest >= 0, "openInterest=%s < 0", openInterest);
        checkArgument(tap == null || tap.compareTo(BigDecimal.ZERO) > 0, "tap=%s <= 0", tap);
        checkArgument(lowerLimit == null || lowerLimit.compareTo(BigDecimal.ZERO) > 0, "lowerLimit=%s <= 0", lowerLimit);
        checkArgument(upperLimit == null || upperLimit.compareTo(BigDecimal.ZERO) > 0, "upperLimit=%s <= 0", upperLimit);
        this.instrumentId = instrumentId;
        this.last = last;
        this.lastQuantity = lastQuantity;
        this.bid = bid;
        this.bidQuantity = bidQuantity;
        this.ask = ask;
        this.askQuantity = askQuantity;
        this.volume = volume;
        this.openInterest = openInterest;
        this.tap = tap;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public BigDecimal getLast() {
        return last;
    }

    public int getLastQuantity() {
        return lastQuantity;
    }

    /**
     * @return fist level of the order book if present, null otherwise
     */
    public PriceQuantity getBid() {
        return bidQuantity == null ? null : new PriceQuantity(bid, bidQuantity);
    }

    /**
     * @return fist level of the order book if present, null otherwise
     */
    public PriceQuantity getAsk() {
        return askQuantity == null ? null : new PriceQuantity(ask, askQuantity);
    }

    public int getVolume() {
        return volume;
    }

    public int getOpenInterest() {
        return openInterest;
    }

    public BigDecimal getTap() {
        return tap;
    }

    public BigDecimal getLowerLimit() {
        return lowerLimit;
    }

    public BigDecimal getUpperLimit() {
        return upperLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quotes quotes = (Quotes) o;
        return instrumentId == quotes.instrumentId &&
                lastQuantity == quotes.lastQuantity &&
                volume == quotes.volume &&
                openInterest == quotes.openInterest &&
                Objects.equal(last, quotes.last) &&
                Objects.equal(bid, quotes.bid) &&
                Objects.equal(bidQuantity, quotes.bidQuantity) &&
                Objects.equal(ask, quotes.ask) &&
                Objects.equal(askQuantity, quotes.askQuantity) &&
                Objects.equal(tap, quotes.tap) &&
                Objects.equal(lowerLimit, quotes.lowerLimit) &&
                Objects.equal(upperLimit, quotes.upperLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
            instrumentId,
            last,
            lastQuantity,
            bid,
            bidQuantity,
            ask,
            askQuantity,
            volume,
            openInterest,
            tap,
            lowerLimit,
            upperLimit
        );
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instrumentId", instrumentId)
                .add("last", last)
                .add("lastQuantity", lastQuantity)
                .add("bid", bid)
                .add("bidQuantity", bidQuantity)
                .add("ask", ask)
                .add("askQuantity", askQuantity)
                .add("volume", volume)
                .add("openInterest", openInterest)
                .add("tap", tap)
                .add("lowerLimit", lowerLimit)
                .add("upperLimit", upperLimit)
                .toString();
    }
}
