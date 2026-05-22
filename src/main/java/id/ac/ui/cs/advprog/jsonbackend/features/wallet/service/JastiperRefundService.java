package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;

import java.util.UUID;

public interface JastiperRefundService {
    Refund approveRefund(User authenticatedUser, UUID refundId);
}
