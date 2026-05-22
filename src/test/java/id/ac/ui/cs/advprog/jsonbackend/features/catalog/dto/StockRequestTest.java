package id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StockRequestTest {

    @Test
    void quantityShouldBeMutablePublicField() {
        StockRequest request = new StockRequest();
        request.quantity = 3;

        assertEquals(3, request.quantity);
    }
}
