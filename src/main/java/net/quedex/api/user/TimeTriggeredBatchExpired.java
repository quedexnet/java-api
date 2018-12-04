package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class TimeTriggeredBatchExpired {

    private final long batchId;

    @JsonCreator
    public TimeTriggeredBatchExpired(final @JsonProperty("timer_id") long batchId) {
        this.batchId = batchId;
    }

    public long getBatchId() {
        return batchId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TimeTriggeredBatchExpired that = (TimeTriggeredBatchExpired) o;
        return batchId == that.batchId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(batchId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("batchId", batchId)
            .toString();
    }
}
