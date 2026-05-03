package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpgradeRequestStatusChangeServiceImpl implements UpgradeRequestStatusChangeService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public UpgradeRequestResponse submitUpgradeRequest(User user, UpgradeRequestSubmissionRequest dto) {
        // Quoted identifiers to match Hibernate's globally_quoted_identifiers=true
        String sqlCheck = "SELECT \"status\" FROM \"upgrade_request\" WHERE \"requester_user\" = ?";
        List<String> statuses = jdbcTemplate.query(sqlCheck, (rs, rowNum) -> rs.getString("status"), user.getId());
        
        for (String s : statuses) {
            if ("PENDING".equals(s)) {
                throw new RuntimeException("Pending request exists");
            }
        }
        
        jdbcTemplate.update("DELETE FROM \"upgrade_request\" WHERE \"requester_user\" = ?", user.getId());

        String requestId = UUID.randomUUID().toString();
        String sqlInsert = "INSERT INTO \"upgrade_request\" (\"upgr_req_id\", \"created_at\", \"credential\", \"full_name\", \"requester_user\", \"social_media_url\", \"status\") VALUES (?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsert,
            requestId, dto.getCredential(), dto.getFullName(), user.getId(), dto.getSocialMediaUrl(), "PENDING");

        user.setStatus(UserStatus.PENDING_JASTIPER);
        userRepository.save(user);
        
        return UpgradeRequestResponse.builder()
            .id(requestId)
            .requesterUserId(user.getId().toString())
            .requesterUsername(user.getUsername())
            .fullName(dto.getFullName())
            .credential(dto.getCredential())
            .socialMediaUrl(dto.getSocialMediaUrl())
            .status("PENDING")
            .build();
    }

    @Override
    @Transactional
    public void updateRequestStatus(UUID requestId, String status) {
        String selectSql = "SELECT \"requester_user\" FROM \"upgrade_request\" WHERE \"upgr_req_id\" = ?";
        List<String> userIds = jdbcTemplate.query(selectSql, (rs, rowNum) -> rs.getString(1), requestId.toString());
        
        if (userIds.isEmpty()) throw new RuntimeException("Not found");
        
        UUID requesterId = UUID.fromString(userIds.get(0));
        jdbcTemplate.update("UPDATE \"upgrade_request\" SET \"status\" = ? WHERE \"upgr_req_id\" = ?", status, requestId.toString());
        
        userRepository.findById(requesterId).ifPresent(u -> {
            u.setStatus(UserStatus.ACTIVE);
            if ("ACCEPTED".equalsIgnoreCase(status)) {
                userService.promoteToJastiper(u);
            }
            userRepository.save(u);
        });
    }
}