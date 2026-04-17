package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

public class PendingState implements OrderState{
    @Override
    public OrderStatus getStatus(){
        return OrderStatus.PENDING;
    }

    @Override
    public void validateTransition(OrderStatus nextStatus) {
        if (nextStatus != OrderStatus.PAID && nextStatus != OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                    "PENDING can only transition to PAID or CANCELLED"
            );
        }
    }
}
