package id.ac.ui.cs.advprog.jsonbackend.features.payment.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.dto.PaymentRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.exception.PaymentNotAllowedException;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.exception.PaymentNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.exception.PaymentUnauthorizedException;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.InsufficientBalanceException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletTransactionService;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.order.service.OrderPricingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final int EXPIRATION_MINUTES = 15;
    private static final DateTimeFormatter REFERENCE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final Set<PaymentStatus> ACTIVE_OR_SUCCESS_STATUS = Set.of(PaymentStatus.PENDING, PaymentStatus.SUCCESS);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderPricingService orderPricingService;
    private final WalletService walletService;
    private final WalletTransactionService walletTransactionService;
    private final SecureRandom secureRandom = new SecureRandom();

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            OrderPricingService orderPricingService,
            WalletService walletService,
            WalletTransactionService walletTransactionService
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderPricingService = orderPricingService;
        this.walletService = walletService;
        this.walletTransactionService = walletTransactionService;
    }

    @Override
    public Payment createPayment(User authenticatedUser, PaymentRequest request) {
        User user = requireAuthenticatedUser(authenticatedUser);
        Order order = orderRepository.findByIdForUpdate(request.getOrderId())
                .orElseThrow(() -> new PaymentNotAllowedException("Order not found"));

        validateOrderOwner(user, order);
        validateOrderPending(order);

        LocalDateTime now = LocalDateTime.now();
        Payment existingPayment = paymentRepository
                .findFirstByOrderIdAndStatusInOrderByCreatedAtDesc(order.getOrderId(), ACTIVE_OR_SUCCESS_STATUS)
                .orElse(null);

        if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.SUCCESS) {
            throw new PaymentNotAllowedException("Order has already been paid");
        }
        if (existingPayment != null && !existingPayment.isExpired(now)) {
            return existingPayment;
        }
        if (existingPayment != null) {
            existingPayment.markExpired();
            paymentRepository.save(existingPayment);
        }

        Wallet wallet = walletService.findWallet(user.getId().toString());
        BigDecimal amount = orderPricingService.calculateTotal(order);
        Payment payment = new Payment(
                order.getOrderId(),
                user.getId(),
                wallet.getId(),
                generateReferenceCode(),
                amount,
                now.plusMinutes(EXPIRATION_MINUTES)
        );

        return paymentRepository.save(payment);
    }

    @Override
    public Payment pay(User authenticatedUser, String referenceCode) {
        User user = requireAuthenticatedUser(authenticatedUser);
        Payment payment = paymentRepository.findByReferenceCodeForUpdate(referenceCode)
                .orElseThrow(() -> new PaymentNotFoundException(referenceCode));

        if (!payment.getUserId().equals(user.getId())) {
            throw new PaymentUnauthorizedException("You can only pay your own payment reference");
        }
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return payment;
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentNotAllowedException("Payment reference is not payable");
        }
        if (payment.isExpired(LocalDateTime.now())) {
            payment.markExpired();
            return paymentRepository.save(payment);
        }

        Order order = orderRepository.findByIdForUpdate(payment.getOrderId())
                .orElseThrow(() -> new PaymentNotAllowedException("Order not found"));
        validateOrderOwner(user, order);
        validateOrderPending(order);

        try {
            Transaction transaction = walletTransactionService.requestPayment(
                    user.getId().toString(),
                    order.getOrderId().toString(),
                    payment.getAmount()
            );
            order.updateStatus(OrderStatus.PAID);
            payment.markSuccess(transaction.getId());
            return paymentRepository.save(payment);
        } catch (InsufficientBalanceException ex) {
            throw ex;
        } catch (Exception ex) {
            payment.markFailed();
            paymentRepository.save(payment);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getMyPayments(User authenticatedUser) {
        User user = requireAuthenticatedUser(authenticatedUser);
        return paymentRepository.findByUserId(user.getId());
    }

    private User requireAuthenticatedUser(User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new PaymentUnauthorizedException("Authentication is required for payment");
        }
        return authenticatedUser;
    }

    private void validateOrderOwner(User user, Order order) {
        if (!order.getTitipersId().equals(user.getId())) {
            throw new PaymentUnauthorizedException("You can only pay your own order");
        }
    }

    private void validateOrderPending(Order order) {
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new PaymentNotAllowedException("Only pending orders can be paid");
        }
    }

    private String generateReferenceCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            byte[] randomBytes = new byte[8];
            secureRandom.nextBytes(randomBytes);
            String code = "PAY-" + LocalDateTime.now().format(REFERENCE_TIME_FORMAT) + "-" + HexFormat.of().formatHex(randomBytes).toUpperCase();
            if (!paymentRepository.existsByReferenceCode(code)) {
                return code;
            }
        }
        throw new PaymentNotAllowedException("Unable to generate payment reference");
    }
}
