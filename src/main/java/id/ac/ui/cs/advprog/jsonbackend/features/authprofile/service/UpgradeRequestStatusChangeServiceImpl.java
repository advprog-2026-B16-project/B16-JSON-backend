package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpgradeRequestStatusChangeServiceImpl implements UpgradeRequestStatusChangeService {

    private final UpgradeRequestRepository upgradeRepo;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UpgradeRequestResponse submitUpgradeRequest(User user, UpgradeRequestSubmissionRequest dto) {
        upgradeRepo.findByRequesterUser(user).ifPresent(r -> {
            if ("PENDING".equals(r.getStatus())) {
                throw new RuntimeException("Pending request exists");
            }
            upgradeRepo.delete(r);
        });

        UpgradeRequest request = UpgradeRequest.builder()
                .requesterUser(user)
                .fullName(dto.getFullName())
                .credential(dto.getCredential())
                .socialMediaUrl(dto.getSocialMediaUrl())
                .status("PENDING")
                .build();

        user.setStatus(UserStatus.PENDING_JASTIPER);
        userRepository.save(user);
        return UpgradeRequestResponse.fromRequest(upgradeRepo.save(request));
    }

    @Override
    @Transactional
    public void updateRequestStatus(UUID requestId, String status) {
        UpgradeRequest r = upgradeRepo.findById(requestId).orElseThrow(() -> new RuntimeException("Not found"));
        r.setStatus(status);

        User requester = r.getRequesterUser();
        requester.setStatus(UserStatus.ACTIVE);
        if ("ACCEPTED".equalsIgnoreCase(status)) {
            userService.promoteToJastiper(requester);
        }
        userRepository.save(requester);
        upgradeRepo.save(r);
    }
}