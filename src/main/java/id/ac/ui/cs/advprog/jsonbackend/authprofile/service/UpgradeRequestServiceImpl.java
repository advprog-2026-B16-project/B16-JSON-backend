package id.ac.ui.cs.advprog.jsonbackend.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.config.SanitizationService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UpgradeRequestRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.repository.UpgradeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpgradeRequestServiceImpl implements UpgradeRequestService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final SanitizationService sanitizationService;

    @Override
    @Transactional
    public void createRequest(User user, UpgradeRequestRegistrationRequest dto) {
        if (upgradeRequestRepository.findByRequesterUser(user).isPresent()) {
            throw new RuntimeException("Upgrade request already exists for this user");
        }

        UpgradeRequest request = UpgradeRequest.builder()
                .requesterUser(user)
                .fullName(sanitizationService.sanitize(dto.getFullName()))
                .credential(sanitizationService.sanitize(dto.getCredential()))
                .status("PENDING")
                .build();

        upgradeRequestRepository.save(request);
    }
}
