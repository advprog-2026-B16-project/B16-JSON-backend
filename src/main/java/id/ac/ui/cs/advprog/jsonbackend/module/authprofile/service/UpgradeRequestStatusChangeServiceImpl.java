package id.ac.ui.cs.advprog.jsonbackend.module.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpgradeRequestStatusChangeServiceImpl implements UpgradeRequestStatusChangeService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void updateRequestStatus(String requestId, String newStatus) {
        UpgradeRequest request = upgradeRequestRepository.findById(UUID.fromString(requestId))
                .orElseThrow(() -> new RuntimeException("Upgrade request not found"));

        request.setStatus(newStatus);

        if ("ACCEPTED".equalsIgnoreCase(newStatus)) {
            User user = request.getRequesterUser();
            user.setRole(UserRole.JASTIPER);
            userRepository.save(user);
        }

        upgradeRequestRepository.save(request);
    }
}
