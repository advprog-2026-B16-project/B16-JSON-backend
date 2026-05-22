package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UpgradeRequestRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
}
