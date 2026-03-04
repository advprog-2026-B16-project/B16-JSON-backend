package id.ac.ui.cs.advprog.jsonbackend.order.model;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/*
 * Order class represents an order placed by a user in the system.
 * It contains information about the product being ordered,
 * the quantity, the shipping address, and the status of the order.
 * Progress (25%) : Belum membuat database di supabase untuk order
 */

@Getter
@Setter
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
    private Integer jastiperRating;
    private Integer productRating;
    private String cancellationReason;

    public Order(String orderId,
                 String productId,
                 String titipersId,
                 String jastiperId,
                 int quantity,
                 String shippingAddress) {

        this.orderId = orderId;
        this.productId = productId;
        this.titipersId = titipersId;
        this.jastiperId = jastiperId;
        this.quantity = quantity;
        this.shippingAddress = shippingAddress;

        this.orderStatus = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void updateStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        updateStatus(OrderStatus.CANCELLED);
        this.cancellationReason = reason;
    }

    public void submitRating(Integer jastiperRating, Integer productRating) {
        if (this.orderStatus != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Rating can only be submitted for COMPLETED orders");
        }
        this.jastiperRating = jastiperRating;
        this.productRating = productRating;
    }

}

