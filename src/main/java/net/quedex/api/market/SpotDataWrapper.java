package net.quedex.api.market;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class SpotDataWrapper {

    // maps from underlying symbol to SpotData
    private final @JsonProperty("spot_data") Map<String, SpotData> spotData;
    private final @JsonProperty("update_time") long timestamp;

    @JsonCreator
    public SpotDataWrapper(final @JsonProperty("spot_data") Map<String, SpotData> spotData,
                           final @JsonProperty("update_time") long timestamp) {

        checkArgument(timestamp >= 0, "timestamp=%s < 0", timestamp);
        this.spotData = checkNotNull(spotData, "spotData");
        this.timestamp = timestamp;
    }

    public Map<String, SpotData> getSpotData() {
        return spotData;
    }
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SpotDataWrapper that = (SpotDataWrapper) o;
        return Objects.equal(spotData, that.spotData) &&
            timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(spotData, timestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("spotData", spotData)
            .add("timestamp", timestamp)
            .toString();
    }

    public static class SpotData {
        private final BigDecimal spotIndex;
        private final BigDecimal spotIndexChange;
        private final BigDecimal settlementIndex;
        private final BigDecimal settlementIndexChange;
        private final List<String> constituents;
        private final Map<String, BigDecimal> spotQuotes;

        @JsonCreator
        public SpotData(final @JsonProperty("spot_index") BigDecimal spotIndex,
                        final @JsonProperty("spot_index_change") BigDecimal spotIndexChange,
                        final @JsonProperty("settlement_index") BigDecimal settlementIndex,
                        final @JsonProperty("settlement_index_change") BigDecimal settlementIndexChange,
                        final @JsonProperty("constituents") List<String> constituents,
                        final @JsonProperty("spot_quotes") Map<String, BigDecimal> spotQuotes) {
            checkArgument(spotIndex != null && spotIndex.compareTo(BigDecimal.ZERO) > 0, "spotIndex=%s <= 0", spotIndex);
            checkArgument(settlementIndex != null && settlementIndex.compareTo(BigDecimal.ZERO) > 0, "settlementIndex=%s <= 0", settlementIndex);
            this.spotIndex = spotIndex;
            this.spotIndexChange = checkNotNull(spotIndexChange, "spotIndexChange");
            this.settlementIndex = settlementIndex;
            this.settlementIndexChange = checkNotNull(settlementIndexChange, "settlementIndexChange");
            this.constituents = checkNotNull(constituents, "constituents");
            this.spotQuotes = checkNotNull(spotQuotes, "spotQuotes");
        }

        public BigDecimal getSpotIndex() {
            return spotIndex;
        }
        public BigDecimal getSpotIndexChange() {
            return spotIndexChange;
        }
        public BigDecimal getSettlementIndex() {
            return settlementIndex;
        }
        public BigDecimal getSettlementIndexChange() {
            return settlementIndexChange;
        }
        public List<String> getConstituents() {
            return constituents;
        }
        public Map<String, BigDecimal> getSpotQuotes() {
            return spotQuotes;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final SpotData that = (SpotData) o;
            return Objects.equal(spotIndex, that.spotIndex) &&
                Objects.equal(spotIndexChange, that.spotIndexChange) &&
                Objects.equal(settlementIndex, that.settlementIndex) &&
                Objects.equal(settlementIndexChange, that.settlementIndexChange) &&
                Objects.equal(constituents, that.constituents) &&
                Objects.equal(spotQuotes, that.spotQuotes);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(spotIndex, spotIndexChange, settlementIndex, settlementIndexChange, constituents, spotQuotes);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("spotIndex", spotIndex)
                .add("spotIndexChange", spotIndexChange)
                .add("settlementIndex", settlementIndex)
                .add("settlementIndexChange", settlementIndexChange)
                .add("constituents", constituents)
                .add("spotQuotes", spotQuotes)
                .toString();
        }
    }
}

