package id.ac.ui.cs.advprog.jsonbackend.order.model;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;
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
    @JdbcTypeCode(Types.VARCHAR)
    private UUID orderId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "titipers_id", nullable = false)
    @JdbcTypeCode(Types.VARCHAR)
    private UUID titipersId;

    @Column(name = "jastiper_id")
    @JdbcTypeCode(Types.VARCHAR)
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
        this.updatedAt = LocalDateTime.now();
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
        this.jastiperRating = jastiperRating;
        this.productRating = productRating;
        this.updatedAt = LocalDateTime.now();
    }
}