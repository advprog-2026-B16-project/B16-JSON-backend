package id.ac.ui.cs.advprog.jsonbackend.features.order.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.service.TitiperOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/titiper/orders")
@PreAuthorize("hasRole('TITIPER')")
public class TitiperOrderController {

    private final TitiperOrderService titiperOrderService;

    public TitiperOrderController(TitiperOrderService titiperOrderService) {
        this.titiperOrderService = titiperOrderService;
    }

    @PatchMapping("/{orderId}/done")
    public ResponseEntity<OrderResponse> confirmDone(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(titiperOrderService.confirmDone(authenticatedUser, orderId));
    }
}
