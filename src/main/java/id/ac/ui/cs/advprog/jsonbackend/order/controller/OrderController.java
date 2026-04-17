package id.ac.ui.cs.advprog.jsonbackend.order.controller;

import id.ac.ui.cs.advprog.jsonbackend.order.dto.CreateOrderRequest;
import id.ac.ui.cs.advprog.jsonbackend.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.order.dto.RatingRequest;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestBody CreateOrderRequest request){
        return ResponseEntity.ok(orderService.checkout(request));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String orderId, @RequestParam Map<String, String> payload) throws Throwable {
        String reason = payload.getOrDefault("cancellationReason", "No reason provided");
        return ResponseEntity.ok(orderService.cancelOrder(orderId, reason));
    }

    @PostMapping("/{orderId}/rating")
    public ResponseEntity<OrderResponse> submitRating(
            @PathVariable UUID orderId,
            @RequestBody RatingRequest request) {

        return ResponseEntity.ok(orderService.submitRating(orderId, request));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrder());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/titipers/{titipersId}")
    public ResponseEntity<List<Order>> getOrdersByTitipersId(@PathVariable String titipersId) {
        return ResponseEntity.ok(orderService.getOrderByTitipersId(titipersId));
    }

    @GetMapping("/jastiper/{jastiperId}")
    public ResponseEntity<List<Order>> getOrdersByJastiperId(@PathVariable String jastiperId) {
        return ResponseEntity.ok(orderService.getOrderByJastiperId(jastiperId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrderByStatus(status));
    }

    @GetMapping("/{orderId}/status/{status}")
    public ResponseEntity<Order> getOrderByOrderIdAndStatus(
            @PathVariable UUID orderId,
            @PathVariable OrderStatus status) {

        return orderService.getOrderByOrderIdAndStatus(orderId, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tambahkan ini di dalam OrderController.java
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status) {

        OrderResponse updatedOrder = orderService.updateOrderStatus(UUID.fromString(orderId), status);
        return ResponseEntity.ok(updatedOrder);
    }
}