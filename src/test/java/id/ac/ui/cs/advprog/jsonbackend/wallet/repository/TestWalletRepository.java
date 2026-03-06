package id.ac.ui.cs.advprog.jsonbackend.wallet.repository;

import id.ac.ui.cs.advprog.jsonbackend.wallet.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TestWalletRepository {

    @Autowired
    private WalletRepository walletRepository;

    private Wallet testWallet;
    private final String USER_ID = "user-123";
    private final String NON_EXISTENT_USER_ID = "user-999";

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        testWallet = new Wallet(USER_ID);
        testWallet.setBalance(BigDecimal.valueOf(1000));
        walletRepository.save(testWallet);
    }

    @Test
    void testFindByUserId_whenUserExists_shouldReturnOptionalOfWallet() {
        Optional<Wallet> foundWallet = walletRepository.findByUserId(USER_ID);
        assertThat(foundWallet).isPresent();
        assertThat(foundWallet.get().getId()).isEqualTo(testWallet.getId());
        assertThat(foundWallet.get().getUserId()).isEqualTo(USER_ID);
        assertThat(foundWallet.get().getBalance()).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    void testFindByUserId_whenUserDoesNotExist_shouldReturnEmptyOptional() {
        Optional<Wallet> foundWallet = walletRepository.findByUserId(NON_EXISTENT_USER_ID);
        assertThat(foundWallet).isEmpty();
    }
}