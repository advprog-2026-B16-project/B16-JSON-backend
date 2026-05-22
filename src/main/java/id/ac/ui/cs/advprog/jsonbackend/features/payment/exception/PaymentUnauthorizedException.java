package id.ac.ui.cs.advprog.jsonbackend.features.payment.exception;

import org.springframework.http.HttpStatus;

public class PaymentUnauthorizedException extends PaymentException {

    public PaymentUnauthorizedException(String message) {
        super(message, "PAYMENT_UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
