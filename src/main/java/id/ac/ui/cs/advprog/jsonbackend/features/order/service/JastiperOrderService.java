package id.ac.ui.cs.advprog.jsonbackend.features.order.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;

import java.util.List;
import java.util.UUID;

public interface JastiperOrderService {
    List<Order> getMyOrders(User authenticatedUser);

    OrderResponse markAsShipped(User authenticatedUser, UUID orderId);

    OrderResponse markAsCompleted(User authenticatedUser, UUID orderId);
}
