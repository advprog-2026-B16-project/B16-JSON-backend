package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
@Service @Transactional
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepo;
    public WalletServiceImpl(WalletRepository walletRepo) { this.walletRepo = walletRepo; }
    @Override public Wallet createWallet(String userId) { UUID uid = UUID.fromString(userId); return walletRepo.findByUserId(uid).orElseGet(() -> walletRepo.save(new Wallet(uid))); }
    @Override public void credit(String userId, BigDecimal amount) { findWallet(userId).credit(amount); }
    @Override public void debit(String userId, BigDecimal amount) { findWallet(userId).debit(amount); }
    @Override public BigDecimal getBalance(String userId) { return findWallet(userId).getBalance(); }
    @Override public Wallet findWallet(String userId) { return walletRepo.findByUserId(UUID.fromString(userId)).orElseThrow(() -> new WalletNotFoundException(userId)); }
}