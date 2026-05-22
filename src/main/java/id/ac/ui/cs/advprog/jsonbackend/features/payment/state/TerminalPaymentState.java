package id.ac.ui.cs.advprog.jsonbackend.features.payment.state;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;

public class TerminalPaymentState implements PaymentState {

    private final PaymentStatus status;

    public TerminalPaymentState(PaymentStatus status) {
        this.status = status;
    }

    @Override
    public PaymentStatus getStatus() {
        return status;
    }

    @Override
    public void validateTransition(PaymentStatus nextStatus) {
        throw new IllegalStateException(status + " payment cannot transition to another status");
    }
}
