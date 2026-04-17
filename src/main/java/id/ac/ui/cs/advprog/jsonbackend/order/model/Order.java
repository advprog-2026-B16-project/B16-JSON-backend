package id.ac.ui.cs.advprog.jsonbackend.order.model;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.state.OrderState;
import id.ac.ui.cs.advprog.jsonbackend.order.state.OrderStateFactory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Order class represents an order placed by a user in the system.
 * It contains information about the product being ordered,
 * the quantity, the shipping address, and the status of the order.
 * Progress (25%) : Belum membuat database di supabase untuk order
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "titipers_id", nullable = false)
    private UUID titipersId;

    @Column(name = "jastiper_id")
    private UUID jastiperId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "jastiper_rating")
    private Integer jastiperRating;

    @Column(name = "product_rating")
    private Integer productRating;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    public Order(UUID orderId, String productId, UUID titipersId, UUID jastiperId,
                 int quantity, String shippingAddress) {
        this.orderId = orderId;
        this.productId = productId;
        this.titipersId = titipersId;
        this.jastiperId = jastiperId;
        this.quantity = quantity;
        this.shippingAddress = shippingAddress;
        this.orderStatus = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void updateStatus(OrderStatus nextStatus) {
        OrderState currentState = OrderStateFactory.getState(this.orderStatus);
        currentState.validateTransition(nextStatus);
        this.orderStatus = nextStatus;
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
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (orderId == null) {
            orderId = UUID.randomUUID();
        }
        if (orderStatus == null) {
            orderStatus = OrderStatus.PENDING;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
