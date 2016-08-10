package net.quedex.client.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class OpenPosition {

    public enum PositionSide {
        LONG, SHORT;

        @JsonCreator
        private static PositionSide deserialize(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    private final int instrumentId;
    private final BigDecimal pnl; // null for options
    private final BigDecimal maintenanceMargin;
    private final BigDecimal initialMargin;
    private final PositionSide side; // any of LONG, SHORT for a closed position
    private final int quantity;
    private final BigDecimal averageOpeningPrice; // any number for a closed position

    @JsonCreator
    public OpenPosition(
            @JsonProperty("instrument_id") int instrumentId,
            @JsonProperty("pnl") BigDecimal pnl,
            @JsonProperty("maintenance_margin") BigDecimal maintenanceMargin,
            @JsonProperty("initial_margin") BigDecimal initialMargin,
            @JsonProperty("side") PositionSide side,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("average_opening_price") BigDecimal averageOpeningPrice
    ) {
        checkArgument(instrumentId > 0, "instrumentId=%s <= 0", instrumentId);
        checkArgument(maintenanceMargin.compareTo(BigDecimal.ZERO) >= 0, "maintenanceMargin=%s < 0", maintenanceMargin);
        checkArgument(initialMargin.compareTo(BigDecimal.ZERO) >= 0, "initialMargin=%s < 0", initialMargin);
        checkArgument(quantity >= 0, "quantity=%s < 0", quantity);
        this.instrumentId = instrumentId;
        this.pnl = pnl;
        this.maintenanceMargin = maintenanceMargin;
        this.initialMargin = initialMargin;
        this.side = checkNotNull(side, "null side");
        this.quantity = quantity;
        this.averageOpeningPrice = averageOpeningPrice;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    /**
     * @return PnL of this position, present only for {@link net.quedex.client.market.Instrument.InstrumentType#FUTURES}
     *         position
     */
    public Optional<BigDecimal> getPnl() {
        return Optional.ofNullable(pnl);
    }

    public BigDecimal getMaintenanceMargin() {
        return maintenanceMargin;
    }

    public BigDecimal getInitialMargin() {
        return initialMargin;
    }

    /**
     * @return side of this position, may be any of LONG, SHORT for a closed position
     */
    public PositionSide getSide() {
        return side;
    }

    /**
     * @return nonnegative quantity of this position, zero for a closed position
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return weighted (by quantities) average of the prices this position has been opened at, may be any number for a
     *         closed position
     */
    public BigDecimal getAverageOpeningPrice() {
        return averageOpeningPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenPosition that = (OpenPosition) o;
        return instrumentId == that.instrumentId &&
                quantity == that.quantity &&
                Objects.equal(pnl, that.pnl) &&
                Objects.equal(maintenanceMargin, that.maintenanceMargin) &&
                Objects.equal(initialMargin, that.initialMargin) &&
                side == that.side &&
                Objects.equal(averageOpeningPrice, that.averageOpeningPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(instrumentId, pnl, maintenanceMargin, initialMargin, side, quantity, averageOpeningPrice);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instrumentId", instrumentId)
                .add("pnl", pnl)
                .add("maintenanceMargin", maintenanceMargin)
                .add("initialMargin", initialMargin)
                .add("side", side)
                .add("quantity", quantity)
                .add("averageOpeningPrice", averageOpeningPrice)
                .toString();
    }
}
