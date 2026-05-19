package id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception;

import org.springframework.http.HttpStatus;

public class InvalidWalletTransactionException extends WalletException {

    public InvalidWalletTransactionException(String message) {
        super(message, "INVALID_WALLET_TRANSACTION", HttpStatus.BAD_REQUEST);
    }
}
