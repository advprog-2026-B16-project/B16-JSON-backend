package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.*;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UpgradeRequestRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpgradeRequestRetrievalServiceImpl implements UpgradeRequestRetrievalService {

    private final UpgradeRequestRepository upgradeRequestRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<UpgradeRequest> getAllRequests() {
        String sql = "SELECT \"upgr_req_id\", \"created_at\", \"credential\", \"full_name\", \"requester_user\", \"social_media_url\", \"status\" FROM \"upgrade_request\"";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            UpgradeRequest r = new UpgradeRequest();
            r.setUpgrReqId(getUuid(rs.getObject("upgr_req_id")));
            
            Object createdAt = rs.getObject("created_at");
            if (createdAt instanceof Timestamp ts) {
                r.setCreatedAt(ts.toInstant().atOffset(ZoneOffset.UTC));
            } else if (createdAt instanceof OffsetDateTime odt) {
                r.setCreatedAt(odt);
            }
            
            r.setCredential(rs.getString("credential"));
            r.setFullName(rs.getString("full_name"));
            r.setSocialMediaUrl(rs.getString("social_media_url"));
            r.setStatus(rs.getString("status"));
            
            UUID userId = getUuid(rs.getObject("requester_user"));
            if (userId != null) {
                userRepository.findById(userId).ifPresent(r::setRequesterUser);
            }
            
            return r;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UpgradeRequest> getRequestByUsername(User user) {
        try {
            return upgradeRequestRepository.findByRequesterUser(user);
        } catch (Exception e) {
            String sql = "SELECT \"upgr_req_id\", \"created_at\", \"credential\", \"full_name\", \"requester_user\", \"social_media_url\", \"status\" FROM \"upgrade_request\" WHERE \"requester_user\" = ?";
            List<UpgradeRequest> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
                UpgradeRequest r = new UpgradeRequest();
                r.setUpgrReqId(getUuid(rs.getObject("upgr_req_id")));
                r.setCredential(rs.getString("credential"));
                r.setFullName(rs.getString("full_name"));
                r.setSocialMediaUrl(rs.getString("social_media_url"));
                r.setStatus(rs.getString("status"));
                r.setRequesterUser(user);
                return r;
            }, user.getId());
            return results.stream().findFirst();
        }
    }

    private UUID getUuid(Object obj) {
        if (obj == null) return null;
        if (obj instanceof UUID uuid) return uuid;
        if (obj instanceof String s) {
            try {
                return UUID.fromString(s);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}