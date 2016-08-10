package net.quedex.client.user;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderSide {
    BUY, SELL;

    @JsonCreator
    private static OrderSide deserialize(String value) {
        return valueOf(value.toUpperCase());
    }
}
