package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

import java.util.Map;

/**
 * OrdateStateFactory menyediakan instance OrderState yang sesuai berdasarkan OrderStatus yang diberikan.
 *
 * Cara Kerja :
 * OrderStatus.PAID  →  OrderStateFactory.getState()  →  PaidState instance
 * OrderStatus.SHIPPED  →  OrderStateFactory.getState()  →  ShippedState instance
 *
 * Alternatif dari switch-case:
 * OrderState state = OrderStateFactory.getState(order.getOrderStatus());
 *
 * Digunakan untuk update status
 * public void updateStatus(OrderStatus newStatus) {
 *     OrderState currentState = OrderStateFactory.getState(this.orderStatus); // ambil state saat ini
 *     currentState.validateTransition(newStatus); // validasi apakah boleh pindah ke status baru
 *     this.orderStatus = newStatus;
 * }
 *
 * Factory memastikan setiap perubahan status divalidasi sesuai aturan bisnis.
 */

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
