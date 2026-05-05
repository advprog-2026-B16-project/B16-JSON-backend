package id.ac.ui.cs.advprog.jsonbackend.order.dto;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateOrderStatusRequestTest {

    @Test
    void testSettersAndGetters() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setNextStatus(OrderStatus.PAID);
        request.setCancellationReason("Out of stock");

        assertEquals(OrderStatus.PAID, request.getNextStatus());
        assertEquals("Out of stock", request.getCancellationReason());
    }

    @Test
    void testDefaultFieldsAreNull() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        assertNull(request.getNextStatus());
        assertNull(request.getCancellationReason());
    }

    @Test
    void testSetNextStatusToCancelled() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setNextStatus(OrderStatus.CANCELLED);
        assertEquals(OrderStatus.CANCELLED, request.getNextStatus());
    }
}

