package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.RefundRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.RefundResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping
    public ResponseEntity<RefundResponse> requestRefund(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody RefundRequest request
    ) {
        return ResponseEntity.ok(new RefundResponse(refundService.requestRefund(authenticatedUser, request)));
    }

    @GetMapping("/me")
    public ResponseEntity<List<RefundResponse>> getMyRefunds(@AuthenticationPrincipal User authenticatedUser) {
        List<RefundResponse> response = refundService.getMyRefunds(authenticatedUser)
                .stream()
                .map(RefundResponse::new)
                .toList();
        return ResponseEntity.ok(response);
    }
}
