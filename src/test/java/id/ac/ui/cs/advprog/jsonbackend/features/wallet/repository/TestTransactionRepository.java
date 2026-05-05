package id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.wallet.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TestTransactionRepository {

    @Autowired
    private TransactionRepository transactionRepository;

    private static final UUID WALLET_ID_1 = UUID.randomUUID();
    private static final UUID WALLET_ID_2 = UUID.randomUUID();
    private static final UUID NON_EXISTENT_WALLET_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        Transaction transaction1 = new Transaction(WALLET_ID_1, TransactionType.TOP_UP, BigDecimal.valueOf(100), "Initial deposit");
        Transaction transaction2 = new Transaction(WALLET_ID_1, TransactionType.WITHDRAW, BigDecimal.valueOf(25), "Purchase");
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        Transaction transaction3 = new Transaction(WALLET_ID_2, TransactionType.TOP_UP, BigDecimal.valueOf(500), "Refund");
        transactionRepository.save(transaction3);
    }

    @Test
    void testFindByWalletId_Found() {
        List<Transaction> transactions = transactionRepository.findByWalletId(WALLET_ID_1);
        assertEquals(2, transactions.size());
    }

    @Test
    void testFindByWalletId_NotFound() {
        List<Transaction> transactions = transactionRepository.findByWalletId(NON_EXISTENT_WALLET_ID);
        assertTrue(transactions.isEmpty());
    }
}
