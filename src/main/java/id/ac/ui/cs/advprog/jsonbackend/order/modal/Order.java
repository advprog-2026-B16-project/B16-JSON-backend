package id.ac.ui.cs.advprog.jsonbackend.order.modal;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/*
 * Order class represents an order placed by a user in the system.
 * It contains information about the product being ordered, the quantity, the shipping address, and the status of the order.
 * Progress (25%) : Belum membuat database di supabase untuk order
 */

@Getter @Setter
public class Order {
    private String orderId;
    private String productId;
    private String titipersId;
    private String jastiperId;
    private int quantity;
    private String shippingAddress;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.orderStatus = OrderStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Order (final String orderId, final String productId, final String titipersId, final String jastiperId, final int quantity, final String shippingAddress, final OrderStatus orderStatus) {
        this.orderId = orderId;
        this.productId = productId;
        this.titipersId = titipersId;
        this.jastiperId = jastiperId;
        this.quantity = quantity;
        this.shippingAddress = shippingAddress;
    }
}
