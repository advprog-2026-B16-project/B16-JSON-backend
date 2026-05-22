package id.ac.ui.cs.advprog.jsonbackend.features.order.state;

import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;

public class CompletedState implements OrderState{

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.COMPLETED;
    }

    @Override
    public void validateTransition(OrderStatus nextStatus) {
        if (nextStatus != OrderStatus.DONE) {
            throw new IllegalStateException("COMPLETED can only transition to DONE");
        }
    }
}
