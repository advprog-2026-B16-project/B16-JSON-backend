package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UpgradeRequestRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/upgrade-request")
@RequiredArgsConstructor
public class UpgradeRequestRetrievalController {

    private final UpgradeRequestRetrievalService upgradeRequestRetrievalService;

    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UpgradeRequestResponse>> getAllRequests() {
        List<UpgradeRequestResponse> responses = upgradeRequestRetrievalService.getAllRequests().stream()
                .map(UpgradeRequestResponse::fromRequest)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
