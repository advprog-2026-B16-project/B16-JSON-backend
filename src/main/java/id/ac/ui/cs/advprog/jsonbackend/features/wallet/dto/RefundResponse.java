package id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;

import java.math.BigDecimal;
import java.util.UUID;

public class RefundResponse {

    private final UUID id;
    private final UUID originalTransactionId;
    private final UUID refundTransactionId;
    private final UUID orderId;
    private final BigDecimal amount;
    private final String reason;
    private final TransactionStatus status;

    public RefundResponse(Refund refund) {
        this.id = refund.getId();
        this.originalTransactionId = refund.getOriginalTransactionId();
        this.refundTransactionId = refund.getRefundTransactionId();
        this.orderId = refund.getOrderId();
        this.amount = refund.getAmount();
        this.reason = refund.getReason();
        this.status = refund.getStatus();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOriginalTransactionId() {
        return originalTransactionId;
    }

    public UUID getRefundTransactionId() {
        return refundTransactionId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}
