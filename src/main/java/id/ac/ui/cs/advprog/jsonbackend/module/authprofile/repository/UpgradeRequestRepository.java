package id.ac.ui.cs.advprog.jsonbackend.module.authprofile.repository;

import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.module.authprofile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpgradeRequestRepository extends JpaRepository<UpgradeRequest, UUID> {
    Optional<UpgradeRequest> findByRequesterUser(User requesterUser);
}
