package id.ac.ui.cs.advprog.jsonbackend.features.payment.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    boolean existsByReferenceCode(String referenceCode);

    List<Payment> findByUserId(UUID userId);

    Optional<Payment> findFirstByOrderIdAndStatusInOrderByCreatedAtDesc(UUID orderId, Collection<PaymentStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.referenceCode = :referenceCode")
    Optional<Payment> findByReferenceCodeForUpdate(@Param("referenceCode") String referenceCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.status = :status and p.expiresAt <= :now")
    List<Payment> findByStatusAndExpiresAtLessThanEqualForUpdate(
            @Param("status") PaymentStatus status,
            @Param("now") LocalDateTime now
    );
}
