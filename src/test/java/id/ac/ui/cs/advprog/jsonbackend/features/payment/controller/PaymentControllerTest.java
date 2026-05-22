package id.ac.ui.cs.advprog.jsonbackend.features.payment.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.dto.PaymentRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.enums.PaymentStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.model.Payment;
import id.ac.ui.cs.advprog.jsonbackend.features.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentControllerTest {

    private PaymentService paymentService;
    private PaymentController controller;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        controller = new PaymentController(paymentService);
    }

    @Test
    void controllerShouldDelegatePaymentOperations() {
        User user = user();
        Payment payment = payment(user.getId());
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(payment.getOrderId());

        when(paymentService.createPayment(user, request)).thenReturn(payment);
        when(paymentService.pay(user, payment.getReferenceCode())).thenReturn(payment);
        when(paymentService.cancelPayment(user, payment.getReferenceCode())).thenReturn(payment);
        when(paymentService.getMyPayments(user)).thenReturn(List.of(payment));

        assertEquals(payment.getReferenceCode(), controller.createPayment(user, request).getBody().getReferenceCode());
        assertEquals(PaymentStatus.PENDING, controller.pay(user, payment.getReferenceCode()).getBody().getStatus());
        assertEquals(payment.getId(), controller.cancelPayment(user, payment.getReferenceCode()).getBody().getId());
        ResponseEntity<?> myPayments = controller.getMyPayments(user);
        assertEquals(200, myPayments.getStatusCode().value());

        verify(paymentService).createPayment(user, request);
        verify(paymentService).pay(user, payment.getReferenceCode());
        verify(paymentService).cancelPayment(user, payment.getReferenceCode());
        verify(paymentService).getMyPayments(user);
    }

    private static User user() {
        return User.builder()
                .id(UUID.randomUUID())
                .username("titiper")
                .email("titiper@example.com")
                .password("secret")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private static Payment payment(UUID userId) {
        Payment payment = new Payment(
                UUID.randomUUID(),
                userId,
                UUID.randomUUID(),
                "PAY-REF",
                new BigDecimal("125000"),
                LocalDateTime.now().plusMinutes(15)
        );
        payment.setId(UUID.randomUUID());
        return payment;
    }
}
