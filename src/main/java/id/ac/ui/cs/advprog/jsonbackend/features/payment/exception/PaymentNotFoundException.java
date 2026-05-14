package id.ac.ui.cs.advprog.jsonbackend.features.payment.exception;

import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends PaymentException {

    public PaymentNotFoundException(String referenceCode) {
        super("Payment not found for reference: " + referenceCode, "PAYMENT_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
