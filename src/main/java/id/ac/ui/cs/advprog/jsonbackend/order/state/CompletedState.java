package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

public class CompletedState implements OrderState{

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.COMPLETED;
    }

    @Override
    public void validateTransition(OrderStatus nextStatus) {
        throw new IllegalStateException("Completed order cannot change state");
    }
}
