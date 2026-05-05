package id.ac.ui.cs.advprog.jsonbackend.order.dto;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreateOrderRequestTest {

    @Test
    void testSettersAndGetters() {
        CreateOrderRequest request = new CreateOrderRequest();
        UUID titipersId = UUID.randomUUID();
        UUID jastiperId = UUID.randomUUID();

        request.setProductId("prod-001");
        request.setTitipersId(titipersId);
        request.setJastiperId(jastiperId);
        request.setQuantity(3);
        request.setShippingAddress("Jl. Kampus No.5");

        assertEquals("prod-001", request.getProductId());
        assertEquals(titipersId, request.getTitipersId());
        assertEquals(jastiperId, request.getJastiperId());
        assertEquals(3, request.getQuantity());
        assertEquals("Jl. Kampus No.5", request.getShippingAddress());
    }

    @Test
    void testDefaultQuantityIsZero() {
        CreateOrderRequest request = new CreateOrderRequest();
        assertEquals(0, request.getQuantity());
    }

    @Test
    void testDefaultFieldsAreNull() {
        CreateOrderRequest request = new CreateOrderRequest();
        assertNull(request.getProductId());
        assertNull(request.getTitipersId());
        assertNull(request.getJastiperId());
        assertNull(request.getShippingAddress());
    }
}
