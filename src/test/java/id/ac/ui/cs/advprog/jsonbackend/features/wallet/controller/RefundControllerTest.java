package id.ac.ui.cs.advprog.jsonbackend.features.wallet.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.RefundRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.RefundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefundControllerTest {

    private RefundService refundService;
    private RefundController controller;

    @BeforeEach
    void setUp() {
        refundService = mock(RefundService.class);
        controller = new RefundController(refundService);
    }

    @Test
    void controllerShouldDelegateRefundOperations() {
        User user = user();
        Refund refund = refund(user.getId());
        RefundRequest request = new RefundRequest();
        request.setTransactionId(refund.getOriginalTransactionId());

        when(refundService.requestRefund(user, request)).thenReturn(refund);
        when(refundService.getMyRefunds(user)).thenReturn(List.of(refund));

        assertEquals(refund.getId(), controller.requestRefund(user, request).getBody().getId());
        assertEquals(1, controller.getMyRefunds(user).getBody().size());
        verify(refundService).requestRefund(user, request);
        verify(refundService).getMyRefunds(user);
    }

    private static User user() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("titiper")
                .email("titiper@example.com")
                .password("secret")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private static Refund refund(UUID userId) {
        Transaction transaction = new Transaction(UUID.randomUUID(), userId, TransactionType.PAYMENT, new BigDecimal("125000"), "Payment");
        transaction.setId(UUID.randomUUID());
        transaction.setOrderId(UUID.randomUUID());
        Refund refund = new Refund(transaction, "reason");
        refund.setId(UUID.randomUUID());
        return refund;
    }
}
