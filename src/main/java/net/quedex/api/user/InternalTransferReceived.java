package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

public class InternalTransferReceived {

    private final long sourceUserId;
    private final BigDecimal amount;

    @JsonCreator
    public InternalTransferReceived(
        @JsonProperty("source_account_id") long sourceUserId,
        @JsonProperty("amount") BigDecimal amount
    ) {
        checkArgument(sourceUserId > 0, "sourceUserId=%s <= 0", sourceUserId);
        checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, "amount=%s <= 0", amount);
        this.sourceUserId = sourceUserId;
        this.amount = amount;
    }

    public long getSourceUserId() {
        return sourceUserId;
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
        final InternalTransferReceived that = (InternalTransferReceived) o;
        return sourceUserId == that.sourceUserId &&
            Objects.equal(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sourceUserId, amount);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("sourceUserId", sourceUserId)
            .add("amount", amount)
            .toString();
    }
}
