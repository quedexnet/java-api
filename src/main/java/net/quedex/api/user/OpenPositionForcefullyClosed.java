package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import net.quedex.api.user.OpenPosition.PositionSide;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class OpenPositionForcefullyClosed {

    public enum Cause {
        BANKRUPTCY,
        DELEVERAGING;

        @JsonCreator
        private static Cause deserialize(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    private final int instrumentId;
    private final PositionSide side;
    private final int closedQuantity;
    private final int remainingQuantity;
    private final BigDecimal closePrice;
    private final Cause cause;

    @JsonCreator
    public OpenPositionForcefullyClosed(final @JsonProperty("instrument_id") int instrumentId,
                                        final @JsonProperty("side") PositionSide side,
                                        final @JsonProperty("closed_quantity") int closedQuantity,
                                        final @JsonProperty("remaining_quantity") int remainingQuantity,
                                        final @JsonProperty("close_price") BigDecimal closePrice,
                                        final @JsonProperty("cause") Cause cause) {
        checkArgument(instrumentId > 0, "instrumentId=%s <= 0", instrumentId);
        checkArgument(closedQuantity >= 0, "closedQuantity=%s < 0", closedQuantity);
        checkArgument(remainingQuantity >= 0, "remainingQuantity=%s < 0", remainingQuantity);
        checkArgument(closePrice != null && closePrice.compareTo(BigDecimal.ZERO) > 0, "closePrice=%s <= 0", closePrice);
        this.instrumentId = instrumentId;
        this.side = checkNotNull(side, "null side");
        this.closedQuantity = closedQuantity;
        this.remainingQuantity = remainingQuantity;
        this.closePrice = closePrice;
        this.cause = checkNotNull(cause, "cause");
    }

    public int getInstrumentId() {
        return instrumentId;
    }
    public PositionSide getSide() {
        return side;
    }
    public int getClosedQuantity() {
        return closedQuantity;
    }
    public int getRemainingQuantity() {
        return remainingQuantity;
    }
    public BigDecimal getClosePrice() {
        return closePrice;
    }
    public Cause getCause() {
        return cause;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OpenPositionForcefullyClosed that = (OpenPositionForcefullyClosed) o;
        return instrumentId == that.instrumentId &&
            side == that.side &&
            closedQuantity == that.closedQuantity &&
            remainingQuantity == that.remainingQuantity &&
            cause == that.cause &&
            Objects.equal(closePrice, that.closePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(instrumentId, side, closedQuantity, remainingQuantity, closePrice, cause);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("instrumentId", instrumentId)
            .add("side", side)
            .add("closedQuantity", closedQuantity)
            .add("remainingQuantity", remainingQuantity)
            .add("closePrice", closePrice)
            .add("cause", cause)
            .toString();
    }
}

