package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.TopUpRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.WalletRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletUnauthorizedException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletTransactionService walletTransactionService;

    public WalletController(
            WalletService walletService,
            WalletTransactionService walletTransactionService
    ) {
        this.walletService = walletService;
        this.walletTransactionService = walletTransactionService;
    }

    @PostMapping("/topup/request")
    public ResponseEntity<Transaction> requestTopUp(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody WalletRequest request
    ) {
        User user = requireAuthenticatedUser(authenticatedUser);
        Transaction trx = walletTransactionService.requestTopUp(user.getId().toString(), request.getAmount());

        return ResponseEntity.ok(trx);
    }

    @PostMapping("/topup/confirm/{transactionId}")
    public ResponseEntity<String> confirmTopUp(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable String transactionId
    ) {
        requireAdmin(authenticatedUser);
        walletTransactionService.confirmTopUp(transactionId);
        return ResponseEntity.ok("Top up confirmed");
    }

    @PostMapping("/topup/reject/{transactionId}")
    public ResponseEntity<String> rejectTopUp(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable String transactionId
    ) {
        requireAdmin(authenticatedUser);
        walletTransactionService.rejectTopUp(transactionId);

        return ResponseEntity.ok("Top up rejected");
    }

    @GetMapping("/topup/requests")
    public ResponseEntity<List<TopUpRequestResponse>> getPendingTopUpRequests(
            @AuthenticationPrincipal User authenticatedUser
    ) {
        requireAdmin(authenticatedUser);
        return ResponseEntity.ok(walletTransactionService.getPendingTopUpRequestResponses());
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody WalletRequest request
    ) {
        User user = requireAuthenticatedUser(authenticatedUser);
        walletTransactionService.requestWithdraw(user.getId().toString(), request.getAmount());
        return ResponseEntity.ok("Withdraw success");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyBalance(@AuthenticationPrincipal User authenticatedUser) {
        User user = requireAuthenticatedUser(authenticatedUser);
        return buildBalanceResponse(user.getId().toString());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getBalance(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable String userId
    ) {
        User user = requireAuthenticatedUser(authenticatedUser);
        if (!user.getId().toString().equals(userId)) {
            throw new WalletUnauthorizedException("You can only view your own wallet balance");
        }
        return buildBalanceResponse(userId);
    }

    private User requireAuthenticatedUser(User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new WalletUnauthorizedException("Authentication is required for wallet operations");
        }
        return authenticatedUser;
    }

    private void requireAdmin(User authenticatedUser) {
        User user = requireAuthenticatedUser(authenticatedUser);
        if (user.getRole() != UserRole.ADMIN) {
            throw new WalletUnauthorizedException("Admin role is required for this wallet operation");
        }
    }

    private ResponseEntity<?> buildBalanceResponse(String userId) {
        BigDecimal balance = walletService.getBalance(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "balance", balance
            )
        );
    }
}
