package id.ac.ui.cs.advprog.jsonbackend.features.order.event;

import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderCatalogListener {

    private final OrderRepository orderRepository;

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleStockResult(StockResultEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan saat memproses stok!"));


        if (!event.isSuccess()) {
            order.cancel("Sistem gagal memproses pesanan: " + event.errorMessage());
            orderRepository.save(order);
        }
    }
}
