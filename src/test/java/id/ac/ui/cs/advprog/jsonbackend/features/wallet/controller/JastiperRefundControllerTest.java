package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.JastiperRefundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JastiperRefundControllerTest {

    private JastiperRefundService refundService;
    private JastiperRefundController controller;

    @BeforeEach
    void setUp() {
        refundService = mock(JastiperRefundService.class);
        controller = new JastiperRefundController(refundService);
    }

    @Test
    void approveRefundShouldDelegateToService() {
        User user = user();
        Refund refund = refund(user.getId());

        when(refundService.approveRefund(user, refund.getId())).thenReturn(refund);

        assertEquals(refund.getId(), controller.approveRefund(user, refund.getId()).getBody().getId());
        verify(refundService).approveRefund(user, refund.getId());
    }

    private static User user() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("jastiper")
                .email("jastiper@example.com")
                .password("secret")
                .role(UserRole.JASTIPER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private static Refund refund(UUID userId) {
        Transaction transaction = new Transaction(UUID.randomUUID(), userId, TransactionType.PAYMENT, new BigDecimal("125000"), "Payment");
        transaction.setId(UUID.randomUUID());
        Refund refund = new Refund(transaction, "reason");
        refund.setId(UUID.randomUUID());
        return refund;
    }
}
