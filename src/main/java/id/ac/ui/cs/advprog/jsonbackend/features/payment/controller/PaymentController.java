package id.ac.ui.cs.advprog.jsonbackend.features.payment.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.dto.PaymentRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.dto.PaymentResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody PaymentRequest request
    ) {
        return ResponseEntity.ok(new PaymentResponse(paymentService.createPayment(authenticatedUser, request)));
    }

    @PostMapping("/{referenceCode}/pay")
    public ResponseEntity<PaymentResponse> pay(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable String referenceCode
    ) {
        return ResponseEntity.ok(new PaymentResponse(paymentService.pay(authenticatedUser, referenceCode)));
    }

    @PatchMapping("/{referenceCode}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(
            @AuthenticationPrincipal User user,
            @PathVariable String referenceCode
    ) {
        return ResponseEntity.ok(new PaymentResponse(paymentService.cancelPayment(user, referenceCode)));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(@AuthenticationPrincipal User authenticatedUser) {
        List<PaymentResponse> response = paymentService.getMyPayments(authenticatedUser)
                .stream()
                .map(PaymentResponse::new)
                .toList();
        return ResponseEntity.ok(response);
    }
}
