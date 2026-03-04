package id.ac.ui.cs.advprog.jsonbackend.wallet.model;

import id.ac.ui.cs.advprog.jsonbackend.wallet.model.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.enums.TransactionStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String walletId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private BigDecimal amount;

    private String description;

    private LocalDateTime createdAt;

    protected WalletTransaction() {}

    public WalletTransaction(String walletId,
                             TransactionType type,
                             BigDecimal amount,
                             String description) {
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.status = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void markSuccess() {
        this.status = TransactionStatus.SUCCESS;
    }

    public void markFailed() {
        this.status = TransactionStatus.FAILED;
    }
}