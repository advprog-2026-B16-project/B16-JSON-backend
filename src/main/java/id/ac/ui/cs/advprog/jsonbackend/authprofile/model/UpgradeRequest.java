package id.ac.ui.cs.advprog.jsonbackend.authprofile.model;

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
    private UUID id;

    @Column(name = "created_at", updatable = false, nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private User requesterUser;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String credential;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";
}
