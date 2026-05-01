package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UpgradeRequestSubmissionRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
@Service @RequiredArgsConstructor
public class UpgradeRequestStatusChangeServiceImpl implements UpgradeRequestStatusChangeService {
    private final UpgradeRequestRepository upgradeRepo;
    private final UserService userService;
    @Override @Transactional public UpgradeRequestResponse submitUpgradeRequest(User user, UpgradeRequestSubmissionRequest dto) {
        upgradeRepo.findByRequesterUser(user).ifPresent(r -> { if ("PENDING".equals(r.getStatus())) throw new RuntimeException("Pending request exists"); });
        UpgradeRequest request = UpgradeRequest.builder().requesterUser(user).fullName(dto.getFullName()).credential(dto.getCredential()).status("PENDING").build();
        return UpgradeRequestResponse.fromRequest(upgradeRepo.save(request));
    }
    @Override @Transactional public void updateRequestStatus(UUID requestId, String status) {
        UpgradeRequest r = upgradeRepo.findById(requestId).orElseThrow(() -> new RuntimeException("Not found"));
        r.setStatus(status);
        if ("ACCEPTED".equalsIgnoreCase(status)) userService.promoteToJastiper(r.getRequesterUser());
        upgradeRepo.save(r);
    }
}