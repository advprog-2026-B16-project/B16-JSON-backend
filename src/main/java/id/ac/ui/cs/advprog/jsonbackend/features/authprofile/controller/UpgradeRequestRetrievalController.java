package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RestController
@RequestMapping("/api/upgrade-request")
@RequiredArgsConstructor
public class UpgradeRequestRetrievalController {

    private final UpgradeRequestRetrievalService upgradeRequestRetrievalService;

    @Value("${app.debug.verbose:false}")
    private boolean verboseLogging;

    @GetMapping({"/get-all", "/get-requests"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UpgradeRequestResponse>> getAllRequests() {
        if (verboseLogging) {
            log.info("[DEBUG] GET /api/upgrade-request/get-all | Fetching all requests");
        }
        
        List<UpgradeRequestResponse> responses = upgradeRequestRetrievalService.getAllRequests().stream()
                .map(UpgradeRequestResponse::fromRequest)
                .collect(Collectors.toList());
        
        if (verboseLogging) {
            log.info("[DEBUG] Fetched {} upgrade requests", responses.size());
        }
        
        return ResponseEntity.ok(responses);
    }
}
