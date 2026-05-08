package id.ac.ui.cs.advprog.jsonbackend.features.order.event;

import java.util.UUID;

public record PaymentResultEvent(
        UUID orderId,
        boolean isSuccess,
        String errorMessage
) {}