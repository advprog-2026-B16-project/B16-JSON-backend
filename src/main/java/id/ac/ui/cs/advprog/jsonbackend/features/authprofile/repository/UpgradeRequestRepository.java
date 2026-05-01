package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface UpgradeRequestRepository extends JpaRepository<UpgradeRequest, UUID> {
    Optional<UpgradeRequest> findByRequesterUser(User requesterUser);
}