package id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;

import java.math.BigDecimal;

public class TransactionResponse {

    private String id;
    private TransactionType type;
    private BigDecimal amount;
    private TransactionStatus status;
    private String description;

    public TransactionResponse(
            String id,
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

    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public TransactionStatus getStatus() { return status; }
    public String getDescription() { return description; }
}