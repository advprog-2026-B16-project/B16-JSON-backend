package id.ac.ui.cs.advprog.jsonbackend.order.controller;

import id.ac.ui.cs.advprog.jsonbackend.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class OrderController {

    @GetMapping
    public List<OrderResponse> getOrders() {
        return List.of(
                OrderResponse.builder()
                        .orderId("order-001")
                        .productId("prod-abc-123")
                        .titipersId("user-titipers-01")
                        .jastiperId("user-jastipers-01")
                        .quantity(2)
                        .shippingAddress("Jl. Margonda Raya No. 100, Depok")
                        .orderStatus(OrderStatus.PENDING)
                        .createdAt(LocalDateTime.of(2025, 6, 1, 10, 0))
                        .updatedAt(null)
                        .jastiperRating(null)
                        .productRating(null)
                        .cancellationReason(null)
                        .build(),

                OrderResponse.builder()
                        .orderId("order-002")
                        .productId("prod-xyz-456")
                        .titipersId("user-titipers-02")
                        .jastiperId("user-jastipers-01")
                        .quantity(1)
                        .shippingAddress("Jl. Kenanga No. 5, Jakarta")
                        .orderStatus(OrderStatus.SHIPPED)
                        .createdAt(LocalDateTime.of(2025, 5, 28, 14, 30))
                        .updatedAt(LocalDateTime.of(2025, 5, 29, 9, 0))
                        .jastiperRating(null)
                        .productRating(null)
                        .cancellationReason(null)
                        .build(),

                OrderResponse.builder()
                        .orderId("order-003")
                        .productId("prod-mno-789")
                        .titipersId("user-titipers-01")
                        .jastiperId("user-jastipers-02")
                        .quantity(3)
                        .shippingAddress("Jl. Sudirman No. 88, Jakarta")
                        .orderStatus(OrderStatus.COMPLETED)
                        .createdAt(LocalDateTime.of(2025, 5, 20, 8, 0))
                        .updatedAt(LocalDateTime.of(2025, 5, 25, 16, 0))
                        .jastiperRating(4)
                        .productRating(5)
                        .cancellationReason(null)
                        .build(),

                OrderResponse.builder()
                        .orderId("order-004")
                        .productId("prod-pqr-321")
                        .titipersId("user-titipers-03")
                        .jastiperId("user-jastipers-02")
                        .quantity(1)
                        .shippingAddress("Jl. Veteran No. 12, Bogor")
                        .orderStatus(OrderStatus.CANCELLED)
                        .createdAt(LocalDateTime.of(2025, 6, 2, 11, 0))
                        .updatedAt(LocalDateTime.of(2025, 6, 2, 12, 0))
                        .jastiperRating(null)
                        .productRating(null)
                        .cancellationReason("Item out of stock")
                        .build()
        );
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable String id) {
        return OrderResponse.builder()
                .orderId(id)
                .productId("prod-abc-123")
                .titipersId("user-titipers-01")
                .jastiperId("user-jastipers-01")
                .quantity(2)
                .shippingAddress("Jl. Margonda Raya No. 100, Depok")
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.of(2025, 6, 1, 10, 0))
                .updatedAt(null)
                .jastiperRating(null)
                .productRating(null)
                .cancellationReason(null)
                .build();
    }
}