package id.ac.ui.cs.advprog.jsonbackend.features.transaction.exception;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletException;
import org.springframework.http.HttpStatus;

public class TransactionNotFoundException extends WalletException {

    public TransactionNotFoundException(String transactionId) {
        super("Transaction not found for transaction: " + transactionId, "TRANSACTION_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
