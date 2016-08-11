package net.quedex.client.market;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Instrument {

    private static final int SETTLEMENT_HOUR_UTC_MILLIS = 8 * 60 * 60 * 1000;

    public enum Type {
        FUTURES,
        OPTION;

        @JsonCreator
        private static Type deserialize(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    public enum OptionType {
        CALL_EUROPEAN,
        PUT_EUROPEAN;

        @JsonCreator
        private static OptionType deserialize(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    private final int instrumentId;
    private final String symbol;
    private final Type type;
    private final BigDecimal tickSize;
    private final long issueDate;
    private final long expirationDate;
    private final String underlyingSymbol;
    private final int notionalAmount;
    private final BigDecimal feeFraction;
    private final BigDecimal takerToMakerFeeFraction;
    private final BigDecimal initialMarginFraction;
    private final BigDecimal maintenanceMarginFraction;
    private final BigDecimal strike;                    // nullable, option only
    private final OptionType optionType;                // nullable, option only

    @JsonCreator
    public Instrument(
            @JsonProperty("symbol") String symbol,
            @JsonProperty("instrument_id") int instrumentId,
            @JsonProperty("type") Type type,
            @JsonProperty("option_type") OptionType optionType,
            @JsonProperty("tick_size") BigDecimal tickSize,
            @JsonProperty("issue_date") long issueDate,
            @JsonProperty("expiration_date") long expirationDate,
            @JsonProperty("underlying_symbol") String underlyingSymbol,
            @JsonProperty("notional_amount") int notionalAmount,
            @JsonProperty("fee") BigDecimal feeFraction,
            @JsonProperty("taker_to_maker") BigDecimal takerToMakerFraction,
            @JsonProperty("initial_margin") BigDecimal initialMarginFraction,
            @JsonProperty("maintenance_margin") BigDecimal maintenanceMarginFraction,
            @JsonProperty("strike") BigDecimal strike
    ) {
        checkArgument(!symbol.isEmpty(), "Empty symbol");
        checkArgument(tickSize.compareTo(BigDecimal.ZERO) > 0, "tickSize=%s <=0", tickSize);
        checkArgument(issueDate > 0, "issueDate=%s <= 0", issueDate);
        checkArgument(expirationDate > issueDate, "expirationDate=%s <= %s=issueDate", expirationDate, issueDate);
        checkArgument(!underlyingSymbol.isEmpty(), "Empty underlyingSymbol");
        checkArgument(notionalAmount > 0, "notionalAmount=%s <= 0", notionalAmount);
        checkArgument(feeFraction.compareTo(BigDecimal.ZERO) >= 0, "feeFraction=%s < 0", takerToMakerFraction);
        checkArgument(takerToMakerFraction.compareTo(BigDecimal.ZERO) >= 0, "takerToMaker=%s < 0", takerToMakerFraction);
        checkArgument(
                initialMarginFraction.compareTo(BigDecimal.ZERO) >= 0,
                "initialMarginFraction=%s < 0", initialMarginFraction
        );
        checkArgument(
                maintenanceMarginFraction.compareTo(BigDecimal.ZERO) >= 0,
                "maintenanceMarginFraction=%s < 0", maintenanceMarginFraction
        );

        this.symbol = symbol;
        this.instrumentId = instrumentId;
        this.type = checkNotNull(type, "null instrumentType");
        this.tickSize = tickSize;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.underlyingSymbol = underlyingSymbol;
        this.notionalAmount = notionalAmount;
        this.feeFraction = feeFraction;
        this.takerToMakerFeeFraction = takerToMakerFraction;
        this.initialMarginFraction = initialMarginFraction;
        this.maintenanceMarginFraction = maintenanceMarginFraction;

        if (this.type == Type.FUTURES) {
            checkArgument(strike == null, "Expected null strike");
            checkArgument(optionType == null, "Expected null optionType");
            this.strike = null;
            this.optionType = null;
        } else {
            checkArgument(strike.compareTo(BigDecimal.ZERO) > 0, "strike=%s <= 0", strike);
            this.strike = strike;
            this.optionType = checkNotNull(optionType, "null optionType");
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public Type getType() {
        return type;
    }

    public BigDecimal getTickSize() {
        return tickSize;
    }

    public long getIssueDate() {
        return issueDate;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public String getUnderlyingSymbol() {
        return underlyingSymbol;
    }

    public int getNotionalAmount() {
        return notionalAmount;
    }

    public BigDecimal getFeeFraction() {
        return feeFraction;
    }

    public BigDecimal getTakerToMakerFeeFraction() {
        return takerToMakerFeeFraction;
    }

    /**
     * @return taker fee fraction
     */
    public BigDecimal getTakerFeeFraction() {
        return feeFraction.add(takerToMakerFeeFraction);
    }

    /**
     * @return maker fee fraction (may be negative)
     */
    public BigDecimal getMakerFeeFraction() {
        return feeFraction.subtract(takerToMakerFeeFraction);
    }

    public BigDecimal getInitialMarginFraction() {
        return initialMarginFraction;
    }

    public BigDecimal getMaintenanceMarginFraction() {
        return maintenanceMarginFraction;
    }

    public Optional<BigDecimal> getStrike() {
        return Optional.ofNullable(strike);
    }

    public Optional<OptionType> getOptionType() {
        return Optional.ofNullable(optionType);
    }

    public boolean isFutures() {
        return type == Type.FUTURES;
    }

    public boolean isTraded(long currentTimeMillis) {
        return issueDate < currentTimeMillis && currentTimeMillis < expirationDate + SETTLEMENT_HOUR_UTC_MILLIS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instrument that = (Instrument) o;
        return instrumentId == that.instrumentId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(instrumentId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instrumentId", instrumentId)
                .add("symbol", symbol)
                .add("instrumentType", type)
                .add("tickSize", tickSize)
                .add("issueDate", issueDate)
                .add("expirationDate", expirationDate)
                .add("underlyingSymbol", underlyingSymbol)
                .add("notionalAmount", notionalAmount)
                .add("feeFraction", feeFraction)
                .add("takerToMakerFeeFraction", takerToMakerFeeFraction)
                .add("initialMarginFraction", initialMarginFraction)
                .add("maintenanceMarginFraction", maintenanceMarginFraction)
                .add("strike", strike)
                .add("optionType", optionType)
                .toString();
    }
}
