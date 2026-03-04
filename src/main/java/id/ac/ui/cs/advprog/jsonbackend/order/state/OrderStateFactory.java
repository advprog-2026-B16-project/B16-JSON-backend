package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

import java.util.Map;

public class OrderStateFactory {
    private static final Map<OrderStatus, OrderState> STATE_MAP = Map.of(
            OrderStatus.PENDING, new PendingState(),
            OrderStatus.PAID, new PaidState(),
            OrderStatus.PURCHASED, new PurchasedState(),
            OrderStatus.SHIPPED, new ShippedState(),
            OrderStatus.COMPLETED, new CompletedState(),
            OrderStatus.CANCELLED, new CancelledState()
    );

    private OrderStateFactory(){
        throw new UnsupportedOperationException("Utility class");
    }

    public static OrderState getState(OrderStatus status) {
        OrderState state = STATE_MAP.get(status);
        if (state == null) {
            throw new IllegalArgumentException("Unknown order status: " + status);
        }
        return state;
    }

}
