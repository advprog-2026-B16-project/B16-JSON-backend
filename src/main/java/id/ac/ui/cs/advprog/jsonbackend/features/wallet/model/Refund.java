package id.ac.ui.cs.advprog.jsonbackend.features.wallet.model;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "refund_requests",
        uniqueConstraints = @UniqueConstraint(columnNames = "original_transaction_id")
)
@Getter
@Setter
@NoArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "original_transaction_id", nullable = false, updatable = false)
    private UUID originalTransactionId;

    @Column(name = "refund_transaction_id")
    private UUID refundTransactionId;

    @Column(name = "requester_id", nullable = false, updatable = false)
    private UUID requesterId;

    @Column(name = "wallet_id", nullable = false, updatable = false)
    private UUID walletId;

    @Column(name = "order_id", updatable = false)
    private UUID orderId;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(name = "reason", length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public Refund(Transaction originalTransaction, String reason) {
        this.originalTransactionId = originalTransaction.getId();
        this.requesterId = originalTransaction.getUserId();
        this.walletId = originalTransaction.getWalletId();
        this.orderId = originalTransaction.getOrderId();
        this.amount = originalTransaction.getAmount();
        this.reason = normalizeReason(reason);
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void markSuccess(UUID refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
        this.status = TransactionStatus.SUCCESS;
        this.processedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = TransactionStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }

    private String normalizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return null;
        }
        return reason.trim();
    }
}
