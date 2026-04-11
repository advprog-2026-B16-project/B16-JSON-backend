package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "upgrade_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpgradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "upgr_req_id")
    private UUID upgrReqId;

    @Column(name = "created_at", updatable = false, nullable = false)
    @Builder.Default
    private java.time.OffsetDateTime createdAt = java.time.OffsetDateTime.now();

    @OneToOne
    @JoinColumn(name = "requester_user", referencedColumnName = "username", nullable = false)
    private User requesterUser;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String credential;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";
}
