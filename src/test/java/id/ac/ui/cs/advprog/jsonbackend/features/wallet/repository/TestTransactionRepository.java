package id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TestTransactionRepository {

    @Autowired
    private TransactionRepository transactionRepository;

    private final String WALLET_ID_1 = "wallet-abc";
    private final String WALLET_ID_2 = "wallet-xyz";
    private final String NON_EXISTENT_WALLET_ID = "wallet-999";

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        Transaction transaction1 = new Transaction(WALLET_ID_1, "user1", TransactionType.TOP_UP, BigDecimal.valueOf(100), "Initial deposit");
        Transaction transaction2 = new Transaction(WALLET_ID_1, "user1", TransactionType.WITHDRAW, BigDecimal.valueOf(25), "Purchase");
        transactionRepository.saveAll(List.of(transaction1, transaction2));

        Transaction transaction3 = new Transaction(WALLET_ID_2, "user2", TransactionType.TOP_UP, BigDecimal.valueOf(500), "Refund");
        transactionRepository.save(transaction3);
    }

    @Test
    void testFindByWalletId_whenTransactionsExist_shouldReturnListOfTransactions() {
        List<Transaction> transactions = transactionRepository.findByWalletId(WALLET_ID_1);

        assertThat(transactions).isNotEmpty();
        assertThat(transactions).hasSize(2);

        for (Transaction transaction : transactions) {
            assertThat(transaction.getWalletId()).isEqualTo(WALLET_ID_1);
        }
    }

    @Test
    void testFindByWalletId_whenNoTransactionsExist_shouldReturnEmptyList() {
        List<Transaction> transactions = transactionRepository.findByWalletId(NON_EXISTENT_WALLET_ID);

        assertThat(transactions).isNotNull();
        assertThat(transactions).isEmpty();
    }
}