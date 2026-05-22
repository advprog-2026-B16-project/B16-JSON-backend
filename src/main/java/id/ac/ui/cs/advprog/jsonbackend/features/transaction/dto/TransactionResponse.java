package id.ac.ui.cs.advprog.jsonbackend.features.transaction.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionResponse {

    private UUID id;
    private TransactionType type;
    private BigDecimal amount;
    private TransactionStatus status;
    private String description;

    public TransactionResponse(
            UUID id,
            TransactionType type,
            BigDecimal amount,
            TransactionStatus status,
            String description
    ) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.description = description;
    }

    public UUID getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public TransactionStatus getStatus() { return status; }
    public String getDescription() { return description; }
}