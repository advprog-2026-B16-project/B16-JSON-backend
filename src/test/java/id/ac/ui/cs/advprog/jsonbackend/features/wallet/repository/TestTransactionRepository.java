package id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TestTransactionRepository {

    @Autowired
    private TransactionRepository transactionRepository;

    private UUID WALLET_ID_1;
    private UUID WALLET_ID_2;
    private UUID NON_EXISTENT_WALLET_ID;

    private UUID USER_ID_1;
    private UUID USER_ID_2;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        WALLET_ID_1 = UUID.randomUUID();
        WALLET_ID_2 = UUID.randomUUID();
        NON_EXISTENT_WALLET_ID = UUID.randomUUID();

        USER_ID_1 = UUID.randomUUID();
        USER_ID_2 = UUID.randomUUID();

        Transaction transaction1 = new Transaction(
                WALLET_ID_1, USER_ID_1,
                TransactionType.TOP_UP,
                BigDecimal.valueOf(100),
                "Initial deposit"
        );

        Transaction transaction2 = new Transaction(
                WALLET_ID_1, USER_ID_1,
                TransactionType.WITHDRAW,
                BigDecimal.valueOf(25),
                "Purchase"
        );

        transactionRepository.saveAll(List.of(transaction1, transaction2));

        Transaction transaction3 = new Transaction(
                WALLET_ID_2, USER_ID_2,
                TransactionType.TOP_UP,
                BigDecimal.valueOf(500),
                "Refund"
        );

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