package id.ac.ui.cs.advprog.jsonbackend.order.state;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStateFactoryTest {

    @Test
    void testGetStateReturnsPendingState() {
        OrderState state = OrderStateFactory.getState(OrderStatus.PENDING);
        assertInstanceOf(PendingState.class, state);
    }

    @Test
    void testGetStateReturnsPaidState() {
        OrderState state = OrderStateFactory.getState(OrderStatus.PAID);
        assertInstanceOf(PaidState.class, state);
    }

    @Test
    void testGetStateReturnsPurchasedState() {
        OrderState state = OrderStateFactory.getState(OrderStatus.PURCHASED);
        assertInstanceOf(PurchasedState.class, state);
    }

    @Test
    void testGetStateReturnsShippedState() {
        OrderState state = OrderStateFactory.getState(OrderStatus.SHIPPED);
        assertInstanceOf(ShippedState.class, state);
    }

    @Test
    void testGetStateReturnsCompletedState() {
        OrderState state = OrderStateFactory.getState(OrderStatus.COMPLETED);
        assertInstanceOf(CompletedState.class, state);
    }

    @Test
    void testGetStateReturnsCancelledState() {
        OrderState state = OrderStateFactory.getState(OrderStatus.CANCELLED);
        assertInstanceOf(CancelledState.class, state);
    }

    @Test
    void testGetStateReturnsCorrectStatusForEachEnum() {
        for (OrderStatus status : OrderStatus.values()) {
            OrderState state = OrderStateFactory.getState(status);
            assertEquals(status, state.getStatus());
        }
    }

    @Test
    void testOrderStateFactoryConstructorThrows() {
        assertThrows(UnsupportedOperationException.class, () -> {
            var constructor = OrderStateFactory.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testGetStateThrowsIllegalArgumentExceptionWhenStatusIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> OrderStateFactory.getState(unknownStatus()));

        assertEquals("Unknown order status: null", ex.getMessage());
    }

    /** Returns null as an unknown OrderStatus to trigger the null-guard in OrderStateFactory. */
    private OrderStatus unknownStatus() {
        return null;
    }
}

