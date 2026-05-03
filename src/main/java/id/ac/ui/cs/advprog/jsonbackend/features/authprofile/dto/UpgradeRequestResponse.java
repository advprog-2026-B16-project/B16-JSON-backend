package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import lombok.Builder;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UpgradeRequestResponse (
    UUID id,
    OffsetDateTime createdAt,
    UUID requesterUserId,
    String requesterUsername,
    String fullName,
    String credential,
    String socialMediaUrl,
    String status
) {
    public static UpgradeRequestResponse fromRequest(UpgradeRequest r) {
        User u = r.getRequesterUser();
        return UpgradeRequestResponse.builder()
                .id(r.getUpgrReqId())
                .createdAt(r.getCreatedAt())
                .requesterUserId(u != null ? u.getId() : null)
                .requesterUsername(u != null ? u.getUsername() : "unknown")
                .fullName(r.getFullName())
                .credential(r.getCredential())
                .socialMediaUrl(r.getSocialMediaUrl())
                .status(r.getStatus())
                .build();
    }
}