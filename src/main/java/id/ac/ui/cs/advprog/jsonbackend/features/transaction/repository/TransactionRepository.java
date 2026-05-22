package id.ac.ui.cs.advprog.jsonbackend.features.transaction.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByWalletId(UUID walletId);

    List<Transaction> findByTypeAndStatusOrderByCreatedAtDesc(TransactionType type, TransactionStatus status);

    long countByTypeAndStatus(TransactionType type, TransactionStatus status);

    @Query("select coalesce(sum(t.amount), 0) from Transaction t where t.type = :type and t.status = :status")
    BigDecimal sumAmountByTypeAndStatus(
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status
    );

    Optional<Transaction> findByIdAndUserId(UUID transactionId, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transaction t where t.id = :transactionId")
    Optional<Transaction> findByIdForUpdate(@Param("transactionId") UUID transactionId);
}
