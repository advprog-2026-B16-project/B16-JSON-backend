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
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.RefundNotAllowedException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JastiperRefundServiceImplTest {

    private static final UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TITIPER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID JASTIPER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private RefundRepository refundRepository;
    private OrderRepository orderRepository;
    private WalletService walletService;
    private TransactionService transactionService;
    private JastiperRefundServiceImpl refundService;

    @BeforeEach
    void setUp() {
        refundRepository = mock(RefundRepository.class);
        orderRepository = mock(OrderRepository.class);
        walletService = mock(WalletService.class);
        transactionService = mock(TransactionService.class);
        refundService = new JastiperRefundServiceImpl(refundRepository, orderRepository, walletService, transactionService);
    }

    @Test
    void approveRefundShouldCreditTitiperAndMarkRefundSuccessWithoutDebitingJastiper() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Order order = order(OrderStatus.COMPLETED, JASTIPER_ID);
        Refund refund = refund();
        Wallet titiperWallet = new Wallet(TITIPER_ID);
        UUID titiperWalletId = UUID.randomUUID();
        titiperWallet.setId(titiperWalletId);
        Transaction refundTransaction = new Transaction(titiperWalletId, TITIPER_ID, TransactionType.REFUND, refund.getAmount(), "Refund");
        UUID refundTransactionId = UUID.randomUUID();
        refundTransaction.setId(refundTransactionId);

        when(refundRepository.findByIdForUpdate(refund.getId())).thenReturn(Optional.of(refund));
        when(orderRepository.findByIdForUpdate(ORDER_ID)).thenReturn(Optional.of(order));
        when(walletService.findWallet(TITIPER_ID.toString())).thenReturn(titiperWallet);
        when(transactionService.createTransaction(eq(titiperWallet), eq(TransactionType.REFUND), eq(refund.getAmount()), any(String.class)))
                .thenReturn(refundTransaction);
        when(refundRepository.save(refund)).thenReturn(refund);

        Refund result = refundService.approveRefund(jastiper, refund.getId());

        assertEquals(TransactionStatus.SUCCESS, result.getStatus());
        assertEquals(refundTransactionId, result.getRefundTransactionId());
        verify(walletService, never()).debit(any(), any());
        verify(walletService).credit(TITIPER_ID.toString(), refund.getAmount());
        verify(transactionService).markSuccess(refundTransactionId.toString());
    }

    @Test
    void approveRefundShouldRejectNonPendingRefund() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Refund refund = refund();
        refund.markSuccess(UUID.randomUUID());

        when(refundRepository.findByIdForUpdate(refund.getId())).thenReturn(Optional.of(refund));

        RefundNotAllowedException exception = assertThrows(RefundNotAllowedException.class,
                () -> refundService.approveRefund(jastiper, refund.getId()));

        assertEquals("Only pending refund can be approved", exception.getMessage());
        verify(orderRepository, never()).findByIdForUpdate(any());
    }

    @Test
    void approveRefundShouldRejectWrongJastiper() {
        User jastiper = user(UUID.randomUUID(), UserRole.JASTIPER);
        Order order = order(OrderStatus.COMPLETED, JASTIPER_ID);
        Refund refund = refund();

        when(refundRepository.findByIdForUpdate(refund.getId())).thenReturn(Optional.of(refund));
        when(orderRepository.findByIdForUpdate(ORDER_ID)).thenReturn(Optional.of(order));

        RefundNotAllowedException exception = assertThrows(RefundNotAllowedException.class,
                () -> refundService.approveRefund(jastiper, refund.getId()));

        assertEquals("Jastiper can only process refund for their own order", exception.getMessage());
        verify(walletService, never()).debit(any(), any());
    }

    @Test
    void getMyRefundsShouldReturnRefundsForJastiperOrders() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Refund refund = refund();

        when(refundRepository.findByJastiperId(JASTIPER_ID)).thenReturn(List.of(refund));

        List<Refund> result = refundService.getMyRefunds(jastiper);

        assertEquals(1, result.size());
        assertEquals(refund.getId(), result.get(0).getId());
        verify(refundRepository).findByJastiperId(JASTIPER_ID);
    }

    @Test
    void rejectRefundShouldMarkPendingRefundFailedReleasePayoutAndMarkOrderDone() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Order order = order(OrderStatus.COMPLETED, JASTIPER_ID);
        Refund refund = refund();
        Wallet jastiperWallet = new Wallet(JASTIPER_ID);
        UUID jastiperWalletId = UUID.randomUUID();
        jastiperWallet.setId(jastiperWalletId);
        Transaction payoutTransaction = new Transaction(jastiperWalletId, JASTIPER_ID, TransactionType.PAYOUT, refund.getAmount(), "Payout");
        UUID payoutTransactionId = UUID.randomUUID();
        payoutTransaction.setId(payoutTransactionId);

        when(refundRepository.findByIdForUpdate(refund.getId())).thenReturn(Optional.of(refund));
        when(orderRepository.findByIdForUpdate(ORDER_ID)).thenReturn(Optional.of(order));
        when(walletService.findWallet(JASTIPER_ID.toString())).thenReturn(jastiperWallet);
        when(transactionService.createTransaction(eq(jastiperWallet), eq(TransactionType.PAYOUT), eq(refund.getAmount()), any(String.class)))
                .thenReturn(payoutTransaction);
        when(refundRepository.save(refund)).thenReturn(refund);

        Refund result = refundService.rejectRefund(jastiper, refund.getId());

        assertEquals(TransactionStatus.FAILED, result.getStatus());
        assertEquals(OrderStatus.DONE, order.getOrderStatus());
        verify(walletService, never()).debit(any(), any());
        verify(walletService).credit(JASTIPER_ID.toString(), refund.getAmount());
        verify(transactionService).markSuccess(payoutTransactionId.toString());
        verify(orderRepository).save(order);
    }

    @Test
    void rejectRefundShouldRejectNonPendingRefund() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Refund refund = refund();
        refund.markFailed();

        when(refundRepository.findByIdForUpdate(refund.getId())).thenReturn(Optional.of(refund));

        RefundNotAllowedException exception = assertThrows(RefundNotAllowedException.class,
                () -> refundService.rejectRefund(jastiper, refund.getId()));

        assertEquals("Only pending refund can be rejected", exception.getMessage());
        verify(orderRepository, never()).findByIdForUpdate(any());
    }

    private static User user(UUID id, UserRole role) {
        return User.builder()
                .id(id)
                .username("user")
                .email("user@example.com")
                .password("secret")
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private static Order order(OrderStatus status, UUID jastiperId) {
        Order order = new Order(ORDER_ID, "prod-abc-123", TITIPER_ID, jastiperId, 1, "Jl. Margonda");
        order.setOrderStatus(status);
        return order;
    }

    private static Refund refund() {
        Transaction original = new Transaction(UUID.randomUUID(), TITIPER_ID, TransactionType.PAYMENT, new BigDecimal("125000"), "Payment");
        original.setId(UUID.randomUUID());
        original.setOrderId(ORDER_ID);
        Refund refund = new Refund(original, "Barang rusak");
        refund.setId(UUID.randomUUID());
        return refund;
    }
}
