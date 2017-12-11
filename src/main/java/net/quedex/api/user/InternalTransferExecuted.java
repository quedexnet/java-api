package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

public class InternalTransferExecuted {

    private final long destinationUserId;
    private final BigDecimal amount;

    @JsonCreator
    public InternalTransferExecuted(
        @JsonProperty("destination_account_id") long destinationUserId,
        @JsonProperty("amount") BigDecimal amount
    ) {
        checkArgument(destinationUserId > 0, "destinationUserId=%s <= 0", destinationUserId);
        checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, "amount=%s <= 0", amount);
        this.destinationUserId = destinationUserId;
        this.amount = amount;
    }

    public long getDestinationUserId() {
        return destinationUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InternalTransferExecuted that = (InternalTransferExecuted) o;
        return destinationUserId == that.destinationUserId &&
            Objects.equal(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(destinationUserId, amount);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("destinationUserId", destinationUserId)
            .add("amount", amount)
            .toString();
    }
}
