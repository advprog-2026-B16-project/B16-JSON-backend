package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

/*
 * REDUNDANT: This file is marked as redundant and is kept for reference only.
 * Functional logic has been integrated into existing controller layers.
 * 

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestStatusChangeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/upgrade-request-v2")
@RequiredArgsConstructor
public class UpgradeRequestController {

    private final UpgradeRequestService upgradeRequestService;
    private final UserService userService;

    @PostMapping("/submit")
    @PreAuthorize("hasRole('TITIPER')")
    public ResponseEntity<UpgradeRequestResponse> submitRequest(
            @Valid @RequestBody UpgradeRequestSubmissionRequest requestDto,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(upgradeRequestService.submitUpgradeRequest(user, requestDto));
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UpgradeRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(upgradeRequestService.getAllRequests());
    }

    @PatchMapping("/handle/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UpgradeRequestResponse> handleDecision(
            @PathVariable UUID requestId,
            @Valid @RequestBody UpgradeRequestStatusChangeRequest dto) {
        
        return ResponseEntity.ok(upgradeRequestService.handleUpgradeDecision(requestId, dto.getNewStatus()));
    }
}
*/
