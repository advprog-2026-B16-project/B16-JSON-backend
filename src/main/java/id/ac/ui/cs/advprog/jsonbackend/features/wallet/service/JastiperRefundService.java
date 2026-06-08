package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;

import java.util.List;
import java.util.UUID;

public interface JastiperRefundService {
    List<Refund> getMyRefunds(User authenticatedUser);
    Refund approveRefund(User authenticatedUser, UUID refundId);
    Refund rejectRefund(User authenticatedUser, UUID refundId);
}
