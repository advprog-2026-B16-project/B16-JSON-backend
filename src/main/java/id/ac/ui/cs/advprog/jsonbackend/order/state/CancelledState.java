package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

public class CancelledState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CANCELLED;
    }

    @Override
    public void validateTransition(OrderStatus nextStatus) {
        throw new IllegalStateException("Cancelled order cannot change state");
    }
}
