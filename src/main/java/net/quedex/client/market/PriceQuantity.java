package net.quedex.client.market;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public final class PriceQuantity { // TODO: better constructors

    private final BigDecimal price; // may be null for market order
    private final int quantity;

    @JsonCreator
    private PriceQuantity(BigDecimal[] priceQty) {
        checkArgument(priceQty.length == 2, "priceQty.length=%s != 2", priceQty.length);
        this.price = priceQty[0];
        this.quantity = priceQty[1].intValueExact();
    }

    public PriceQuantity(BigDecimal price, int quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public Optional<BigDecimal> getPrice() {
        return Optional.ofNullable(price);
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceQuantity priceQuantity = (PriceQuantity) o;
        return quantity == priceQuantity.quantity &&
                Objects.equal(price, priceQuantity.price);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(price, quantity);
    }

    @Override
    public String toString() {
        return "[" + price + ',' + quantity + ']';
    }
}
