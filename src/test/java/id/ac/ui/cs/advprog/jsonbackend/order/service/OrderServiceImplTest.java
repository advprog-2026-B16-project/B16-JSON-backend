package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.order.dto.CreateOrderRequest;
import id.ac.ui.cs.advprog.jsonbackend.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.order.dto.RatingRequest;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderServiceImplTest {

    @Test
    void updateOrderStatusSignatureShouldMatchContract() throws NoSuchMethodException {
        Method method = OrderServiceImpl.class.getMethod("updateOrderStatus", UUID.class, OrderStatus.class);
        assertNotNull(method);
        assertEquals(OrderResponse.class, method.getReturnType());
    }

    @Test
    void checkoutSignatureShouldMatchContract() throws NoSuchMethodException {
        Method method = OrderServiceImpl.class.getMethod("checkout", CreateOrderRequest.class);
        assertNotNull(method);
        assertEquals(OrderResponse.class, method.getReturnType());
    }

    @Test
    void queryMethodsShouldExposeExpectedReturnTypes() throws NoSuchMethodException {
        Method getAllOrder = OrderServiceImpl.class.getMethod("getAllOrder");
        Method getOrderByStatus = OrderServiceImpl.class.getMethod("getOrderByStatus", OrderStatus.class);
        Method getOrderByOrderIdAndStatus = OrderServiceImpl.class.getMethod(
                "getOrderByOrderIdAndStatus", UUID.class, OrderStatus.class
        );
        Method submitRating = OrderServiceImpl.class.getMethod("submitRating", UUID.class, RatingRequest.class);

        assertEquals(List.class, getAllOrder.getReturnType());
        assertEquals(List.class, getOrderByStatus.getReturnType());
        assertEquals(Optional.class, getOrderByOrderIdAndStatus.getReturnType());
        assertEquals(OrderResponse.class, submitRating.getReturnType());
    }

    @Test
    void cancelOrderSignatureShouldAcceptStringReason() throws NoSuchMethodException {
        Method cancelOrder = OrderServiceImpl.class.getMethod("cancelOrder", String.class, String.class);
        assertNotNull(cancelOrder);
        assertEquals(OrderResponse.class, cancelOrder.getReturnType());
    }
}
