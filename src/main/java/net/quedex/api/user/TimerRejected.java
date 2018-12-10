package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class TimerRejected {

    public enum Cause {
        TOO_MANY_ACTIVE_TIMERS,
        TIMER_ALREADY_EXPIRED,
        TIMER_ALREADY_EXISTS;

        @JsonCreator
        private static Cause deserialize(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    private final long timerId;
    private final Cause cause;

    @JsonCreator
    public TimerRejected(final @JsonProperty("timer_id") long timerId,
                         final @JsonProperty("cause") Cause cause) {
        this.timerId = timerId;
        this.cause = checkNotNull(cause, "Null cause");
    }

    public long getTimerId() {
        return timerId;
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimerRejected that = (TimerRejected) o;
        return timerId == that.timerId &&
            cause == that.cause;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timerId, cause);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("timerId", timerId)
            .add("cause", cause)
            .toString();
    }
}
