package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UpgradeRequestRetrievalServiceImpl implements UpgradeRequestRetrievalService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<UpgradeRequest> getAllRequests() {
        // Native query to avoid ClassCastException from Hibernate's proxy or type mapping
        String sql = "SELECT upgr_req_id, created_at, credential, full_name, requester_user, status FROM upgrade_request";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String requesterUsername = rs.getString("requester_user");
            User user = userRepository.findByUsername(requesterUsername).orElse(null);
            
            return UpgradeRequest.builder()
                    .upgrReqId(UUID.fromString(rs.getString("upgr_req_id")))
                    .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                    .credential(rs.getString("credential"))
                    .fullName(rs.getString("full_name"))
                    .requesterUser(user)
                    .status(rs.getString("status"))
                    .build();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UpgradeRequest> getRequestByUsername(User user) {
        return upgradeRequestRepository.findByRequesterUser(user);
    }
}
