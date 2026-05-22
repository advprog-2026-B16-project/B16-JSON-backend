package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestRetrievalService;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/upgrade-request")
@RequiredArgsConstructor
public class UpgradeRequestRetrievalController {

    private final UpgradeRequestRetrievalService retrievalService;
    private final UserService userService;

    @Value("${app.debug.verbose:false}")
    private boolean verboseLogging;

    @GetMapping("/get-all")
    public ResponseEntity<List<UpgradeRequestResponse>> getAllRequests() {
        if (verboseLogging) {
            log.info("[DEBUG] GET /api/upgrade-request/get-all | Fetching all requests");
        }

        List<UpgradeRequestResponse> responses = retrievalService.getAllRequests().stream()
                .map(UpgradeRequestResponse::fromRequest)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/me")
    public ResponseEntity<UpgradeRequestResponse> getMyRequest(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        return userService.getUserByUsername(authentication.getName())
                .flatMap(retrievalService::getRequestByUsername)
                .map(UpgradeRequestResponse::fromRequest)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
