package id.ac.ui.cs.advprog.jsonbackend.features.transaction.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByWalletId(UUID walletId);

    Optional<Transaction> findByIdAndUserId(UUID transactionId, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transaction t where t.id = :transactionId")
    Optional<Transaction> findByIdForUpdate(@Param("transactionId") UUID transactionId);
}
