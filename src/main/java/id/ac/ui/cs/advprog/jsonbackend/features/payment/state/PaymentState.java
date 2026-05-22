package id.ac.ui.cs.advprog.jsonbackend.features.payment.state;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;

public interface PaymentState {
    PaymentStatus getStatus();

    void validateTransition(PaymentStatus nextStatus);
}
