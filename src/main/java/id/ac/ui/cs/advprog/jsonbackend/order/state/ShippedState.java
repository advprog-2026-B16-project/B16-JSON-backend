package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

public class ShippedState implements OrderState{

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.SHIPPED;
    }

    @Override
    public void validateTransition(OrderStatus nextStatus) {

        if (nextStatus != OrderStatus.COMPLETED) {

            throw new IllegalStateException(
                    "SHIPPED can only transition to COMPLETED"
            );
        }
    }
}
