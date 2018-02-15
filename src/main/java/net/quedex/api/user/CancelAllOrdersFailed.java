package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class CancelAllOrdersFailed {
    public enum Cause {
        SESSION_NOT_ACTIVE;

        @JsonCreator
        public static Cause deserialize(final String str) {
            return valueOf(str.toUpperCase());
        }
    }

    private final Cause cause;

    @JsonCreator
    public CancelAllOrdersFailed(final @JsonProperty("cause") Cause cause) {
        this.cause = checkNotNull(cause, "null cause");
    }

    public Cause getCause() {
        return cause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CancelAllOrdersFailed that = (CancelAllOrdersFailed) o;
        return this.cause == that.cause;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cause);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("cause", cause)
            .toString();
    }
}
