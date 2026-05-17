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
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.repository.RefundRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final TransactionService transactionService;
    private final WalletService walletService;

    public RefundServiceImpl(
            RefundRepository refundRepository,
            OrderRepository orderRepository,
            TransactionService transactionService,
            WalletService walletService
    ) {
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    @Override
    public Refund requestRefund(User authenticatedUser, RefundRequest request) {
        if (authenticatedUser == null) {
            throw new WalletUnauthorizedException("Authentication is required to request a refund");
        }

        Transaction originalTransaction = transactionService.getTransactionByIdForUpdate(request.getTransactionId().toString());
        validateRefundRequest(authenticatedUser, originalTransaction);

        Refund refund = refundRepository.save(new Refund(originalTransaction, request.getReason()));

        Transaction refundTransaction = transactionService.createTransaction(
                walletService.findWallet(authenticatedUser.getId().toString()),
                TransactionType.REFUND,
                originalTransaction.getAmount(),
                buildDescription(originalTransaction, request.getReason())
        );
        refundTransaction.setOrderId(originalTransaction.getOrderId());

        try {
            walletService.credit(authenticatedUser.getId().toString(), originalTransaction.getAmount());
            transactionService.markSuccess(refundTransaction.getId().toString());
            refund.markSuccess(refundTransaction.getId());
            return refundRepository.save(refund);
        } catch (Exception ex) {
            transactionService.markFailed(refundTransaction.getId().toString());
            refund.markFailed();
            refundRepository.save(refund);
            throw ex;
        }
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
        validateOrderIsCancelled(originalTransaction);
    }

    private void validateOrderIsCancelled(Transaction originalTransaction) {
        UUID orderId = originalTransaction.getOrderId();
        if (orderId == null) {
            throw new RefundNotAllowedException("Payment transaction is not linked to an order");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RefundNotAllowedException("Order linked to payment transaction was not found"));

        if (order.getOrderStatus() != OrderStatus.CANCELLED) {
            throw new RefundNotAllowedException("Order must be cancelled before refund can be requested");
        }
    }

    private String buildDescription(Transaction originalTransaction, String reason) {
        String description = "Refund for transaction " + originalTransaction.getId();
        if (reason == null || reason.isBlank()) {
            return description;
        }
        return description + ": " + reason.trim();
    }
}
