package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "upgrade_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpgradeRequest {

    @Id
    @Column(name = "upgr_req_id")
    private String upgrReqId;

    @Column(name = "created_at", updatable = false, nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_user", referencedColumnName = "id", nullable = false)
    private User requesterUser;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "credential", nullable = false)
    private String credential;

    @Column(name = "social_media_url")
    private String socialMediaUrl;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "PENDING";
}