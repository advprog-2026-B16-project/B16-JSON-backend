package id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefundRepository extends JpaRepository<Refund, UUID> {
    boolean existsByOriginalTransactionId(UUID originalTransactionId);

    Optional<Refund> findByOriginalTransactionId(UUID originalTransactionId);

    List<Refund> findByRequesterId(UUID requesterId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Refund r where r.id = :refundId")
    Optional<Refund> findByIdForUpdate(@Param("refundId") UUID refundId);
}
