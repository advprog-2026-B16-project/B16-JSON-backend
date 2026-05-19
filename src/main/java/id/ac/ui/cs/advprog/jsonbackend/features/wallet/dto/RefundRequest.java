package id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class RefundRequest {

    @NotNull
    private UUID transactionId;

    @Size(max = 500)
    private String reason;

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
