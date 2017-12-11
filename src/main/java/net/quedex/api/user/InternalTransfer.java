package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

public class InternalTransfer {

    private final long destinationAccountId;
    private final BigDecimal amount;

    public InternalTransfer(final long destinationAccountId, final BigDecimal amount) {
        checkArgument(destinationAccountId > 0, "destinationAccountId=%s <= 0", destinationAccountId);
        checkArgument(amount.compareTo(BigDecimal.ZERO) > 0, "amount=%s <= 0", amount);
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
    }

    @JsonProperty("destination_account_id")
    public long getDestinationAccountId() {
        return destinationAccountId;
    }

    @JsonProperty("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    @JsonProperty("type")
    private String getType() {
        return "internal_transfer";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InternalTransfer that = (InternalTransfer) o;
        return destinationAccountId == that.destinationAccountId &&
            Objects.equal(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(destinationAccountId, amount);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("destinationAccountId", destinationAccountId)
            .add("amount", amount)
            .toString();
    }
}
