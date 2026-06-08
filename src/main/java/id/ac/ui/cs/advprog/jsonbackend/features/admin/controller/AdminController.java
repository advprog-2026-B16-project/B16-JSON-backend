package id.ac.ui.cs.advprog.jsonbackend.features.admin.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestRetrievalService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestStatusChangeService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.TopUpRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final UpgradeRequestRetrievalService upgradeRequestRetrievalService;
    private final UpgradeRequestStatusChangeService upgradeRequestStatusChangeService;
    private final WalletTransactionService walletTransactionService;

    @GetMapping("/users")
    public ResponseEntity<List<UserLoginResponse>> getUsers() {
        List<UserLoginResponse> response = userService.getAllUsers()
                .stream()
                .map(user -> UserLoginResponse.fromUser(user, null))
                .toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable UUID userId) {
        userService.banUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable UUID userId) {
        userService.unbanUser(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{userId}/demote")
    public ResponseEntity<Void> demoteUser(@PathVariable UUID userId) {
        userService.demoteUser(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/topups")
    public ResponseEntity<List<TopUpRequestResponse>> getPendingTopUps() {
        return ResponseEntity.ok(walletTransactionService.getPendingTopUpRequestResponses());
    }

    @PostMapping("/topups/{transactionId}/confirm")
    public ResponseEntity<Void> confirmTopUp(@PathVariable String transactionId) {
        walletTransactionService.confirmTopUp(transactionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/upgrade-requests")
    public ResponseEntity<List<UpgradeRequestResponse>> getUpgradeRequests() {
        List<UpgradeRequestResponse> response = upgradeRequestRetrievalService.getAllRequests()
                .stream()
                .map(UpgradeRequestResponse::fromRequest)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/upgrade-requests/{requestId}/accept")
    public ResponseEntity<Void> acceptUpgradeRequest(@PathVariable UUID requestId) {
        upgradeRequestStatusChangeService.updateRequestStatus(requestId, "ACCEPTED");
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/upgrade-requests/{requestId}/reject")
    public ResponseEntity<Void> rejectUpgradeRequest(@PathVariable UUID requestId) {
        upgradeRequestStatusChangeService.updateRequestStatus(requestId, "REJECTED");
        return ResponseEntity.ok().build();
    }
}
