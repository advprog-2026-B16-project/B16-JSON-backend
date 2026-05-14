package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet createWallet(String userId) {
        UUID uid = UUID.fromString(userId);
        return walletRepository.findByUserId(uid)
                .orElseGet(() -> walletRepository.save(new Wallet(uid)));
    }

    @Override
    public void credit(String userId, BigDecimal amount) {
        findWallet(userId).credit(amount);
    }

    @Override
    public void debit(String userId, BigDecimal amount) {
        findWallet(userId).debit(amount);
    }

    @Override
    public BigDecimal getBalance(String userId) {
        return findWallet(userId).getBalance();
    }

    @Override
    public Wallet findWallet(String userId) {
        return walletRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new WalletNotFoundException(userId));
    }

    @Override
    public Wallet findWalletForUpdate(String userId) {
        return walletRepository.findByUserIdForUpdate(UUID.fromString(userId))
                .orElseThrow(() -> new WalletNotFoundException(userId));
    }
}
