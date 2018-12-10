package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class TimerExpired {

    private final long timerId;

    @JsonCreator
    public TimerExpired(final @JsonProperty("timer_id") long timerId) {
        this.timerId = timerId;
    }

    public long getTimerId() {
        return timerId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TimerExpired that = (TimerExpired) o;
        return timerId == that.timerId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(timerId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("timerId", timerId)
            .toString();
    }
}
