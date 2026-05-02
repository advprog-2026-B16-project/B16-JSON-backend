package id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByUserId(String userId);
    boolean existsByUserId(String userId);
}