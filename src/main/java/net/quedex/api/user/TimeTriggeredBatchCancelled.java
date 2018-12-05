package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class TimeTriggeredBatchCancelled {

    private final long batchId;

    @JsonCreator
    public TimeTriggeredBatchCancelled(final @JsonProperty("timer_id") long batchId) {
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
        final TimeTriggeredBatchCancelled that = (TimeTriggeredBatchCancelled) o;
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
