package id.ac.ui.cs.advprog.jsonbackend.features.order.event;

import java.util.UUID;

public record StockResultEvent(
        UUID orderId,
        boolean isSuccess,
        String errorMessage
) {}