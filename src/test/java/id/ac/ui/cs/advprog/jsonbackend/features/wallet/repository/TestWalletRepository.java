package id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TestWalletRepository {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void testFindByUserId() {
        UUID USER_ID = UUID.randomUUID();

        Wallet testWallet = new Wallet(USER_ID);
        walletRepository.save(testWallet);

        Optional<Wallet> foundWallet = walletRepository.findByUserId(USER_ID);

        assertTrue(foundWallet.isPresent());
        assertEquals(USER_ID, foundWallet.get().getUserId());
    }
}