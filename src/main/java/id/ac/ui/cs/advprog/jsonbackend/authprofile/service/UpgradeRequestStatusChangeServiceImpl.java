package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UpgradeRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpgradeRequestStatusChangeServiceImpl implements UpgradeRequestStatusChangeService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void updateRequestStatus(String requestId, String newStatus) {
        UpgradeRequest request = upgradeRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Upgrade request not found"));

        request.setStatus(newStatus);

        if ("ACCEPTED".equalsIgnoreCase(newStatus)) {
            userService.promoteToJastiper(request.getRequesterUser());
        }

        upgradeRequestRepository.save(request);
    }
}
