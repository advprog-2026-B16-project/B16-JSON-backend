package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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
        try {
            upgradeRepo.findByRequesterUser(user).ifPresent(r -> {
                if ("PENDING".equals(r.getStatus())) {
                    throw new RuntimeException("Pending request exists");
                }
                upgradeRepo.delete(r);
            });
        } catch (RuntimeException e) {
            if ("Pending request exists".equals(e.getMessage())) throw e;
            log.warn("[STABILITY] JPA pre-submit check failed. Falling back to JdbcTemplate.");
            jdbcTemplate.update("DELETE FROM \"upgrade_request\" WHERE \"requester_user\" = ?", user.getId());
        }

        UpgradeRequest request = UpgradeRequest.builder()
                .upgrReqId(UUID.randomUUID().toString())
                .requesterUser(user)
                .fullName(dto.getFullName())
                .credential(dto.getCredential())
                .socialMediaUrl(dto.getSocialMediaUrl())
                .status("PENDING")
                .build();

        user.setStatus(UserStatus.PENDING_JASTIPER);
        userRepository.save(user);
        
        try {
            return UpgradeRequestResponse.fromRequest(upgradeRepo.save(request));
        } catch (Exception e) {
            log.warn("[STABILITY] JPA save failed. Manual INSERT via JdbcTemplate.");
            jdbcTemplate.update("INSERT INTO \"upgrade_request\" (\"upgr_req_id\", \"created_at\", \"credential\", \"full_name\", \"requester_user\", \"social_media_url\", \"status\") VALUES (?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)",
                request.getUpgrReqId(), request.getCredential(), request.getFullName(), user.getId(), request.getSocialMediaUrl(), "PENDING");
                
            return UpgradeRequestResponse.builder()
                .id(request.getUpgrReqId())
                .requesterUserId(user.getId().toString())
                .requesterUsername(user.getUsername())
                .fullName(request.getFullName())
                .credential(request.getCredential())
                .socialMediaUrl(request.getSocialMediaUrl())
                .status("PENDING")
                .build();
        }
    }

    @Override
    @Transactional
    public void updateRequestStatus(UUID requestId, String status) {
        try {
            UpgradeRequest r = upgradeRepo.findById(requestId.toString()).orElseThrow(() -> new RuntimeException("Not found"));
            r.setStatus(status);
            User requester = r.getRequesterUser();
            updateUserRoleAndStatus(requester, status);
            upgradeRepo.save(r);
        } catch (Exception e) {
            if ("Not found".equals(e.getMessage())) throw e;
            log.warn("[STABILITY] JPA updateStatus failed. Falling back to manual SQL.");
            
            String selectSql = "SELECT \"requester_user\" FROM \"upgrade_request\" WHERE \"upgr_req_id\" = ?";
            List<String> userIds = jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString(1), requestId.toString());
            
            if (userIds.isEmpty()) throw new RuntimeException("Not found");
            
            UUID requesterId = UUID.fromString(userIds.get(0));
            jdbcTemplate.update("UPDATE \"upgrade_request\" SET \"status\" = ? WHERE \"upgr_req_id\" = ?", status, requestId.toString());
            userRepository.findById(requesterId).ifPresent(u -> updateUserRoleAndStatus(u, status));
        }
    }
    
    private void updateUserRoleAndStatus(User user, String status) {
        user.setStatus(UserStatus.ACTIVE);
        if ("ACCEPTED".equalsIgnoreCase(status)) {
            userService.promoteToJastiper(user);
        }
        userRepository.save(user);
    }
}