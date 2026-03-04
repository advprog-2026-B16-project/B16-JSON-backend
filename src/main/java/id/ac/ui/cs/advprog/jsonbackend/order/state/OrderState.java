package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

public interface OrderState {

    OrderStatus getStatus();

    void validateTransition(OrderStatus nextStatus);
}
