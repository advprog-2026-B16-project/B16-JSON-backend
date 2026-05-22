package id.ac.ui.cs.advprog.jsonbackend.features.payment.service;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.service.ProductStockService;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentExpirationSchedulerTest {

    private PaymentRepository paymentRepository;
    private OrderRepository orderRepository;
    private ProductStockService productStockService;
    private PaymentExpirationScheduler scheduler;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        orderRepository = mock(OrderRepository.class);
        productStockService = mock(ProductStockService.class);
        scheduler = new PaymentExpirationScheduler(paymentRepository, orderRepository, productStockService);
    }

    @Test
    void expirePendingPaymentsShouldMarkExpiredAndReleaseStock() {
        Order order = order();
        Payment payment = payment(order);

        when(paymentRepository.findByStatusAndExpiresAtLessThanEqualForUpdate(eq(PaymentStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(payment));
        when(orderRepository.findByIdForUpdate(order.getOrderId())).thenReturn(Optional.of(order));
        when(paymentRepository.save(payment)).thenReturn(payment);

        scheduler.expirePendingPayments();

        assertEquals(PaymentStatus.EXPIRED, payment.getStatus());
        verify(productStockService).releaseReservedStock(order);
        verify(paymentRepository).save(payment);
    }

    private static Order order() {
        return new Order(
                UUID.randomUUID(),
                "prod-abc-123",
                UUID.randomUUID(),
                UUID.randomUUID(),
                2,
                "Jl. Margonda"
        );
    }

    private static Payment payment(Order order) {
        UUID userId = order.getTitipersId();
        Wallet wallet = new Wallet(userId);
        wallet.setId(UUID.randomUUID());
        return new Payment(
                order.getOrderId(),
                userId,
                wallet.getId(),
                "PAY-202605082000-ABCDEF1234567890",
                new BigDecimal("125000"),
                LocalDateTime.now().minusMinutes(1)
        );
    }
}
