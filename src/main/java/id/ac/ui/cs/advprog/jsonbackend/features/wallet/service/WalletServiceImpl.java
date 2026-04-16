package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Wallet createWallet(String userId) {
        Wallet wallet = new Wallet(userId);
        return walletRepository.save(wallet);
    }

    @Override
    public void credit(String userId, BigDecimal amount) {
        Wallet wallet = findWallet(userId);
        wallet.credit(amount);
    }

    @Override
    public void debit(String userId, BigDecimal amount) {
        Wallet wallet = findWallet(userId);
        wallet.debit(amount);
    }

    @Override
    public BigDecimal getBalance(String userId) {
        Wallet wallet = findWallet(userId);
        return wallet.getBalance();
    }

    @Override
    public Wallet findWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));
    }
}
