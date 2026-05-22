package id.ac.ui.cs.advprog.jsonbackend.features.order.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.service.JastiperOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jastiper/orders")
@PreAuthorize("hasRole('JASTIPER')")
public class JastiperOrderController {

    private final JastiperOrderService jastiperOrderService;

    public JastiperOrderController(JastiperOrderService jastiperOrderService) {
        this.jastiperOrderService = jastiperOrderService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(jastiperOrderService.getMyOrders(authenticatedUser));
    }

    @PatchMapping("/{orderId}/shipped")
    public ResponseEntity<OrderResponse> markAsShipped(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(jastiperOrderService.markAsShipped(authenticatedUser, orderId));
    }

    @PatchMapping("/{orderId}/completed")
    public ResponseEntity<OrderResponse> markAsCompleted(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(jastiperOrderService.markAsCompleted(authenticatedUser, orderId));
    }
}
