package id.ac.ui.cs.advprog.jsonbackend.features.wallet.model;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;
@Entity @Table(name = "wallet") @Getter @Setter
public class Wallet {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true) private UUID userId;
    @Column(nullable = false) private BigDecimal balance;
    @Version private Long version;
    protected Wallet() {}
    public Wallet(UUID userId) { this.userId = userId; this.balance = BigDecimal.ZERO; }
    public void credit(BigDecimal amount) { this.balance = this.balance.add(amount); }
    public void debit(BigDecimal amount) { if (this.balance.compareTo(amount) < 0) throw new InsufficientBalanceException(); this.balance = this.balance.subtract(amount); }
}