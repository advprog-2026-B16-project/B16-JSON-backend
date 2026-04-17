package id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception;

import org.springframework.http.HttpStatus;

public class InvalidAmountException extends WalletException {

    public InvalidAmountException() {
        super("Amount must be greater than zero", "INVALID_AMOUNT", HttpStatus.BAD_REQUEST);
    }
}
