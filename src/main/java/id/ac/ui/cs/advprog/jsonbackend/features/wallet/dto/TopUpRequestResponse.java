package id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TopUpRequestResponse(
        UUID transactionId,
        UUID userId,
        UUID walletId,
        BigDecimal amount,
        TransactionStatus status,
        LocalDateTime createdAt,
        String description
) {
    public TopUpRequestResponse(Transaction transaction) {
        this(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getWalletId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCreatedAt(),
                transaction.getDescription()
        );
    }
}
