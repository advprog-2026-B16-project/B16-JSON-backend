package id.ac.ui.cs.advprog.jsonbackend.features.order.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;

import java.util.UUID;

public interface TitiperOrderService {
    OrderResponse confirmDone(User authenticatedUser, UUID orderId);
}
