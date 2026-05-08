package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestStatusChangeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestStatusChangeService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RestController
@RequestMapping("/api/upgrade-request")
@RequiredArgsConstructor
public class UpgradeRequestStatusChangeController {

    private final UpgradeRequestStatusChangeService upgradeRequestStatusChangeService;
    private final UserService userService;

    @Value("${app.debug.verbose:false}")
    private boolean verboseLogging;

    @PostMapping("/submit")
    @PreAuthorize("hasRole('TITIPER')")
    public ResponseEntity<?> submitRequest(
            @Valid @RequestBody UpgradeRequestSubmissionRequest requestDto,
            BindingResult result,
            Authentication authentication) {

        if (verboseLogging) {
            log.info("[DEBUG] POST /api/upgrade-request/submit | User: {} | Body: {}", 
                authentication.getName(), requestDto);
        }

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            if (verboseLogging) log.warn("[DEBUG] Validation Errors: {}", errors);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return ResponseEntity.ok(upgradeRequestStatusChangeService.submitUpgradeRequest(user, requestDto));
        } catch (Exception e) {
            if (verboseLogging) log.error("[DEBUG] Error in submitRequest: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/change-status/{requestId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID requestId,
            @Valid @RequestBody UpgradeRequestStatusChangeRequest dto,
            BindingResult result,
            Authentication authentication) {

        if (verboseLogging) {
            log.info("[DEBUG] PATCH /api/upgrade-request/change-status/{} | Admin: {} | Body: {}", 
                requestId, authentication.getName(), dto);
        }

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            if (verboseLogging) log.warn("[DEBUG] Validation Errors: {}", errors);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            this.upgradeRequestStatusChangeService.updateRequestStatus(requestId, dto.getNewStatus());
            if (verboseLogging) log.info("[DEBUG] Success: Status updated to {}", dto.getNewStatus());
            return ResponseEntity.ok("Status updated successfully");
        } catch (Exception e) {
            if (verboseLogging) log.error("[DEBUG] Error in updateStatus: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}
