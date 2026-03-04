package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

public class PaidState implements OrderState{

    @Override
    public OrderStatus getStatus(){
        return OrderStatus.PAID;
    }

    @Override
    public void validateTransition(OrderStatus nextStatus) {

        if (nextStatus != OrderStatus.PURCHASED &&
                nextStatus != OrderStatus.CANCELLED) {

            throw new IllegalStateException(
                    "PAID can only transition to PURCHASED or CANCELLED"
            );
        }
    }
}
