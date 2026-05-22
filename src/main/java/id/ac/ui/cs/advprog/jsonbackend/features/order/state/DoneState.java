package id.ac.ui.cs.advprog.jsonbackend.features.order.state;

import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;

public class DoneState implements OrderState {

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.DONE;
    }

    @Override
    public void validateTransition(OrderStatus nextStatus) {
        throw new IllegalStateException("Done order cannot change state");
    }
}
