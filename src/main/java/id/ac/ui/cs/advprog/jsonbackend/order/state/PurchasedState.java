package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

public class PurchasedState implements OrderState{

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PURCHASED;
    }

    @Override
    public void validateTransition(OrderStatus nextStatus) {

        if (nextStatus != OrderStatus.SHIPPED &&
                nextStatus != OrderStatus.CANCELLED) {

            throw new IllegalStateException(
                    "PURCHASED can only transition to SHIPPED or CANCELLED"
            );
        }
    }
}
