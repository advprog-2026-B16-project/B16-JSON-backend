package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;

import java.util.EnumMap;
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
    private static final Map<OrderStatus, OrderState> STATE_MAP = new EnumMap<>(OrderStatus.class);

    static {
        STATE_MAP.put(OrderStatus.PENDING,   new PendingState());
        STATE_MAP.put(OrderStatus.PAID,      new PaidState());
        STATE_MAP.put(OrderStatus.PURCHASED, new PurchasedState());
        STATE_MAP.put(OrderStatus.SHIPPED,   new ShippedState());
        STATE_MAP.put(OrderStatus.COMPLETED, new CompletedState());
        STATE_MAP.put(OrderStatus.CANCELLED, new CancelledState());
    }

    private OrderStateFactory(){
        throw new UnsupportedOperationException("Utility class");
    }

    public static OrderState getState(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Unknown order status: null");
        }
        OrderState state = STATE_MAP.get(status);
        if (state == null) {
            throw new IllegalArgumentException("Unknown order status: " + status);
        }
        return state;
    }

}
