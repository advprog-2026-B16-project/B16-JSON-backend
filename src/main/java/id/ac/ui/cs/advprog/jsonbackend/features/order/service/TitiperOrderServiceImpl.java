package id.ac.ui.cs.advprog.jsonbackend.features.order.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.state.OrderStateFactory;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.repository.PaymentRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TitiperOrderServiceImpl implements TitiperOrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final OrderPricingService orderPricingService;

    @Override
    @Transactional
    public OrderResponse confirmDone(User authenticatedUser, UUID orderId) {
        User titiper = requireTitiper(authenticatedUser);
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan!"));

        if (!titiper.getId().equals(order.getTitipersId())) {
            throw new IllegalStateException("Titiper hanya boleh menyelesaikan order miliknya sendiri");
        }

        OrderStateFactory.getState(order.getOrderStatus()).validateTransition(OrderStatus.DONE);
        BigDecimal payoutAmount = resolvePaidAmount(order);

        Wallet jastiperWallet = walletService.findWallet(order.getJastiperId().toString());
        walletService.credit(order.getJastiperId().toString(), payoutAmount);
        Transaction payoutTransaction = transactionService.createTransaction(
                jastiperWallet,
                TransactionType.PAYOUT,
                payoutAmount,
                "Payout for order " + order.getOrderId()
        );
        payoutTransaction.setOrderId(order.getOrderId());
        transactionService.markSuccess(payoutTransaction.getId().toString());

        order.updateStatus(OrderStatus.DONE);
        Order savedOrder = orderRepository.save(order);
        return mapToOrderResponse(savedOrder);
    }

    private User requireTitiper(User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new IllegalStateException("Authentication is required");
        }
        if (authenticatedUser.getRole() != UserRole.TITIPER) {
            throw new IllegalStateException("Only titiper can confirm order done");
        }
        return authenticatedUser;
    }

    private BigDecimal resolvePaidAmount(Order order) {
        return paymentRepository.findFirstByOrderIdAndStatusOrderByCreatedAtDesc(order.getOrderId(), PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .orElseGet(() -> orderPricingService.calculateTotal(order));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalAmount(orderPricingService.calculateTotal(order))
                .shippingAddress(order.getShippingAddress())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .titipersId(order.getTitipersId())
                .jastiperId(order.getJastiperId())
                .jastiperRating(order.getJastiperRating())
                .productRating(order.getProductRating())
                .cancellationReason(order.getCancellationReason())
                .build();
    }
}
