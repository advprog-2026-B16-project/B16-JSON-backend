package id.ac.ui.cs.advprog.jsonbackend.features.payment.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class PaymentRequest {

    @NotNull
    private UUID orderId;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}
