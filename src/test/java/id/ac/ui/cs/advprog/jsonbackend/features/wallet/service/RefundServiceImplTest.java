package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.RefundRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.RefundNotAllowedException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefundServiceImplTest {

    private RefundRepository refundRepository;
    private OrderRepository orderRepository;
    private TransactionService transactionService;
    private RefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        refundRepository = mock(RefundRepository.class);
        orderRepository = mock(OrderRepository.class);
        transactionService = mock(TransactionService.class);
        refundService = new RefundServiceImpl(refundRepository, orderRepository, transactionService);
    }

    @Test
    void requestRefundShouldAllowCompletedOrderWithinThreeDays() {
        User user = user();
        Order order = order(OrderStatus.COMPLETED, LocalDateTime.now().minusDays(2));
        Transaction originalTransaction = paymentTransaction(user.getId(), order.getOrderId());
        RefundRequest request = refundRequest(originalTransaction.getId(), "Barang rusak");

        when(transactionService.getTransactionByIdForUpdate(originalTransaction.getId().toString())).thenReturn(originalTransaction);
        when(refundRepository.existsByOriginalTransactionId(originalTransaction.getId())).thenReturn(false);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));
        when(refundRepository.save(any(Refund.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Refund result = refundService.requestRefund(user, request);

        assertEquals(TransactionStatus.PENDING, result.getStatus());
        verify(refundRepository).save(any(Refund.class));
    }

    @Test
    void requestRefundShouldRejectCompletedOrderAfterThreeDays() {
        User user = user();
        Order order = order(OrderStatus.COMPLETED, LocalDateTime.now().minusDays(4));
        Transaction originalTransaction = paymentTransaction(user.getId(), order.getOrderId());
        RefundRequest request = refundRequest(originalTransaction.getId(), "Telat refund");

        when(transactionService.getTransactionByIdForUpdate(originalTransaction.getId().toString())).thenReturn(originalTransaction);
        when(refundRepository.existsByOriginalTransactionId(originalTransaction.getId())).thenReturn(false);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        RefundNotAllowedException exception = assertThrows(RefundNotAllowedException.class,
                () -> refundService.requestRefund(user, request));

        assertEquals("Refund can only be requested within 3 days after order completion", exception.getMessage());
        verify(refundRepository, never()).save(any());
    }

    @Test
    void requestRefundShouldRejectCancelledOrder() {
        User user = user();
        Order order = order(OrderStatus.CANCELLED, LocalDateTime.now());
        Transaction originalTransaction = paymentTransaction(user.getId(), order.getOrderId());
        RefundRequest request = refundRequest(originalTransaction.getId(), "Cancelled order");

        when(transactionService.getTransactionByIdForUpdate(originalTransaction.getId().toString())).thenReturn(originalTransaction);
        when(refundRepository.existsByOriginalTransactionId(originalTransaction.getId())).thenReturn(false);
        when(orderRepository.findById(order.getOrderId())).thenReturn(Optional.of(order));

        RefundNotAllowedException exception = assertThrows(RefundNotAllowedException.class,
                () -> refundService.requestRefund(user, request));

        assertEquals("Order must be completed before refund can be requested", exception.getMessage());
        verify(refundRepository, never()).save(any());
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

    private static Order order(OrderStatus status, LocalDateTime updatedAt) {
        Order order = new Order(
                UUID.randomUUID(),
                "prod-abc-123",
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                "Jl. Margonda"
        );
        order.setOrderStatus(status);
        order.setUpdatedAt(updatedAt);
        return order;
    }

    private static Transaction paymentTransaction(UUID userId, UUID orderId) {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                userId,
                TransactionType.PAYMENT,
                new BigDecimal("125000"),
                "Payment for order " + orderId
        );
        transaction.setId(UUID.randomUUID());
        transaction.setOrderId(orderId);
        transaction.markSuccess();
        return transaction;
    }

    private static RefundRequest refundRequest(UUID transactionId, String reason) {
        RefundRequest request = new RefundRequest();
        request.setTransactionId(transactionId);
        request.setReason(reason);
        return request;
    }
}
