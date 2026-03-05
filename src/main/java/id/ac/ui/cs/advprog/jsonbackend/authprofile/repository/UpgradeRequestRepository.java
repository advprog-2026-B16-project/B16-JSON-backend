package id.ac.ui.cs.advprog.jsonbackend.repository;

import java.util.Optional;

import java.util.UUID;

import id.ac.ui.cs.advprog.jsonbackend.model.UpgradeRequest;
import id.ac.ui.cs.advprog.jsonbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpgradeRequestRepository extends JpaRepository<UpgradeRequest, UUID> {
    Optional<UpgradeRequest> findByRequesterUser(User user);
//    Optional<User> findByEmail(String email);
}