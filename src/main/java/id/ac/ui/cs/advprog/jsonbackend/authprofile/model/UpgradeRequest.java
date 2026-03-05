package id.ac.ui.cs.advprog.jsonbackend.authprofile.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID; // Import UUID

@Entity
@Table(name = "upgrade_request")
@Getter
@Setter
public class UpgradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Hibernate handles UUID generation
    @Column(name = "upgr_req_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToOne
    @JoinColumn(name = "requester_user", referencedColumnName = "username")
    private User requesterUser;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "credential")
    private String credential;

    @Column(name = "status")
    private String status;
}