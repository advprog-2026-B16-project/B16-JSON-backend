package id.ac.ui.cs.advprog.jsonbackend.features.wallet.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.dto.RefundRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.wallet.model.Refund;

import java.util.List;

public interface RefundService {
    Refund requestRefund(User authenticatedUser, RefundRequest request);

    List<Refund> getMyRefunds(User authenticatedUser);
}
