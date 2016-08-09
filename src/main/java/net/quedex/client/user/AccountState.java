package net.quedex.client.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkNotNull;

public class AccountState {

    public enum Status {
        ACTIVE, MARGIN_CALL, LIQUIDATION;

        @JsonCreator
        private static Status deserialize(String value) {
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
            @JsonProperty("balance") BigDecimal balance,
            @JsonProperty("free_balance") BigDecimal freeBalance,
            @JsonProperty("total_initial_margin") BigDecimal totalInitialMargin,
            @JsonProperty("total_maintenance_margin") BigDecimal totalMaintenanceMargin,
            @JsonProperty("total_unsettled_pnl") BigDecimal totalUnsettledPnL,
            @JsonProperty("total_locked_for_orders") BigDecimal totalLockedForOrders,
            @JsonProperty("total_pending_withdrawal") BigDecimal totalPendingWithdrawal,
            @JsonProperty("account_status") Status status
    ) {
        this.balance = checkNotNull(balance, "null balance");
        this.freeBalance = checkNotNull(freeBalance, "null freeBalance");
        this.totalInitialMargin = checkNotNull(totalInitialMargin, "null totalInitialMargin");
        this.totalMaintenanceMargin = checkNotNull(totalMaintenanceMargin, "null totalMaintenanceMargin");
        this.totalUnsettledPnL = checkNotNull(totalUnsettledPnL, "null totalUnsettledPnL");
        this.totalLockedForOrders = checkNotNull(totalLockedForOrders, "null totalLockedForOrders");
        this.totalPendingWithdrawal = checkNotNull(totalPendingWithdrawal, "null totalPendingWithdrawal");
        this.status = checkNotNull(status, "null status");
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getFreeBalance() {
        return freeBalance;
    }

    public BigDecimal getTotalInitialMargin() {
        return totalInitialMargin;
    }

    public BigDecimal getTotalMaintenanceMargin() {
        return totalMaintenanceMargin;
    }

    public BigDecimal getTotalLockedForOrders() {
        return totalLockedForOrders;
    }

    public BigDecimal getTotalUnsettledPnL() {
        return totalUnsettledPnL;
    }

    public BigDecimal getTotalPendingWithdrawal() {
        return totalPendingWithdrawal;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountState that = (AccountState) o;
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
    public int hashCode() {
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
    public String toString() {
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
