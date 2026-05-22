package id.ac.ui.cs.advprog.jsonbackend.features.payment.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponse {

    private final UUID id;
    private final UUID orderId;
    private final UUID transactionId;
    private final String referenceCode;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final LocalDateTime expiresAt;
    private final LocalDateTime paidAt;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.orderId = payment.getOrderId();
        this.transactionId = payment.getTransactionId();
        this.referenceCode = payment.getReferenceCode();
        this.amount = payment.getAmount();
        this.status = payment.getStatus();
        this.expiresAt = payment.getExpiresAt();
        this.paidAt = payment.getPaidAt();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}
