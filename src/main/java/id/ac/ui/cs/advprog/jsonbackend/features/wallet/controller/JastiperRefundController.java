package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.RefundResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.JastiperRefundService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/jastiper/refunds")
@PreAuthorize("hasRole('JASTIPER')")
public class JastiperRefundController {

    private final JastiperRefundService jastiperRefundService;

    public JastiperRefundController(JastiperRefundService jastiperRefundService) {
        this.jastiperRefundService = jastiperRefundService;
    }

    @PatchMapping("/{refundId}/approve")
    public ResponseEntity<RefundResponse> approveRefund(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable UUID refundId
    ) {
        return ResponseEntity.ok(new RefundResponse(jastiperRefundService.approveRefund(authenticatedUser, refundId)));
    }
}
