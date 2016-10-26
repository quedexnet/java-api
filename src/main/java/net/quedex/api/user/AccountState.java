package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class AccountState
{
    public enum Status
    {
        ACTIVE, MARGIN_CALL, LIQUIDATION;

        @JsonCreator
        private static Status deserialize(final String value)
        {
            return valueOf(value.toUpperCase());
        }
    }

    private final BigDecimal balance;
    private final BigDecimal freeBalance;
    private final BigDecimal totalInitialMargin;
    private final BigDecimal totalMaintenanceMargin;
    private final BigDecimal totalLockedForOrders;
    private final BigDecimal totalUnsettledPnL;
    private final BigDecimal totalPendingWithdrawal;

    private final Status status;

    @JsonCreator
    public AccountState(
        @JsonProperty("balance") final BigDecimal balance,
        @JsonProperty("free_balance") final BigDecimal freeBalance,
        @JsonProperty("total_initial_margin") final BigDecimal totalInitialMargin,
        @JsonProperty("total_maintenance_margin") final BigDecimal totalMaintenanceMargin,
        @JsonProperty("total_unsettled_pnl") final BigDecimal totalUnsettledPnL,
        @JsonProperty("total_locked_for_orders") final BigDecimal totalLockedForOrders,
        @JsonProperty("total_pending_withdrawal") final BigDecimal totalPendingWithdrawal,
        @JsonProperty("account_status") final Status status)
    {
        this.balance = checkNotNull(balance, "null balance");
        this.freeBalance = checkNotNull(freeBalance, "null freeBalance");
        this.totalInitialMargin = checkNotNull(totalInitialMargin, "null totalInitialMargin");
        this.totalMaintenanceMargin = checkNotNull(totalMaintenanceMargin, "null totalMaintenanceMargin");
        this.totalUnsettledPnL = checkNotNull(totalUnsettledPnL, "null totalUnsettledPnL");
        this.totalLockedForOrders = checkNotNull(totalLockedForOrders, "null totalLockedForOrders");
        this.totalPendingWithdrawal = checkNotNull(totalPendingWithdrawal, "null totalPendingWithdrawal");
        this.status = checkNotNull(status, "null status");
    }

    public BigDecimal getBalance()
    {
        return balance;
    }

    public BigDecimal getFreeBalance()
    {
        return freeBalance;
    }

    public BigDecimal getTotalInitialMargin()
    {
        return totalInitialMargin;
    }

    public BigDecimal getTotalMaintenanceMargin()
    {
        return totalMaintenanceMargin;
    }

    public BigDecimal getTotalLockedForOrders()
    {
        return totalLockedForOrders;
    }

    public BigDecimal getTotalUnsettledPnL()
    {
        return totalUnsettledPnL;
    }

    public BigDecimal getTotalPendingWithdrawal()
    {
        return totalPendingWithdrawal;
    }

    public Status getStatus()
    {
        return status;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final AccountState that = (AccountState) o;
        return Objects.equal(balance, that.balance) &&
            Objects.equal(freeBalance, that.freeBalance) &&
            Objects.equal(totalInitialMargin, that.totalInitialMargin) &&
            Objects.equal(totalMaintenanceMargin, that.totalMaintenanceMargin) &&
            Objects.equal(totalLockedForOrders, that.totalLockedForOrders) &&
            Objects.equal(totalUnsettledPnL, that.totalUnsettledPnL) &&
            Objects.equal(totalPendingWithdrawal, that.totalPendingWithdrawal) &&
            status == that.status;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(
            balance,
            freeBalance,
            totalInitialMargin,
            totalMaintenanceMargin,
            totalLockedForOrders,
            totalUnsettledPnL,
            totalPendingWithdrawal,
            status
        );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
            .add("balance", balance)
            .add("freeBalance", freeBalance)
            .add("totalInitialMargin", totalInitialMargin)
            .add("totalMaintenanceMargin", totalMaintenanceMargin)
            .add("totalLockedForOrders", totalLockedForOrders)
            .add("totalUnsettledPnL", totalUnsettledPnL)
            .add("totalPendingWithdrawal", totalPendingWithdrawal)
            .add("status", status)
            .toString();
    }
}
