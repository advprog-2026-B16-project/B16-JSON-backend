package id.ac.ui.cs.advprog.jsonbackend.features.payment.service;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.service.ProductStockService;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentExpirationScheduler {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductStockService productStockService;

    @Scheduled(fixedDelayString = "${payment.expiration-scan-delay-ms:60000}")
    @Transactional
    public void expirePendingPayments() {
        List<Payment> expiredPayments = paymentRepository.findByStatusAndExpiresAtLessThanEqualForUpdate(
                PaymentStatus.PENDING,
                LocalDateTime.now()
        );

        for (Payment payment : expiredPayments) {
            orderRepository.findByIdForUpdate(payment.getOrderId()).ifPresent(order -> expirePayment(payment, order));
        }
    }

    private void expirePayment(Payment payment, Order order) {
        payment.markExpired();
        productStockService.releaseReservedStock(order);
        paymentRepository.save(payment);
    }
}
