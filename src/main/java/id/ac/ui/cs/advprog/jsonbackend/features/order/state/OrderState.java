package id.ac.ui.cs.advprog.jsonbackend.features.order.state;

import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;

public interface OrderState {

    OrderStatus getStatus();

    void validateTransition(OrderStatus nextStatus);
}
