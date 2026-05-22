package id.ac.ui.cs.advprog.jsonbackend.features.payment.state;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;

import java.util.EnumMap;
import java.util.Map;

public class PaymentStateFactory {

    private static final Map<PaymentStatus, PaymentState> STATE_MAP = new EnumMap<>(PaymentStatus.class);

    static {
        STATE_MAP.put(PaymentStatus.PENDING, new PendingPaymentState());
        STATE_MAP.put(PaymentStatus.SUCCESS, new TerminalPaymentState(PaymentStatus.SUCCESS));
        STATE_MAP.put(PaymentStatus.EXPIRED, new TerminalPaymentState(PaymentStatus.EXPIRED));
        STATE_MAP.put(PaymentStatus.FAILED, new TerminalPaymentState(PaymentStatus.FAILED));
        STATE_MAP.put(PaymentStatus.CANCELLED, new TerminalPaymentState(PaymentStatus.CANCELLED));
    }

    private PaymentStateFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static PaymentState getState(PaymentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Unknown payment status: null");
        }
        PaymentState state = STATE_MAP.get(status);
        if (state == null) {
            throw new IllegalArgumentException("Unknown payment status: " + status);
        }
        return state;
    }
}
