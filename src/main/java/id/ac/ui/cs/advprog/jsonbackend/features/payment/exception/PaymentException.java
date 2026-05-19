package id.ac.ui.cs.advprog.jsonbackend.features.payment.exception;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletException;
import org.springframework.http.HttpStatus;

public class PaymentException extends WalletException {

    public PaymentException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
