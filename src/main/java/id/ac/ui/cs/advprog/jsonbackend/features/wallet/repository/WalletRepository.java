package id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}