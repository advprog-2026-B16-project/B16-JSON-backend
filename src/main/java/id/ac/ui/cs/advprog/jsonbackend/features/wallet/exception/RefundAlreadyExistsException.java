package id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class RefundAlreadyExistsException extends WalletException {

    public RefundAlreadyExistsException(UUID transactionId) {
        super("Refund already exists for transaction: " + transactionId, "REFUND_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}
