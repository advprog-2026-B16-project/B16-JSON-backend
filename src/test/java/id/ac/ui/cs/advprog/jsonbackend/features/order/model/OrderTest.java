package id.ac.ui.cs.advprog.jsonbackend.features.order.model;

import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    private Order order;
    private UUID orderId;
    private UUID titipersId;
    private UUID jastiperId;
    private String productId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        titipersId = UUID.randomUUID();
        jastiperId = UUID.randomUUID();
        productId = "prod-abc-123";

        // Menggunakan custom constructor
        order = new Order(orderId, productId, titipersId, jastiperId, 2, "Kosan Fasilkom");
    }

    @Test
    void testCustomConstructorAndInitialValues() {
        assertEquals(orderId, order.getOrderId());
        assertEquals(productId, order.getProductId());
        assertEquals(titipersId, order.getTitipersId());
        assertEquals(jastiperId, order.getJastiperId());
        assertEquals(2, order.getQuantity());
        assertEquals("Kosan Fasilkom", order.getShippingAddress());

        // Memastikan status awal PENDING dan waktu dicatat
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertNotNull(order.getCreatedAt());
        assertNull(order.getUpdatedAt());
    }

    @Test
    void testUpdateStatusShouldKeepCreatedAtAndRefreshUpdatedAt() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0);
        order.setCreatedAt(createdAt);

        order.updateStatus(OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(createdAt, order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertFalse(order.getUpdatedAt().isBefore(createdAt));
    }

    @Test
    void testCancelShouldSetCancelledStatusAndOverwriteReason() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0);
        order.setCreatedAt(createdAt);
        order.updateStatus(OrderStatus.PAID);

        order.cancel("Barang kosong di toko");

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertEquals("Barang kosong di toko", order.getCancellationReason());
        assertEquals(createdAt, order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertFalse(order.getUpdatedAt().isBefore(createdAt));

        order.cancel("Pembatalan ulang");
        assertEquals("Pembatalan ulang", order.getCancellationReason());
    }

    @Test
    void testSubmitRatingSuccess() {
        // Status harus diubah ke COMPLETED terlebih dahulu agar tidak melempar exception
        order.updateStatus(OrderStatus.COMPLETED);
        LocalDateTime beforeRating = order.getUpdatedAt();
        order.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));

        order.submitRating(5, 4);

        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        assertEquals(5, order.getJastiperRating());
        assertEquals(4, order.getProductRating());
        assertNotNull(order.getUpdatedAt());
        assertFalse(order.getUpdatedAt().isBefore(beforeRating));
        assertEquals(LocalDateTime.of(2026, 1, 1, 10, 0), order.getCreatedAt());
    }

    @Test
    void testSubmitRatingThrowsExceptionIfNotCompleted() {
        // Status masih PENDING atau selain COMPLETED
        order.updateStatus(OrderStatus.SHIPPED);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> order.submitRating(5, 5));

        assertEquals("Rating can only be submitted for COMPLETED orders", exception.getMessage());
    }

    @Test
    void testNoArgsConstructor() {
        Order emptyOrder = new Order();
        assertNull(emptyOrder.getOrderId());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Order fullOrder = new Order(
                orderId, productId, titipersId, jastiperId, 1,
                "Alamat Lengkap", OrderStatus.SHIPPED, now, now,
                5, 5, null
        );

        assertEquals(OrderStatus.SHIPPED, fullOrder.getOrderStatus());
        assertEquals(now, fullOrder.getCreatedAt());
        assertEquals(now, fullOrder.getUpdatedAt());
        assertEquals(5, fullOrder.getJastiperRating());
        assertEquals(5, fullOrder.getProductRating());
        assertNull(fullOrder.getCancellationReason());
    }

    @Test
    void testSettersAndGetters() {
        Order testOrder = new Order();
        LocalDateTime time = LocalDateTime.now();

        testOrder.setOrderId(orderId);
        testOrder.setProductId(productId);
        testOrder.setTitipersId(titipersId);
        testOrder.setJastiperId(jastiperId);
        testOrder.setQuantity(5);
        testOrder.setShippingAddress("Stasiun UI");
        testOrder.setOrderStatus(OrderStatus.PURCHASED);
        testOrder.setCreatedAt(time);
        testOrder.setUpdatedAt(time);
        testOrder.setJastiperRating(4);
        testOrder.setProductRating(3);
        testOrder.setCancellationReason("Habis");

        assertEquals(orderId, testOrder.getOrderId());
        assertEquals(productId, testOrder.getProductId());
        assertEquals(titipersId, testOrder.getTitipersId());
        assertEquals(jastiperId, testOrder.getJastiperId());
        assertEquals(5, testOrder.getQuantity());
        assertEquals("Stasiun UI", testOrder.getShippingAddress());
        assertEquals(OrderStatus.PURCHASED, testOrder.getOrderStatus());
        assertEquals(time, testOrder.getCreatedAt());
        assertEquals(time, testOrder.getUpdatedAt());
        assertEquals(4, testOrder.getJastiperRating());
        assertEquals(3, testOrder.getProductRating());
        assertEquals("Habis", testOrder.getCancellationReason());
    }
}