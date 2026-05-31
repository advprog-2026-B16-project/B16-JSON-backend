package id.ac.ui.cs.advprog.jsonbackend.features.payment.model;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.state.PaymentStateFactory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payments_order_id", columnList = "order_id"),
                @Index(name = "idx_payments_user_id", columnList = "user_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = "reference_code")
)
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "wallet_id", nullable = false, updatable = false)
    private UUID walletId;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "reference_code", nullable = false, unique = true, updatable = false, length = 40)
    private String referenceCode;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Payment(UUID orderId, UUID userId, UUID walletId, String referenceCode, BigDecimal amount, LocalDateTime expiresAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.walletId = walletId;
        this.referenceCode = referenceCode;
        this.amount = amount;
        this.expiresAt = expiresAt;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public boolean isExpired(LocalDateTime now) {
        return status == PaymentStatus.PENDING && !expiresAt.isAfter(now);
    }

    public void markSuccess(UUID transactionId) {
        transitionTo(PaymentStatus.SUCCESS);
        this.transactionId = transactionId;
        this.paidAt = LocalDateTime.now();
        this.updatedAt = this.paidAt;
    }

    public void markExpired() {
        transitionTo(PaymentStatus.EXPIRED);
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed() {
        transitionTo(PaymentStatus.FAILED);
        this.updatedAt = LocalDateTime.now();
    }

    public void markCancelled() {
        transitionTo(PaymentStatus.CANCELLED);
        this.updatedAt = LocalDateTime.now();
    }

    private void transitionTo(PaymentStatus nextStatus) {
        PaymentStateFactory.getState(this.status).validateTransition(nextStatus);
        this.status = nextStatus;
    }
}
