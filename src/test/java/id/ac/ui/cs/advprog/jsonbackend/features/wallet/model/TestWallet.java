package id.ac.ui.cs.advprog.jsonbackend.features.wallet.model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class TestWallet {
    private Wallet wallet;
    private final UUID USER_ID = UUID.randomUUID();
    @BeforeEach void setUp() { wallet = new Wallet(USER_ID); }
    @Test void testWalletCreation() { assertEquals(USER_ID, wallet.getUserId()); assertEquals(BigDecimal.ZERO, wallet.getBalance()); }
}