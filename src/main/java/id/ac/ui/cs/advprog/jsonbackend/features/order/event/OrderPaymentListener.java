package id.ac.ui.cs.advprog.jsonbackend.features.order.event;

import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderPaymentListener {

    private final OrderRepository orderRepository;

    @EventListener
    @Transactional
    public void handlePaymentResult(PaymentResultEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan saat memproses pembayaran!"));

        if (event.isSuccess()) {
            order.updateStatus(OrderStatus.PAID);
        } else {
            order.cancel("Pembayaran gagal: " + event.errorMessage());
        }

        orderRepository.save(order);
    }
}
