package id.ac.ui.cs.advprog.jsonbackend.features.order.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RatingRequestTest {

    @Test
    void testSettersAndGetters() {
        RatingRequest request = new RatingRequest();
        request.setJastiperRating(5);
        request.setProductRating(4);

        assertEquals(5, request.getJastiperRating());
        assertEquals(4, request.getProductRating());
    }

    @Test
    void testRatingsCanBeClearedBackToNull() {
        RatingRequest request = new RatingRequest();
        request.setJastiperRating(5);
        request.setProductRating(4);

        request.setJastiperRating(null);
        request.setProductRating(null);

        assertNull(request.getJastiperRating());
        assertNull(request.getProductRating());
    }

    @Test
    void testDefaultFieldsAreNull() {
        RatingRequest request = new RatingRequest();
        assertNull(request.getJastiperRating());
        assertNull(request.getProductRating());
    }
}
