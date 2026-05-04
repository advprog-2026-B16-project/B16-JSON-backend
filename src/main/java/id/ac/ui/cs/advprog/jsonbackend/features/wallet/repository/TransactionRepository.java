package id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByWalletId(String walletId);

    Optional<Transaction> findById(String transactionId);
}
