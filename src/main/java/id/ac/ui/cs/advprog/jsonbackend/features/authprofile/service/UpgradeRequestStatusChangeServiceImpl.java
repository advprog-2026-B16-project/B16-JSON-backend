package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UpgradeRequestStatusChangeServiceImpl implements UpgradeRequestStatusChangeService {

    private final UpgradeRequestRepository upgradeRepo;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;

    @Override
    @Transactional
    public UpgradeRequestResponse submitUpgradeRequest(User user, UpgradeRequestSubmissionRequest dto) {
        String checkSql = "SELECT count(*) FROM upgrade_request WHERE requester_user = ? AND status = 'PENDING'";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, user.getUsername());
        
        if (count != null && count > 0) {
            throw new RuntimeException("Pending request exists");
        }

        UUID requestId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        
        String insertSql = "INSERT INTO upgrade_request (upgr_req_id, created_at, credential, full_name, requester_user, status) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertSql, requestId, now, dto.getCredential(), dto.getFullName(), user.getUsername(), "PENDING");
        
        return UpgradeRequestResponse.builder()
                .id(requestId)
                .createdAt(now)
                .requesterUserId(user.getId().toString())
                .requesterUsername(user.getUsername())
                .fullName(dto.getFullName())
                .credential(dto.getCredential())
                .status("PENDING")
                .build();
    }

    @Override
    @Transactional
    public void updateRequestStatus(UUID requestId, String status) {
        String updateSql = "UPDATE upgrade_request SET status = ? WHERE upgr_req_id = ?";
        jdbcTemplate.update(updateSql, status, requestId);
        
        if ("ACCEPTED".equalsIgnoreCase(status)) {
            // Find user from request using native query
            String findUserSql = "SELECT requester_user FROM upgrade_request WHERE upgr_req_id = ?";
            String username = jdbcTemplate.queryForObject(findUserSql, String.class, requestId);
            userRepository.findByUsername(username).ifPresent(userService::promoteToJastiper);
        }
    }
}
