package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.RefundRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.enums.TransactionType;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.RefundAlreadyExistsException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.RefundNotAllowedException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.exception.WalletUnauthorizedException;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.model.Transaction;
import id.ac.ui.cs.advprog.jsonbackend.features.transaction.service.TransactionService;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.RefundRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private static final int REFUND_WINDOW_DAYS = 3;

    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final TransactionService transactionService;

    public RefundServiceImpl(
            RefundRepository refundRepository,
            OrderRepository orderRepository,
            TransactionService transactionService
    ) {
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.transactionService = transactionService;
    }

    @Override
    public Refund requestRefund(User authenticatedUser, RefundRequest request) {
        if (authenticatedUser == null) {
            throw new WalletUnauthorizedException("Authentication is required to request a refund");
        }

        Transaction originalTransaction = transactionService.getTransactionByIdForUpdate(request.getTransactionId().toString());
        validateRefundRequest(authenticatedUser, originalTransaction);

        return refundRepository.save(new Refund(originalTransaction, request.getReason()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Refund> getMyRefunds(User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new WalletUnauthorizedException("Authentication is required to view refunds");
        }
        return refundRepository.findByRequesterId(authenticatedUser.getId());
    }

    private void validateRefundRequest(User authenticatedUser, Transaction originalTransaction) {
        UUID authenticatedUserId = authenticatedUser.getId();

        if (!originalTransaction.getUserId().equals(authenticatedUserId)) {
            throw new WalletUnauthorizedException("You can only refund your own transaction");
        }
        if (originalTransaction.getType() != TransactionType.PAYMENT) {
            throw new RefundNotAllowedException("Only payment transactions can be refunded");
        }
        if (originalTransaction.getStatus() != TransactionStatus.SUCCESS) {
            throw new RefundNotAllowedException("Only successful payment transactions can be refunded");
        }
        if (originalTransaction.getAmount() == null || originalTransaction.getAmount().signum() <= 0) {
            throw new RefundNotAllowedException("Refund amount must be greater than zero");
        }
        if (refundRepository.existsByOriginalTransactionId(originalTransaction.getId())) {
            throw new RefundAlreadyExistsException(originalTransaction.getId());
        }
        validateOrderIsEligibleForRefund(originalTransaction);
    }

    private void validateOrderIsEligibleForRefund(Transaction originalTransaction) {
        UUID orderId = originalTransaction.getOrderId();
        if (orderId == null) {
            throw new RefundNotAllowedException("Payment transaction is not linked to an order");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RefundNotAllowedException("Order linked to payment transaction was not found"));

        if (order.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new RefundNotAllowedException("Order must be completed before refund can be requested");
        }

        LocalDateTime completedAt = order.getUpdatedAt();
        if (completedAt == null) {
            throw new RefundNotAllowedException("Completed order timestamp is missing");
        }
        if (completedAt.plusDays(REFUND_WINDOW_DAYS).isBefore(LocalDateTime.now())) {
            throw new RefundNotAllowedException("Refund can only be requested within 3 days after order completion");
        }
    }
}
