package net.quedex.client.user;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class OrderModificationSpec {

    private final long clientOrderId;
    private final Integer newQuantity;
    private final BigDecimal newLimitPrice;

    public OrderModificationSpec(long clientOrderId, int newQuantity, BigDecimal newLimitPrice) {
        checkArgument(newQuantity > 0, "newQuantity=%s <= 0", newQuantity);
        checkArgument(newLimitPrice.compareTo(BigDecimal.ZERO) > 0, "limitPrice=%s <= 0", newLimitPrice);
        this.clientOrderId = clientOrderId;
        this.newQuantity = newQuantity;
        this.newLimitPrice = newLimitPrice;
    }

    public OrderModificationSpec(long clientOrderId, int newQuantity) {
        checkArgument(newQuantity > 0, "newQuantity=%s <= 0", newQuantity);
        this.clientOrderId = clientOrderId;
        this.newQuantity = newQuantity;
        this.newLimitPrice = null;
    }

    public OrderModificationSpec(long clientOrderId, BigDecimal newLimitPrice) {
        checkArgument(newLimitPrice.compareTo(BigDecimal.ZERO) > 0, "limitPrice=%s <= 0", newLimitPrice);
        this.clientOrderId = clientOrderId;
        this.newQuantity = null;
        this.newLimitPrice = newLimitPrice;
    }

    public long getClientOrderId() {
        return clientOrderId;
    }

    public Optional<Integer> getNewQuantity() {
        return Optional.ofNullable(newQuantity);
    }

    public Optional<BigDecimal> getNewLimitPrice() {
        return Optional.ofNullable(newLimitPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderModificationSpec that = (OrderModificationSpec) o;
        return clientOrderId == that.clientOrderId &&
                Objects.equal(newQuantity, that.newQuantity) &&
                Objects.equal(newLimitPrice, that.newLimitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientOrderId, newQuantity, newLimitPrice);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clientOrderId", clientOrderId)
                .add("newQuantity", newQuantity)
                .add("newLimitPrice", newLimitPrice)
                .toString();
    }
}
