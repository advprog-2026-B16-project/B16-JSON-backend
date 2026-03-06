package id.ac.ui.cs.advprog.jsonbackend.authprofile.dto;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UpgradeRequest;
import lombok.Builder;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UpgradeRequestResponse (
    UUID id,
    OffsetDateTime createdAt,
    String requesterUserId,
    String requesterUsername,
    String fullName,
    String credential,
    String status
) {
    public static UpgradeRequestResponse fromRequest(UpgradeRequest request) {
        return UpgradeRequestResponse.builder()
                .id(request.getId())
                .createdAt(request.getCreatedAt())
                .requesterUserId(request.getRequesterUser().getUsername())
                .requesterUsername(request.getRequesterUser().getUsername())
                .fullName(request.getFullName())
                .credential(request.getCredential())
                .status(request.getStatus())
                .build();
    }
}
