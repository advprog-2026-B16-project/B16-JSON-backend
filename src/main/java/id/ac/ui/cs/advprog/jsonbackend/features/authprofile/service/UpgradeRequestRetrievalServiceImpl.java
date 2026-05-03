package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpgradeRequestRetrievalServiceImpl implements UpgradeRequestRetrievalService {

    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<UpgradeRequest> getAllRequests() {
        // Quoted identifiers to match Hibernate's globally_quoted_identifiers=true
        String sql = "SELECT \"upgr_req_id\", \"created_at\", \"credential\", \"full_name\", \"requester_user\", \"social_media_url\", \"status\" FROM \"upgrade_request\"";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UpgradeRequest> getRequestByUsername(User user) {
        String sql = "SELECT \"upgr_req_id\", \"created_at\", \"credential\", \"full_name\", \"requester_user\", \"social_media_url\", \"status\" FROM \"upgrade_request\" WHERE \"requester_user\" = ?";
        List<UpgradeRequest> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            UpgradeRequest r = mapRow(rs);
            r.setRequesterUser(user);
            return r;
        }, user.getId());
        return results.stream().findFirst();
    }

    private UpgradeRequest mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        UpgradeRequest r = new UpgradeRequest();
        r.setUpgrReqId(rs.getString("upgr_req_id"));
        
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
        
        Object userIdObj = rs.getObject("requester_user");
        UUID userId = parseUuid(userIdObj);
        if (userId != null) {
            userRepository.findById(userId).ifPresent(r::setRequesterUser);
        }
        
        return r;
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