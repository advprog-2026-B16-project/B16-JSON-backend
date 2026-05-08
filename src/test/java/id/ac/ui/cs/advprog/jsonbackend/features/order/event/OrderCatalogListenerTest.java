package id.ac.ui.cs.advprog.jsonbackend.features.order.event;

import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCatalogListenerTest {

    private static final UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TITIPERS_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID JASTIPER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private OrderRepository orderRepository;

    private OrderCatalogListener listener;

    @BeforeEach
    void setUp() {
        listener = new OrderCatalogListener(orderRepository);
    }

    @Test
    void handleStockResultShouldNotSaveWhenSuccess() {
        Order order = order();
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        listener.handleStockResult(new StockResultEvent(ORDER_ID, true, null));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void handleStockResultShouldCancelOrderWhenFailed() {
        Order order = order();
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        listener.handleStockResult(new StockResultEvent(ORDER_ID, false, "Stock empty"));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertEquals(OrderStatus.CANCELLED, captor.getValue().getOrderStatus());
        assertEquals("Sistem gagal memproses pesanan: Stock empty", captor.getValue().getCancellationReason());
    }

    @Test
    void handleStockResultShouldThrowWhenOrderMissing() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> listener.handleStockResult(new StockResultEvent(ORDER_ID, false, "Stock empty")));

        assertEquals("Order tidak ditemukan saat memproses stok!", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    private static Order order() {
        return new Order(ORDER_ID, "prod-abc-123", TITIPERS_ID, JASTIPER_ID, 2, "Jl. Margonda Raya No. 1");
    }
}

