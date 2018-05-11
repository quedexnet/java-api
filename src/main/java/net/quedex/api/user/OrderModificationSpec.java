package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

public class OrderModificationSpec implements OrderSpec {

    private final long clientOrderId;
    private final Integer newQuantity;
    private final BigDecimal newLimitPrice;
    private final boolean postOnly;

    public OrderModificationSpec(final long clientOrderId,
                                 final int newQuantity,
                                 final BigDecimal newLimitPrice,
                                 final boolean postOnly) {
        this(clientOrderId, newQuantity, newLimitPrice, postOnly, null);
    }

    public OrderModificationSpec(final long clientOrderId, final int newQuantity, final BigDecimal newLimitPrice) {
        this(clientOrderId, newQuantity, newLimitPrice, false, null);
    }

    public OrderModificationSpec(long clientOrderId, int newQuantity) {
        this(clientOrderId, newQuantity, null, false, null);
    }

    public OrderModificationSpec(final long clientOrderId, final BigDecimal newLimitPrice, final boolean postOnly) {
        this(clientOrderId, null, newLimitPrice, postOnly, null);
    }

    public OrderModificationSpec(final long clientOrderId, final BigDecimal newLimitPrice) {
        this(clientOrderId, null, newLimitPrice, false, null);
    }

    private OrderModificationSpec(final long clientOrderId,
                                  final Integer newQuantity,
                                  final BigDecimal newLimitPrice,
                                  final boolean postOnly,
                                  final Void dummy) {
        checkArgument(clientOrderId > 0, "clientOrderId=%s <= 0", clientOrderId);
        checkArgument(
            newQuantity != null || newLimitPrice != null,
            "at least one of newQuantity and newLimitPrice must be specified"
        );
        checkArgument(newQuantity == null || newQuantity > 0, "newQuantity=%s <= 0", newQuantity);
        checkArgument(
            newLimitPrice == null || newLimitPrice.compareTo(BigDecimal.ZERO) > 0,
            "newLImitPrice=%s <= 0", newLimitPrice
        );

        this.clientOrderId = clientOrderId;
        this.newQuantity = newQuantity;
        this.newLimitPrice = newLimitPrice;
        this.postOnly = postOnly;
    }

    @JsonProperty("client_order_id")
    public long getClientOrderId() {
        return clientOrderId;
    }

    /**
     * @return new quantity of the order if present, null otherwise
     */
    @JsonProperty("new_quantity")
    public Integer getNewQuantity() {
        return newQuantity;
    }

    /**
     * @return new price of the order if present, null otherwise
     */
    @JsonProperty("new_limit_price")
    public BigDecimal getNewLimitPrice() {
        return newLimitPrice;
    }

    @JsonProperty("post_only")
    public Boolean getPostOnly() {
        return postOnly;
    }

    @JsonProperty("type")
    private String getType() {
        return "modify_order";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderModificationSpec that = (OrderModificationSpec) o;
        return clientOrderId == that.clientOrderId &&
                Objects.equal(newQuantity, that.newQuantity) &&
                Objects.equal(newLimitPrice, that.newLimitPrice) &&
                Objects.equal(postOnly, that.postOnly);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientOrderId, newQuantity, newLimitPrice, postOnly);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clientOrderId", clientOrderId)
                .add("newQuantity", newQuantity)
                .add("newLimitPrice", newLimitPrice)
                .add("postOnly", postOnly)
                .toString();
    }
}
