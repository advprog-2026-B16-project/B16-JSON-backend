package id.ac.ui.cs.advprog.jsonbackend.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("PENDING"),
    PAID("PAID"),
    PURCHASED("PURCHASED"),
    SHIPPED("SHIPPED"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String value;

    OrderStatus(final String value) {
        this.value = value;
    }
}
