package id.ac.ui.cs.advprog.jsonbackend.wallet.repository;

import id.ac.ui.cs.advprog.jsonbackend.wallet.model.WalletTransaction;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TestWalletTransactionRepository {

    @Autowired
    private WalletTransactionRepository transactionRepository;

    private final String WALLET_ID_1 = "wallet-abc";
    private final String WALLET_ID_2 = "wallet-xyz";
    private final String NON_EXISTENT_WALLET_ID = "wallet-999";

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        WalletTransaction transaction1 = new WalletTransaction(WALLET_ID_1, TransactionType.TOP_UP, BigDecimal.valueOf(100), "Initial deposit");
        WalletTransaction transaction2 = new WalletTransaction(WALLET_ID_1, TransactionType.WITHDRAW, BigDecimal.valueOf(25), "Purchase");
        transactionRepository.saveAll(List.of(transaction1, transaction2));

        WalletTransaction transaction3 = new WalletTransaction(WALLET_ID_2, TransactionType.TOP_UP, BigDecimal.valueOf(500), "Refund");
        transactionRepository.save(transaction3);
    }

    @Test
    void testFindByWalletId_whenTransactionsExist_shouldReturnListOfTransactions() {
        List<WalletTransaction> transactions = transactionRepository.findByWalletId(WALLET_ID_1);

        assertThat(transactions).isNotEmpty();
        assertThat(transactions).hasSize(2);

        for (WalletTransaction transaction : transactions) {
            assertThat(transaction.getWalletId()).isEqualTo(WALLET_ID_1);
        }
    }

    @Test
    void testFindByWalletId_whenNoTransactionsExist_shouldReturnEmptyList() {
        List<WalletTransaction> transactions = transactionRepository.findByWalletId(NON_EXISTENT_WALLET_ID);

        assertThat(transactions).isNotNull();
        assertThat(transactions).isEmpty();
    }
}