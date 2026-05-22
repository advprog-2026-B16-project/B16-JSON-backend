package id.ac.ui.cs.advprog.jsonbackend.features.order.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

class TitiperOrderServiceImplTest {

    private static final UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TITIPER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID JASTIPER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private OrderRepository orderRepository;
    private PaymentRepository paymentRepository;
    private WalletService walletService;
    private TransactionService transactionService;
    private OrderPricingService orderPricingService;
    private TitiperOrderServiceImpl titiperOrderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        walletService = mock(WalletService.class);
        transactionService = mock(TransactionService.class);
        orderPricingService = mock(OrderPricingService.class);
        titiperOrderService = new TitiperOrderServiceImpl(
                orderRepository,
                paymentRepository,
                walletService,
                transactionService,
                orderPricingService
        );
    }

    @Test
    void confirmDoneShouldReleasePayoutToJastiperAndMarkOrderDone() {
        User titiper = user(TITIPER_ID, UserRole.TITIPER);
        Order order = order(OrderStatus.COMPLETED, TITIPER_ID, JASTIPER_ID);
        Wallet jastiperWallet = new Wallet(JASTIPER_ID);
        UUID walletId = UUID.randomUUID();
        jastiperWallet.setId(walletId);
        Payment payment = new Payment(ORDER_ID, TITIPER_ID, UUID.randomUUID(), "PAY-REF", new BigDecimal("125000"), LocalDateTime.now().plusMinutes(5));
        payment.markSuccess(UUID.randomUUID());
        Transaction payout = new Transaction(walletId, JASTIPER_ID, TransactionType.PAYOUT, payment.getAmount(), "Payout for order " + ORDER_ID);
        UUID payoutId = UUID.randomUUID();
        payout.setId(payoutId);

        when(orderRepository.findByIdForUpdate(ORDER_ID)).thenReturn(Optional.of(order));
        when(paymentRepository.findFirstByOrderIdAndStatusOrderByCreatedAtDesc(ORDER_ID, PaymentStatus.SUCCESS))
                .thenReturn(Optional.of(payment));
        when(walletService.findWallet(JASTIPER_ID.toString())).thenReturn(jastiperWallet);
        when(transactionService.createTransaction(eq(jastiperWallet), eq(TransactionType.PAYOUT), eq(payment.getAmount()), any(String.class)))
                .thenReturn(payout);
        when(orderRepository.save(order)).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderPricingService.calculateTotal(order)).thenReturn(new BigDecimal("125000"));

        OrderResponse response = titiperOrderService.confirmDone(titiper, ORDER_ID);

        assertEquals(OrderStatus.DONE, response.getOrderStatus());
        verify(walletService).credit(JASTIPER_ID.toString(), payment.getAmount());
        verify(transactionService).markSuccess(payoutId.toString());
        verify(orderRepository).save(order);
    }

    @Test
    void confirmDoneShouldRejectWrongOwner() {
        User titiper = user(UUID.randomUUID(), UserRole.TITIPER);
        Order order = order(OrderStatus.COMPLETED, TITIPER_ID, JASTIPER_ID);

        when(orderRepository.findByIdForUpdate(ORDER_ID)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> titiperOrderService.confirmDone(titiper, ORDER_ID));

        assertEquals("Titiper hanya boleh menyelesaikan order miliknya sendiri", exception.getMessage());
        verify(walletService, never()).credit(any(), any());
    }

    @Test
    void confirmDoneShouldRejectNonTitiperUser() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> titiperOrderService.confirmDone(jastiper, ORDER_ID));

        assertEquals("Only titiper can confirm order done", exception.getMessage());
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

    private static Order order(OrderStatus status, UUID titiperId, UUID jastiperId) {
        Order order = new Order(ORDER_ID, "prod-abc-123", titiperId, jastiperId, 1, "Jl. Margonda");
        order.setOrderStatus(status);
        return order;
    }
}
