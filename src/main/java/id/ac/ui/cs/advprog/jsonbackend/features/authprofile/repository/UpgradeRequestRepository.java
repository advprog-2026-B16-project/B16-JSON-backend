package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpgradeRequestRepository extends JpaRepository<UpgradeRequest, UUID> {
    
    @Query("SELECT r FROM UpgradeRequest r WHERE r.requesterUser.username = :username")
    Optional<UpgradeRequest> findByRequesterUsername(@Param("username") String username);

    Optional<UpgradeRequest> findByRequesterUser(User requesterUser);
}
