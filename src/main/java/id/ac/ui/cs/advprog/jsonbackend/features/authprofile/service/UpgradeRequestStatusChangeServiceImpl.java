package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpgradeRequestStatusChangeServiceImpl implements UpgradeRequestStatusChangeService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${app.debug.verbose:false}")
    private boolean verboseLogging;

    @Override
    @Transactional
    public UpgradeRequestResponse submitUpgradeRequest(User user, UpgradeRequestSubmissionRequest requestDto) {
        if (verboseLogging) {
            log.debug("[DEBUG] Execution started: submitUpgradeRequest | User: {}", user.getUsername());
        }

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
            log.debug("[DEBUG] UpgradeRequest created: {}", savedRequest.getUpgrReqId());
        }

        return UpgradeRequestResponse.fromRequest(savedRequest);
    }

    @Override
    @Transactional
    public void updateRequestStatus(UUID requestId, String newStatus) {
        if (verboseLogging) {
            log.debug("[DEBUG] Execution started: handleUpgradeDecision | RequestID: {} | Status: {}", requestId, newStatus);
        }

        UpgradeRequest request = upgradeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Upgrade request not found"));

        request.setStatus(newStatus);

        if ("ACCEPTED".equalsIgnoreCase(newStatus)) {
            User user = request.getRequesterUser();
            userService.promoteToJastiper(user);
            if (verboseLogging) {
                log.debug("[DEBUG] User promoted to JASTIPER: {}", user.getUsername());
            }
        }

        upgradeRequestRepository.save(request);

        if (verboseLogging) {
            log.debug("[DEBUG] Execution finished: Status updated to {}", newStatus);
        }
    }
}
