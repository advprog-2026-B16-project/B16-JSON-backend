package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpgradeRequestRetrievalServiceImpl implements UpgradeRequestRetrievalService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<UpgradeRequest> getAllRequests() {
        try {
            // Try standard JPA first, as it is cleaner
            return upgradeRequestRepository.findAll();
        } catch (Exception e) {
            log.warn("[STABILITY] JPA findAll failed with: {}. Falling back to robust JdbcTemplate retrieval.", e.getMessage());
            String sql = "SELECT * FROM \"upgrade_request\"";
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UpgradeRequest> getRequestByUsername(User user) {
        try {
            return upgradeRequestRepository.findByRequesterUser(user);
        } catch (Exception e) {
            log.warn("[STABILITY] JPA findByRequesterUser failed. Falling back to JdbcTemplate.");
            String sql = "SELECT * FROM \"upgrade_request\" WHERE \"requester_user\" = ?";
            List<UpgradeRequest> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                UpgradeRequest r = mapRow(rs);
                r.setRequesterUser(user);
                return r;
            }, user.getId());
            return results.stream().findFirst();
        }
    }

    private UpgradeRequest mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        UpgradeRequest r = new UpgradeRequest();
        
        // Manual column name mapping to be robust against case sensitivity and aliases
        r.setUpgrReqId(getUuidString(rs.getObject(1))); // upgr_req_id is usually 1st
        
        Object createdAt = rs.getObject("created_at");
        if (createdAt instanceof Timestamp ts) {
            r.setCreatedAt(ts.toInstant().atOffset(ZoneOffset.UTC));
        } else if (createdAt instanceof OffsetDateTime odt) {
            r.setCreatedAt(odt);
        } else {
            r.setCreatedAt(OffsetDateTime.now());
        }
        
        r.setCredential(rs.getString("credential"));
        r.setFullName(rs.getString("full_name"));
        r.setSocialMediaUrl(rs.getString("social_media_url"));
        r.setStatus(rs.getString("status"));
        
        // Handle User association manually
        Object userIdObj = rs.getObject("requester_user");
        UUID userId = parseUuid(userIdObj);
        if (userId != null) {
            userRepository.findById(userId).ifPresent(r::setRequesterUser);
        }
        
        return r;
    }

    private String getUuidString(Object obj) {
        if (obj == null) return null;
        return obj.toString();
    }

    private UUID parseUuid(Object obj) {
        if (obj == null) return null;
        if (obj instanceof UUID uuid) return uuid;
        try {
            return UUID.fromString(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }
}