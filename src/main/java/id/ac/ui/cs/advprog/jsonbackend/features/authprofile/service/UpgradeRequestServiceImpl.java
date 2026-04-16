package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

/*
 * REDUNDANT: This file is marked as redundant and is kept for reference only.
 * Functional logic has been integrated into existing service layers.
 * 

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpgradeRequestServiceImpl implements UpgradeRequestService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final UserService userService;

    @Value("${app.debug.verbose:false}")
    private boolean verboseLogging;

    @Override
    @Transactional
    public UpgradeRequestResponse submitUpgradeRequest(User user, UpgradeRequestSubmissionRequest requestDto) {
        if (verboseLogging) {
            log.debug("[DEBUG] Execution started: submitUpgradeRequest | User: {}", user.getUsername());
        }

        // Check if user already has a PENDING request
        upgradeRequestRepository.findByRequesterUser(user).ifPresent(r -> {
            if ("PENDING".equals(r.getStatus())) {
                throw new RuntimeException("An upgrade request is already pending.");
            }
        });

        UpgradeRequest request = UpgradeRequest.builder()
                .requesterUser(user)
                .fullName(requestDto.getFullName())
                .credential(requestDto.getCredential())
                .status("PENDING")
                .build();

        UpgradeRequest savedRequest = upgradeRequestRepository.save(request);
        
        if (verboseLogging) {
            log.debug("[DEBUG] UpgradeRequest created: {}", savedRequest.getId());
        }

        return UpgradeRequestResponse.fromRequest(savedRequest);
    }

    @Override
    public List<UpgradeRequestResponse> getAllRequests() {
        return upgradeRequestRepository.findAll().stream()
                .map(UpgradeRequestResponse::fromRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UpgradeRequestResponse handleUpgradeDecision(UUID requestId, String newStatus) {
        if (verboseLogging) {
            log.debug("[DEBUG] Execution started: handleUpgradeDecision | RequestID: {} | Status: {}", requestId, newStatus);
        }

        UpgradeRequest request = upgradeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Upgrade request not found"));

        request.setStatus(newStatus);
        UpgradeRequest savedRequest = upgradeRequestRepository.save(request);

        if ("ACCEPTED".equalsIgnoreCase(newStatus)) {
            User user = request.getRequesterUser();
            userService.promoteToJastiper(user);
            if (verboseLogging) {
                log.debug("[DEBUG] User promoted to JASTIPER: {}", user.getUsername());
            }
        }

        if (verboseLogging) {
            log.debug("[DEBUG] Execution finished: Status updated to {}", newStatus);
        }

        return UpgradeRequestResponse.fromRequest(savedRequest);
    }
}
*/
