package id.ac.ui.cs.advprog.jsonbackend.repository;

import id.ac.ui.cs.advprog.jsonbackend.model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, String> {

    List<WalletTransaction> findByWalletId(String walletId);
}
