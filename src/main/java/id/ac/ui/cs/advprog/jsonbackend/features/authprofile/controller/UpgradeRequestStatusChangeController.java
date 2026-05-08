package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping("/api/upgrade-request") @RequiredArgsConstructor
public class UpgradeRequestStatusChangeController {
    private final UpgradeRequestStatusChangeService upgradeService;
    private final UserService userService;
    @PostMapping("/submit") public ResponseEntity<?> submit(@Valid @RequestBody UpgradeRequestSubmissionRequest dto, Authentication auth) { return ResponseEntity.ok(upgradeService.submitUpgradeRequest(userService.getUserByUsername(auth.getName()).get(), dto)); }
    @PatchMapping("/change-status/{requestId}") public ResponseEntity<?> update(@PathVariable UUID requestId, @Valid @RequestBody UpgradeRequestStatusChangeRequest dto) { upgradeService.updateRequestStatus(requestId, dto.getNewStatus()); return ResponseEntity.ok("Status updated successfully"); }
}