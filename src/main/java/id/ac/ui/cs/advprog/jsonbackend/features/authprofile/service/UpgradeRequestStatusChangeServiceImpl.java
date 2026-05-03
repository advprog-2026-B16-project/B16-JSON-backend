package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpgradeRequestStatusChangeServiceImpl implements UpgradeRequestStatusChangeService {

    private final UpgradeRequestRepository upgradeRepo;
    private final UserService userService;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public UpgradeRequestResponse submitUpgradeRequest(User user, UpgradeRequestSubmissionRequest dto) {
        String sql = "SELECT \"status\" FROM \"upgrade_request\" WHERE \"requester_user\" = ?";
        List<String> statuses = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("status"), user.getId());
        
        for (String s : statuses) {
            if ("PENDING".equals(s)) {
                throw new RuntimeException("Pending request exists");
            }
        }
        
        jdbcTemplate.update("DELETE FROM \"upgrade_request\" WHERE \"requester_user\" = ?", user.getId());

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
        userRepository.save(requester);

        if ("ACCEPTED".equalsIgnoreCase(status)) {
            userService.promoteToJastiper(requester);
        }
        upgradeRepo.save(r);
    }
}