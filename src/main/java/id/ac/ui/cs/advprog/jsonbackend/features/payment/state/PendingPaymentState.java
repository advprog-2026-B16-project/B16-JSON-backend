package id.ac.ui.cs.advprog.jsonbackend.features.payment.state;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;

public class PendingPaymentState implements PaymentState {

    @Override
    public PaymentStatus getStatus() {
        return PaymentStatus.PENDING;
    }

    @Override
    public void validateTransition(PaymentStatus nextStatus) {
        if (nextStatus != PaymentStatus.SUCCESS
                && nextStatus != PaymentStatus.EXPIRED
                && nextStatus != PaymentStatus.FAILED
                && nextStatus != PaymentStatus.CANCELLED) {
            throw new IllegalStateException("PENDING payment can only transition to SUCCESS, EXPIRED, FAILED, or CANCELLED");
        }
    }
}
