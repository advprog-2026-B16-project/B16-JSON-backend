package id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends WalletException {

    public InsufficientBalanceException() {
        super("Insufficient balance", "INSUFFICIENT_BALANCE", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
