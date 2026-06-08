package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JastiperRefundServiceImpl implements JastiperRefundService {

    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final WalletService walletService;
    private final TransactionService transactionService;

    @Override
    @Transactional(readOnly = true)
    public List<Refund> getMyRefunds(User authenticatedUser) {
        User jastiper = requireJastiper(authenticatedUser);
        return refundRepository.findByJastiperId(jastiper.getId());
    }

    @Override
    @Transactional
    public Refund approveRefund(User authenticatedUser, UUID refundId) {
        User jastiper = requireJastiper(authenticatedUser);
        Refund refund = refundRepository.findByIdForUpdate(refundId)
                .orElseThrow(() -> new RefundNotAllowedException("Refund request was not found"));

        if (refund.getStatus() != TransactionStatus.PENDING) {
            throw new RefundNotAllowedException("Only pending refund can be approved");
        }

        Order order = requireOwnedOrder(jastiper, refund);
        if (order.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new RefundNotAllowedException("Refund can only be approved before order is done");
        }

        Wallet titiperWallet = walletService.findWallet(refund.getRequesterId().toString());
        walletService.credit(refund.getRequesterId().toString(), refund.getAmount());

        Transaction refundTransaction = transactionService.createTransaction(
                titiperWallet,
                TransactionType.REFUND,
                refund.getAmount(),
                buildDescription(refund)
        );
        refundTransaction.setOrderId(refund.getOrderId());
        transactionService.markSuccess(refundTransaction.getId().toString());

        refund.markSuccess(refundTransaction.getId());
        order.updateStatus(OrderStatus.DONE);
        orderRepository.save(order);
        return refundRepository.save(refund);
    }

    @Override
    @Transactional
    public Refund rejectRefund(User authenticatedUser, UUID refundId) {
        User jastiper = requireJastiper(authenticatedUser);
        Refund refund = refundRepository.findByIdForUpdate(refundId)
                .orElseThrow(() -> new RefundNotAllowedException("Refund request was not found"));

        if (refund.getStatus() != TransactionStatus.PENDING) {
            throw new RefundNotAllowedException("Only pending refund can be rejected");
        }

        Order order = requireOwnedOrder(jastiper, refund);
        refund.markFailed();
        releasePayoutToJastiper(jastiper, order, refund);
        return refundRepository.save(refund);
    }

    private Order requireOwnedOrder(User jastiper, Refund refund) {
        Order order = orderRepository.findByIdForUpdate(refund.getOrderId())
                .orElseThrow(() -> new RefundNotAllowedException("Order linked to refund was not found"));

        if (!jastiper.getId().equals(order.getJastiperId())) {
            throw new RefundNotAllowedException("Jastiper can only process refund for their own order");
        }
        return order;
    }

    private User requireJastiper(User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new RefundNotAllowedException("Authentication is required");
        }
        if (authenticatedUser.getRole() != UserRole.JASTIPER) {
            throw new RefundNotAllowedException("Only jastiper can approve refund");
        }
        return authenticatedUser;
    }

    private void releasePayoutToJastiper(User jastiper, Order order, Refund refund) {
        if (order.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new RefundNotAllowedException("Refund can only be rejected before order is done");
        }

        Wallet jastiperWallet = walletService.findWallet(jastiper.getId().toString());
        walletService.credit(jastiper.getId().toString(), refund.getAmount());

        Transaction payoutTransaction = transactionService.createTransaction(
                jastiperWallet,
                TransactionType.PAYOUT,
                refund.getAmount(),
                "Payout for rejected refund " + refund.getId()
        );
        payoutTransaction.setOrderId(order.getOrderId());
        transactionService.markSuccess(payoutTransaction.getId().toString());

        order.updateStatus(OrderStatus.DONE);
        orderRepository.save(order);
    }

    private String buildDescription(Refund refund) {
        String description = "Refund for transaction " + refund.getOriginalTransactionId();
        if (refund.getReason() == null || refund.getReason().isBlank()) {
            return description;
        }
        return description + ": " + refund.getReason();
    }
}
