package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import lombok.Builder;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UpgradeRequestResponse (
    String id,
    OffsetDateTime createdAt,
    String requesterUserId,
    String requesterUsername,
    String fullName,
    String credential, String socialMediaUrl,
    String status
) {
    public static UpgradeRequestResponse fromRequest(UpgradeRequest r) {
        if (r == null) return null;
        
        User u = r.getRequesterUser();
        String userId = (u != null && u.getId() != null) ? u.getId().toString() : "unknown";
        String username = (u != null) ? u.getUsername() : "unknown";
        
        return UpgradeRequestResponse.builder()
                .id(r.getUpgrReqId())
                .createdAt(r.getCreatedAt())
                .requesterUserId(userId)
                .requesterUsername(username)
                .fullName(r.getFullName())
                .credential(r.getCredential()).socialMediaUrl(r.getSocialMediaUrl())
                .status(r.getStatus())
                .build();
    }
}
