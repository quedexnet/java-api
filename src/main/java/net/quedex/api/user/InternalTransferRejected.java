package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class InternalTransferRejected {

    public enum Cause {
        INSUFFICIENT_FUNDS,
        FORBIDDEN;

        @JsonCreator
        private static Cause deserialize(String value) {
            return valueOf(value.toUpperCase());
        }
    }

    private final long destinationUserId;
    private final BigDecimal amount;
    private final Cause cause;

    @JsonCreator
    public InternalTransferRejected(
        @JsonProperty("destination_account_id") long destinationUserId,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("cause") Cause cause
    ) {
        checkArgument(destinationUserId > 0, "destinationUserId=%s <= 0", destinationUserId);
        checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, "amount=%s <= 0", amount);
        this.destinationUserId = destinationUserId;
        this.amount = amount;
        this.cause = checkNotNull(cause, "cause");
    }

    public long getDestinationUserId() {
        return destinationUserId;
    }

    public BigDecimal getAmount() {
        return amount;
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
        final InternalTransferRejected that = (InternalTransferRejected) o;
        return destinationUserId == that.destinationUserId &&
            Objects.equal(amount, that.amount) &&
            cause == that.cause;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(destinationUserId, amount, cause);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("destinationUserId", destinationUserId)
            .add("amount", amount)
            .add("cause", cause)
            .toString();
    }
}
