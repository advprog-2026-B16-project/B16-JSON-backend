package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.order.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    private OrderServiceImpl orderService;

    private CreateOrderRequest buildCheckoutRequest(String productId, String titipersId, String jastiperId) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setProductId(productId);
        request.setTitipersId(titipersId);
        request.setJastiperId(jastiperId);
        request.setQuantity(2);
        request.setShippingAddress("Jl. Margonda No.1");
        return request;
    }

    @BeforeEach
    void setUp() {
        StubOrderRepository stubRepository = new StubOrderRepository();
        orderService = new OrderServiceImpl(stubRepository);
    }

    // ── Checkout ──────────────────────────────────────────────────────────────

    @Test
    void testCheckoutCreatesOrderWithPendingStatus() {
        CreateOrderRequest request = buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001");

        OrderResponse response = orderService.checkout(request);

        assertNotNull(response);
        assertEquals(OrderStatus.PENDING, response.getOrderStatus());
    }

    @Test
    void testCheckoutReturnsResponseWithCorrectFields() {
        CreateOrderRequest request = buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001");

        OrderResponse response = orderService.checkout(request);

        assertEquals("prod-001", response.getProductId());
        assertEquals("titipers-001", response.getTitipersId());
        assertEquals("jastiper-001", response.getJastiperId());
        assertEquals(2, response.getQuantity());
        assertEquals("Jl. Margonda No.1", response.getShippingAddress());
    }

    @Test
    void testCheckoutGeneratesUniqueOrderId() {
        CreateOrderRequest request = buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001");

        OrderResponse response1 = orderService.checkout(request);
        OrderResponse response2 = orderService.checkout(request);

        assertNotEquals(response1.getOrderId(), response2.getOrderId());
    }

    @Test
    void testCheckoutSetsCreatedAt() {
        OrderResponse response = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));

        assertNotNull(response.getCreatedAt());
    }

    // ── GetOrderById ──────────────────────────────────────────────────────────

    @Test
    void testGetOrderByIdReturnsCorrectOrder() {
        OrderResponse created = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));

        OrderResponse fetched = orderService.getOrderById(created.getOrderId());

        assertEquals(created.getOrderId(), fetched.getOrderId());
    }

    @Test
    void testGetOrderByIdThrowsWhenNotFound() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.getOrderById("non-existent"));

        assertTrue(ex.getMessage().contains("Order not found"));
    }

    // ── UpdateStatus ──────────────────────────────────────────────────────────

    @Test
    void testUpdateStatusChangesOrderStatus() {
        OrderResponse created = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));

        UpdateOrderStatusRequest req = new UpdateOrderStatusRequest();
        req.setNextStatus(OrderStatus.PAID);

        OrderResponse updated = orderService.updateStatus(created.getOrderId(), req);

        assertEquals(OrderStatus.PAID, updated.getOrderStatus());
    }

    @Test
    void testUpdateStatusSetsUpdatedAt() {
        OrderResponse created = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));

        UpdateOrderStatusRequest req = new UpdateOrderStatusRequest();
        req.setNextStatus(OrderStatus.PAID);

        OrderResponse updated = orderService.updateStatus(created.getOrderId(), req);

        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void testUpdateStatusThrowsWhenOrderNotFound() {
        UpdateOrderStatusRequest req = new UpdateOrderStatusRequest();
        req.setNextStatus(OrderStatus.PAID);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateStatus("non-existent", req));
    }

    // ── CancelOrder ───────────────────────────────────────────────────────────

    @Test
    void testCancelOrderSetsStatusToCancelled() {
        OrderResponse created = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));

        OrderResponse cancelled = orderService.cancelOrder(created.getOrderId(), "Out of stock");

        assertEquals(OrderStatus.CANCELLED, cancelled.getOrderStatus());
    }

    @Test
    void testCancelOrderSetsCancellationReason() {
        OrderResponse created = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));

        OrderResponse cancelled = orderService.cancelOrder(created.getOrderId(), "Jastiper batal berangkat");

        assertEquals("Jastiper batal berangkat", cancelled.getCancellationReason());
    }

    @Test
    void testCancelOrderThrowsWhenOrderNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.cancelOrder("non-existent", "reason"));
    }

    // ── SubmitRating ──────────────────────────────────────────────────────────

    @Test
    void testSubmitRatingSucceedsWhenCompleted() {
        OrderResponse created = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));

        // Advance to COMPLETED via valid transitions
        String orderId = created.getOrderId();
        updateTo(orderId, OrderStatus.PAID);
        updateTo(orderId, OrderStatus.PURCHASED);
        updateTo(orderId, OrderStatus.SHIPPED);
        updateTo(orderId, OrderStatus.COMPLETED);

        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setJastiperRating(5);
        ratingRequest.setProductRating(4);

        OrderResponse rated = orderService.submitRating(orderId, ratingRequest);

        assertEquals(5, rated.getJastiperRating());
        assertEquals(4, rated.getProductRating());
    }

    @Test
    void testSubmitRatingThrowsWhenOrderNotCompleted() {
        OrderResponse created = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));

        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setJastiperRating(5);
        ratingRequest.setProductRating(4);

        assertThrows(IllegalStateException.class,
                () -> orderService.submitRating(created.getOrderId(), ratingRequest));
    }

    @Test
    void testSubmitRatingThrowsWhenOrderNotFound() {
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setJastiperRating(5);
        ratingRequest.setProductRating(4);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.submitRating("non-existent", ratingRequest));
    }

    // ── GetOrdersByTitipersId ─────────────────────────────────────────────────

    @Test
    void testGetOrdersByTitipersIdReturnsCorrectOrders() {
        orderService.checkout(buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));
        orderService.checkout(buildCheckoutRequest("prod-002", "titipers-001", "jastiper-001"));
        orderService.checkout(buildCheckoutRequest("prod-003", "titipers-002", "jastiper-001"));

        List<OrderResponse> result = orderService.getOrdersByTitipersId("titipers-001");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(o -> "titipers-001".equals(o.getTitipersId())));
    }

    @Test
    void testGetOrdersByTitipersIdReturnsEmptyWhenNoMatch() {
        List<OrderResponse> result = orderService.getOrdersByTitipersId("titipers-999");
        assertTrue(result.isEmpty());
    }

    // ── GetOrdersByJastiperId ─────────────────────────────────────────────────

    @Test
    void testGetOrdersByJastiperIdReturnsCorrectOrders() {
        orderService.checkout(buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));
        orderService.checkout(buildCheckoutRequest("prod-002", "titipers-002", "jastiper-002"));

        List<OrderResponse> result = orderService.getOrdersByJastiperId("jastiper-001");

        assertEquals(1, result.size());
        assertEquals("jastiper-001", result.getFirst().getJastiperId());
    }

    @Test
    void testGetOrdersByJastiperIdReturnsEmptyWhenNoMatch() {
        List<OrderResponse> result = orderService.getOrdersByJastiperId("jastiper-999");
        assertTrue(result.isEmpty());
    }

    // ── GetOrdersByJastiperIdAndStatus ────────────────────────────────────────

    @Test
    void testGetOrdersByJastiperIdAndStatusFiltersCorrectly() {
        OrderResponse o1 = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));
        OrderResponse o2 = orderService.checkout(
                buildCheckoutRequest("prod-002", "titipers-002", "jastiper-001"));
        updateTo(o2.getOrderId(), OrderStatus.PAID);

        List<OrderResponse> pending = orderService.getOrdersByJastiperIdAndStatus(
                "jastiper-001", OrderStatus.PENDING);

        assertEquals(1, pending.size());
        assertEquals(o1.getOrderId(), pending.getFirst().getOrderId());
    }

    @Test
    void testGetOrdersByJastiperIdAndStatusReturnsEmptyWhenNoMatch() {
        List<OrderResponse> result = orderService.getOrdersByJastiperIdAndStatus(
                "jastiper-001", OrderStatus.COMPLETED);
        assertTrue(result.isEmpty());
    }

    // ── GetAllOrders ──────────────────────────────────────────────────────────

    @Test
    void testGetAllOrdersReturnsAllOrders() {
        orderService.checkout(buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));
        orderService.checkout(buildCheckoutRequest("prod-002", "titipers-002", "jastiper-002"));

        List<OrderResponse> all = orderService.getAllOrders();

        assertEquals(2, all.size());
    }

    @Test
    void testGetAllOrdersReturnsEmptyWhenNoOrders() {
        List<OrderResponse> all = orderService.getAllOrders();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    // ── GetOrdersByStatus ─────────────────────────────────────────────────────

    @Test
    void testGetOrdersByStatusReturnsCorrectOrders() {
        OrderResponse o1 = orderService.checkout(
                buildCheckoutRequest("prod-001", "titipers-001", "jastiper-001"));
        orderService.checkout(buildCheckoutRequest("prod-002", "titipers-002", "jastiper-001"));
        updateTo(o1.getOrderId(), OrderStatus.PAID);

        List<OrderResponse> paid = orderService.getOrdersByStatus(OrderStatus.PAID);
        List<OrderResponse> pending = orderService.getOrdersByStatus(OrderStatus.PENDING);

        assertEquals(1, paid.size());
        assertEquals(1, pending.size());
    }

    @Test
    void testGetOrdersByStatusReturnsEmptyWhenNoMatch() {
        List<OrderResponse> result = orderService.getOrdersByStatus(OrderStatus.COMPLETED);
        assertTrue(result.isEmpty());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void updateTo(String orderId, OrderStatus status) {
        UpdateOrderStatusRequest req = new UpdateOrderStatusRequest();
        req.setNextStatus(status);
        orderService.updateStatus(orderId, req);
    }
}

