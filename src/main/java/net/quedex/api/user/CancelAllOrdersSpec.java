package net.quedex.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CancelAllOrdersSpec implements OrderSpec {
    public static final CancelAllOrdersSpec INSTANCE = new CancelAllOrdersSpec();

    @JsonProperty("type")
    private String getType() {
        return "cancel_all_orders";
    }
}
