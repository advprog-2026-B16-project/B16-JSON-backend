package id.ac.ui.cs.advprog.jsonbackend.features.payment.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.dto.PaymentRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.exception.PaymentNotAllowedException;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.exception.PaymentNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.exception.PaymentUnauthorizedException;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.service.ProductStockService;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.service.OrderPricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    private PaymentRepository paymentRepository;
    private OrderRepository orderRepository;
    private OrderPricingService orderPricingService;
    private ProductStockService productStockService;
    private WalletService walletService;
    private WalletTransactionService walletTransactionService;
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        orderRepository = mock(OrderRepository.class);
        orderPricingService = mock(OrderPricingService.class);
        productStockService = mock(ProductStockService.class);
        walletService = mock(WalletService.class);
        walletTransactionService = mock(WalletTransactionService.class);
        paymentService = new PaymentServiceImpl(
                paymentRepository,
                orderRepository,
                orderPricingService,
                productStockService,
                walletService,
                walletTransactionService
        );
    }

    @Test
    void createPaymentGeneratesReferenceAndExpiresInFifteenMinutes() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Wallet wallet = new Wallet(user.getId());
        wallet.setId(UUID.randomUUID());
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(order.getOrderId());

        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.findFirstByOrderIdAndStatusInOrderByCreatedAtDesc(eq(order.getOrderId()), anySet()))
                .thenReturn(Optional.empty());
        when(walletService.findWallet(user.getId().toString())).thenReturn(wallet);
        when(orderPricingService.calculateTotal(order)).thenReturn(new BigDecimal("125000"));
        when(paymentRepository.existsByReferenceCode(anyString())).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.createPayment(user, request);

        assertEquals(order.getOrderId(), result.getOrderId());
        assertEquals(user.getId(), result.getUserId());
        assertEquals(new BigDecimal("125000"), result.getAmount());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertTrue(result.getReferenceCode().startsWith("PAY-"));
        assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(14)));
        assertTrue(result.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(16)));
        verify(productStockService).reserveStock(order);
    }

    @Test
    void createPaymentReturnsActivePendingPayment() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment existingPayment = buildPayment(user, order, PaymentStatus.PENDING, LocalDateTime.now().plusMinutes(5));
        PaymentRequest request = buildRequest(order.getOrderId());

        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.findFirstByOrderIdAndStatusInOrderByCreatedAtDesc(eq(order.getOrderId()), anySet()))
                .thenReturn(Optional.of(existingPayment));

        Payment result = paymentService.createPayment(user, request);

        assertSame(existingPayment, result);
        verify(walletService, never()).findWallet(anyString());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void createPaymentExpiresOldPendingPaymentAndCreatesNewOne() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Wallet wallet = buildWallet(user.getId(), new BigDecimal("200000"));
        Payment existingPayment = buildPayment(user, order, PaymentStatus.PENDING, LocalDateTime.now().minusMinutes(1));
        PaymentRequest request = buildRequest(order.getOrderId());

        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.findFirstByOrderIdAndStatusInOrderByCreatedAtDesc(eq(order.getOrderId()), anySet()))
                .thenReturn(Optional.of(existingPayment));
        when(walletService.findWallet(user.getId().toString())).thenReturn(wallet);
        when(orderPricingService.calculateTotal(order)).thenReturn(new BigDecimal("125000"));
        when(paymentRepository.existsByReferenceCode(anyString())).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.createPayment(user, request);

        assertEquals(PaymentStatus.EXPIRED, existingPayment.getStatus());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        assertNotSame(existingPayment, result);
        verify(productStockService).releaseReservedStock(order);
        verify(productStockService).reserveStock(order);
    }

    @Test
    void createPaymentRejectsAlreadyPaidOrder() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment existingPayment = buildPayment(user, order, PaymentStatus.SUCCESS, LocalDateTime.now().plusMinutes(5));
        PaymentRequest request = buildRequest(order.getOrderId());

        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.findFirstByOrderIdAndStatusInOrderByCreatedAtDesc(eq(order.getOrderId()), anySet()))
                .thenReturn(Optional.of(existingPayment));

        assertThrows(PaymentNotAllowedException.class, () -> paymentService.createPayment(user, request));
    }

    @Test
    void createPaymentRejectsUnauthenticatedUser() {
        assertThrows(PaymentUnauthorizedException.class, () -> paymentService.createPayment(null, buildRequest(UUID.randomUUID())));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void createPaymentRejectsMissingOrder() {
        User user = buildUser();
        PaymentRequest request = buildRequest(UUID.randomUUID());

        when(orderRepository.findByIdForUpdate(request.getOrderId())).thenReturn(Optional.empty());

        assertThrows(PaymentNotAllowedException.class, () -> paymentService.createPayment(user, request));
    }

    @Test
    void createPaymentRejectsDifferentOwner() {
        User user = buildUser();
        Order order = buildOrder(UUID.randomUUID());
        PaymentRequest request = buildRequest(order.getOrderId());

        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));

        assertThrows(PaymentUnauthorizedException.class, () -> paymentService.createPayment(user, request));
    }

    @Test
    void createPaymentRejectsNonPendingOrder() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        order.updateStatus(OrderStatus.PAID);
        PaymentRequest request = buildRequest(order.getOrderId());

        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));

        assertThrows(PaymentNotAllowedException.class, () -> paymentService.createPayment(user, request));
    }

    @Test
    void createPaymentRejectsReferenceGenerationFailure() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Wallet wallet = buildWallet(user.getId(), new BigDecimal("200000"));
        PaymentRequest request = buildRequest(order.getOrderId());

        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.findFirstByOrderIdAndStatusInOrderByCreatedAtDesc(eq(order.getOrderId()), anySet()))
                .thenReturn(Optional.empty());
        when(walletService.findWallet(user.getId().toString())).thenReturn(wallet);
        when(orderPricingService.calculateTotal(order)).thenReturn(new BigDecimal("125000"));
        when(paymentRepository.existsByReferenceCode(anyString())).thenReturn(true);

        assertThrows(PaymentNotAllowedException.class, () -> paymentService.createPayment(user, request));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void payDebitsWalletCreatesTransactionAndMarksOrderPaid() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Wallet wallet = new Wallet(user.getId());
        wallet.setId(UUID.randomUUID());
        wallet.setBalance(new BigDecimal("200000"));
        Payment payment = new Payment(
                order.getOrderId(),
                user.getId(),
                wallet.getId(),
                "PAY-202605082000-ABCDEF1234567890",
                new BigDecimal("125000"),
                LocalDateTime.now().plusMinutes(15)
        );
        Transaction transaction = new Transaction(
                wallet.getId(),
                user.getId(),
                TransactionType.PAYMENT,
                payment.getAmount(),
                "Payment"
        );
        UUID transactionId = UUID.randomUUID();
        transaction.setId(transactionId);

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));
        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(walletTransactionService.requestPayment(
                user.getId().toString(),
                order.getOrderId().toString(),
                payment.getAmount()
        ))
                .thenReturn(transaction);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.pay(user, payment.getReferenceCode());

        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(transactionId, result.getTransactionId());
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        verify(walletTransactionService).requestPayment(
                user.getId().toString(),
                order.getOrderId().toString(),
                payment.getAmount()
        );
    }

    @Test
    void payReturnsSuccessfulPaymentIdempotently() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment payment = buildPayment(user, order, PaymentStatus.SUCCESS, LocalDateTime.now().plusMinutes(5));

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));

        Payment result = paymentService.pay(user, payment.getReferenceCode());

        assertSame(payment, result);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(walletService);
        verifyNoInteractions(walletTransactionService);
    }

    @Test
    void payRejectsUnauthenticatedUser() {
        assertThrows(PaymentUnauthorizedException.class, () -> paymentService.pay(null, "PAY-REF"));
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void payRejectsMissingReference() {
        User user = buildUser();

        when(paymentRepository.findByReferenceCodeForUpdate("PAY-MISSING")).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.pay(user, "PAY-MISSING"));
    }

    @Test
    void payRejectsDifferentOwner() {
        User user = buildUser();
        User owner = buildUser();
        Order order = buildOrder(owner.getId());
        Payment payment = buildPayment(owner, order, PaymentStatus.PENDING, LocalDateTime.now().plusMinutes(5));

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));

        assertThrows(PaymentUnauthorizedException.class, () -> paymentService.pay(user, payment.getReferenceCode()));
    }

    @Test
    void payRejectsNonPendingPayment() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment payment = buildPayment(user, order, PaymentStatus.FAILED, LocalDateTime.now().plusMinutes(5));

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));

        assertThrows(PaymentNotAllowedException.class, () -> paymentService.pay(user, payment.getReferenceCode()));
    }

    @Test
    void payExpiresExpiredPayment() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment payment = buildPayment(user, order, PaymentStatus.PENDING, LocalDateTime.now().minusSeconds(1));

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));
        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.pay(user, payment.getReferenceCode());

        assertEquals(PaymentStatus.EXPIRED, result.getStatus());
        verify(orderRepository).findByIdForUpdate(order.getOrderId());
        verify(productStockService).releaseReservedStock(order);
        verifyNoInteractions(walletService);
        verifyNoInteractions(walletTransactionService);
    }

    @Test
    void payRejectsMissingOrder() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment payment = buildPayment(user, order, PaymentStatus.PENDING, LocalDateTime.now().plusMinutes(5));

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));
        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.empty());

        assertThrows(PaymentNotAllowedException.class, () -> paymentService.pay(user, payment.getReferenceCode()));
    }

    @Test
    void payRejectsNonPendingOrder() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        order.updateStatus(OrderStatus.CANCELLED);
        Payment payment = buildPayment(user, order, PaymentStatus.PENDING, LocalDateTime.now().plusMinutes(5));

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));
        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));

        assertThrows(PaymentNotAllowedException.class, () -> paymentService.pay(user, payment.getReferenceCode()));
    }

    @Test
    void payRejectsInsufficientBalance() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment payment = buildPayment(user, order, PaymentStatus.PENDING, LocalDateTime.now().plusMinutes(5));

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));
        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        doThrow(new InsufficientBalanceException()).when(walletTransactionService).requestPayment(
                user.getId().toString(),
                order.getOrderId().toString(),
                payment.getAmount()
        );

        assertThrows(InsufficientBalanceException.class, () -> paymentService.pay(user, payment.getReferenceCode()));
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void payMarksPaymentFailedWhenDebitFailsAfterTransactionCreated() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment payment = buildPayment(user, order, PaymentStatus.PENDING, LocalDateTime.now().plusMinutes(5));

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));
        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new RuntimeException("debit failed")).when(walletTransactionService).requestPayment(
                user.getId().toString(),
                order.getOrderId().toString(),
                payment.getAmount()
        );

        assertThrows(RuntimeException.class, () -> paymentService.pay(user, payment.getReferenceCode()));
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        verify(productStockService).releaseReservedStock(order);
        verify(paymentRepository).save(payment);
    }

    @Test
    void cancelPaymentReleasesReservedStock() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment payment = buildPayment(user, order, PaymentStatus.PENDING, LocalDateTime.now().plusMinutes(5));

        when(paymentRepository.findByReferenceCodeForUpdate(payment.getReferenceCode())).thenReturn(Optional.of(payment));
        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.cancelPayment(user, payment.getReferenceCode());

        assertEquals(PaymentStatus.CANCELLED, result.getStatus());
        verify(productStockService).releaseReservedStock(order);
    }

    @Test
    void getMyPaymentsReturnsUserPayments() {
        User user = buildUser();
        Order order = buildOrder(user.getId());
        Payment payment = buildPayment(user, order, PaymentStatus.PENDING, LocalDateTime.now().plusMinutes(5));

        when(paymentRepository.findByUserId(user.getId())).thenReturn(java.util.List.of(payment));

        assertEquals(java.util.List.of(payment), paymentService.getMyPayments(user));
    }

    @Test
    void getMyPaymentsRejectsUnauthenticatedUser() {
        assertThrows(PaymentUnauthorizedException.class, () -> paymentService.getMyPayments(null));
    }

    private User buildUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("titiper")
                .email("titiper@example.com")
                .password("password")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private Order buildOrder(UUID userId) {
        return new Order(
                UUID.randomUUID(),
                "prod-abc-123",
                userId,
                UUID.randomUUID(),
                1,
                "Jl. Margonda"
        );
    }

    private Wallet buildWallet(UUID userId, BigDecimal balance) {
        Wallet wallet = new Wallet(userId);
        wallet.setId(UUID.randomUUID());
        wallet.setBalance(balance);
        return wallet;
    }

    private PaymentRequest buildRequest(UUID orderId) {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        return request;
    }

    private Payment buildPayment(User user, Order order, PaymentStatus status, LocalDateTime expiresAt) {
        Wallet wallet = buildWallet(user.getId(), new BigDecimal("200000"));
        Payment payment = new Payment(
                order.getOrderId(),
                user.getId(),
                wallet.getId(),
                "PAY-202605082000-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(),
                new BigDecimal("125000"),
                expiresAt
        );
        if (status == PaymentStatus.SUCCESS) {
            payment.markSuccess(UUID.randomUUID());
        } else if (status == PaymentStatus.EXPIRED) {
            payment.markExpired();
        } else if (status == PaymentStatus.FAILED) {
            payment.markFailed();
        } else if (status == PaymentStatus.CANCELLED) {
            payment.setStatus(PaymentStatus.CANCELLED);
        }
        return payment;
    }
}
