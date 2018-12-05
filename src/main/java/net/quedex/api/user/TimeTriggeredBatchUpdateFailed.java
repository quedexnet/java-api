package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class TimeTriggeredBatchUpdateFailed {

    public enum Cause {
        NOT_FOUND,
        TIMER_EXECUTION_INTERVAL_BROKEN;

        @JsonCreator
        private static Cause deserialize(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    private final long batchId;
    private final Cause cause;

    @JsonCreator
    public TimeTriggeredBatchUpdateFailed(final @JsonProperty("timer_id") long batchId,
                                          final @JsonProperty("cause") Cause cause) {
        this.batchId = batchId;
        this.cause = checkNotNull(cause, "Null cause");
    }

    public long getBatchId() {
        return batchId;
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeTriggeredBatchUpdateFailed that = (TimeTriggeredBatchUpdateFailed) o;
        return batchId == that.batchId &&
            cause == that.cause;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(batchId, cause);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("batchId", batchId)
            .add("cause", cause)
            .toString();
    }
}
