package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.order.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    private OrderServiceImpl orderService;


    // ── Helper ────────────────────────────────────────────────────────────────

    private void updateTo(String orderId, OrderStatus status) {
        UpdateOrderStatusRequest req = new UpdateOrderStatusRequest();
        req.setNextStatus(status);
        orderService.updateStatus(orderId, req);
    }
}

