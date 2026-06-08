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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByWalletId(UUID walletId);

    List<Transaction> findByTypeAndStatusOrderByCreatedAtDesc(TransactionType type, TransactionStatus status);

    @Query(value = """
            select
                id as transactionId,
                user_id as userId,
                wallet_id as walletId,
                amount as amount,
                status as status,
                created_at as createdAt,
                description as description
            from wallet_transactions
            where upper(replace(type::text, '-', '_')) in ('TOP_UP', 'TOPUP')
              and upper(status::text) = 'PENDING'
            order by created_at desc
            """, nativeQuery = true)
    List<PendingTopUpView> findPendingTopUpsForAdmin();

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

    interface PendingTopUpView {
        UUID getTransactionId();
        UUID getUserId();
        UUID getWalletId();
        BigDecimal getAmount();
        String getStatus();
        LocalDateTime getCreatedAt();
        String getDescription();
    }
}
