package id.ac.ui.cs.advprog.jsonbackend.order.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrderRequestTest {

    @Test
    void testSettersAndGetters() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setProductId("prod-001");
        request.setTitipersId("titipers-001");
        request.setJastiperId("jastiper-001");
        request.setQuantity(3);
        request.setShippingAddress("Jl. Kampus No.5");

        assertEquals("prod-001", request.getProductId());
        assertEquals("titipers-001", request.getTitipersId());
        assertEquals("jastiper-001", request.getJastiperId());
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

