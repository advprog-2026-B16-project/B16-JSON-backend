package id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception;

import org.springframework.http.HttpStatus;

public class RefundNotAllowedException extends WalletException {

    public RefundNotAllowedException(String message) {
        super(message, "REFUND_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
    }
}
