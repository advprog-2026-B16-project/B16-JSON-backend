package id.ac.ui.cs.advprog.jsonbackend.order.dto;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * OrderResponse adalah DTO untuk mengirim data pesanan dari server ke client.
 *
 * Cara Kerja:
 * Order (domain model) → mapToResponse() → OrderResponse → HTTP JSON Response ke client
 *
 * Contoh response body:
 * {
 *   "orderId": "uuid-123",
 *   "productId": "prod-456",
 *   "titipersId": "user-789",
 *   "jastiperId": "jastiper-001",
 *   "quantity": 2,
 *   "shippingAddress": "Jl. Margonda No.1",
 *   "orderStatus": "PENDING",
 *   "createdAt": "2024-01-01T10:00:00",
 *   "updatedAt": null,
 *   "jastiperRating": null,
 *   "productRating": null,
 *   "cancellationReason": null
 * }
 *
 * Digunakan sebagai return type di:
 * - OrderController (semua endpoint)
 * - OrderService (semua method)
 *
 * Menggunakan @Builder pattern dari Lombok untuk konstruksi objek yang lebih bersih.
 * Hanya mengekspos field yang perlu dilihat client, bukan seluruh domain model Order.
 */


@Getter
@Builder
public class OrderResponse {
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
}

