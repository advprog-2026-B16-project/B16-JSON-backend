package id.ac.ui.cs.advprog.jsonbackend.features.payment.exception;

import org.springframework.http.HttpStatus;

public class PaymentNotAllowedException extends PaymentException {

    public PaymentNotAllowedException(String message) {
        super(message, "PAYMENT_NOT_ALLOWED", HttpStatus.BAD_REQUEST);
    }
}
