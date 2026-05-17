package id.ac.ui.cs.advprog.jsonbackend.features.order.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID orderId,
        String titipersId,
        BigDecimal totalRefundAmount,
        String reason
) {}