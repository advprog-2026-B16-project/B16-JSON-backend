package id.ac.ui.cs.advprog.jsonbackend.features.payment.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.dto.PaymentRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;

import java.util.List;

public interface PaymentService {
    Payment createPayment(User authenticatedUser, PaymentRequest request);

    Payment pay(User authenticatedUser, String referenceCode);

    List<Payment> getMyPayments(User authenticatedUser);
}
