package id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefundRepository extends JpaRepository<Refund, UUID> {
    boolean existsByOriginalTransactionId(UUID originalTransactionId);

    Optional<Refund> findByOriginalTransactionId(UUID originalTransactionId);

    List<Refund> findByRequesterId(UUID requesterId);
}
