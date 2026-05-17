package id.ac.ui.cs.advprog.jsonbackend.features.order.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        String titipersId,
        String productId,
        int quantity,
        BigDecimal totalAmount
) {}