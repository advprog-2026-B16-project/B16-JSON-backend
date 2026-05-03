package id.ac.ui.cs.advprog.jsonbackend.order.model;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"orders\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "titipers_id", nullable = false)
    private UUID titipersId;

    @Column(name = "jastiper_id")
    private UUID jastiperId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "jastiper_rating")
    private Integer jastiperRating;

    @Column(name = "product_rating")
    private Integer productRating;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    public Order(UUID orderId, String productId, UUID titipersId, UUID jastiperId, int quantity, String shippingAddress) {
        this.orderId = orderId;
        this.productId = productId;
        this.titipersId = titipersId;
        this.jastiperId = jastiperId;
        this.quantity = quantity;
        this.shippingAddress = shippingAddress;
        this.orderStatus = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    public void updateStatus(OrderStatus status) {
        this.orderStatus = status;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        this.orderStatus = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    public void submitRating(int jastiperRating, int productRating) {
        if (this.orderStatus != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Rating can only be submitted for COMPLETED orders");
        }
        this.jastiperRating = jastiperRating;
        this.productRating = productRating;
        this.updatedAt = LocalDateTime.now();
    }
}