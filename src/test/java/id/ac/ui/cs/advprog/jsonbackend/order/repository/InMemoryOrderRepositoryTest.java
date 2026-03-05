package id.ac.ui.cs.advprog.jsonbackend.order.repository;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;

    private Order makeOrder(String orderId, String titipersId, String jastiperId) {
        return new Order(orderId, "product-001", titipersId, jastiperId, 1, "Jl. Test No.1");
    }

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
    }

    @Test
    void testSaveAndFindById() {
        Order order = makeOrder("order-001", "titipers-001", "jastiper-001");
        repository.save(order);

        Optional<Order> result = repository.findById("order-001");

        assertTrue(result.isPresent());
        assertEquals("order-001", result.get().getOrderId());
    }

    @Test
    void testFindByIdReturnsEmptyWhenNotFound() {
        Optional<Order> result = repository.findById("non-existent");
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveOverwritesExistingOrder() {
        Order order = makeOrder("order-001", "titipers-001", "jastiper-001");
        repository.save(order);
        order.updateStatus(OrderStatus.PAID);
        repository.save(order);

        Optional<Order> result = repository.findById("order-001");

        assertTrue(result.isPresent());
        assertEquals(OrderStatus.PAID, result.get().getOrderStatus());
    }

    @Test
    void testFindAllReturnsAllSavedOrders() {
        repository.save(makeOrder("order-001", "titipers-001", "jastiper-001"));
        repository.save(makeOrder("order-002", "titipers-002", "jastiper-001"));

        List<Order> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void testFindAllReturnsEmptyListWhenNoOrders() {
        List<Order> all = repository.findAll();
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void testFindAllByTitipersId() {
        repository.save(makeOrder("order-001", "titipers-001", "jastiper-001"));
        repository.save(makeOrder("order-002", "titipers-001", "jastiper-001"));
        repository.save(makeOrder("order-003", "titipers-002", "jastiper-001"));

        List<Order> result = repository.findAllByTitipersId("titipers-001");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(o -> "titipers-001".equals(o.getTitipersId())));
    }

    @Test
    void testFindAllByTitipersIdReturnsEmptyWhenNoMatch() {
        repository.save(makeOrder("order-001", "titipers-001", "jastiper-001"));

        List<Order> result = repository.findAllByTitipersId("titipers-999");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllByJastiperId() {
        repository.save(makeOrder("order-001", "titipers-001", "jastiper-001"));
        repository.save(makeOrder("order-002", "titipers-002", "jastiper-001"));
        repository.save(makeOrder("order-003", "titipers-003", "jastiper-002"));

        List<Order> result = repository.findAllByJastiperId("jastiper-001");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(o -> "jastiper-001".equals(o.getJastiperId())));
    }

    @Test
    void testFindAllByJastiperIdReturnsEmptyWhenNoMatch() {
        repository.save(makeOrder("order-001", "titipers-001", "jastiper-001"));

        List<Order> result = repository.findAllByJastiperId("jastiper-999");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllByOrderStatus() {
        Order order1 = makeOrder("order-001", "titipers-001", "jastiper-001");
        Order order2 = makeOrder("order-002", "titipers-002", "jastiper-001");
        order2.updateStatus(OrderStatus.PAID);

        repository.save(order1);
        repository.save(order2);

        List<Order> pending = repository.findAllByOrderStatus(OrderStatus.PENDING);
        List<Order> paid = repository.findAllByOrderStatus(OrderStatus.PAID);

        assertEquals(1, pending.size());
        assertEquals(1, paid.size());
        assertEquals(OrderStatus.PENDING, pending.getFirst().getOrderStatus());
        assertEquals(OrderStatus.PAID, paid.getFirst().getOrderStatus());
    }

    @Test
    void testFindAllByOrderStatusReturnsEmptyWhenNoMatch() {
        repository.save(makeOrder("order-001", "titipers-001", "jastiper-001"));

        List<Order> result = repository.findAllByOrderStatus(OrderStatus.COMPLETED);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllByJastiperIdAndOrderStatus() {
        Order order1 = makeOrder("order-001", "titipers-001", "jastiper-001");
        Order order2 = makeOrder("order-002", "titipers-002", "jastiper-001");
        Order order3 = makeOrder("order-003", "titipers-003", "jastiper-002");
        order2.updateStatus(OrderStatus.PAID);

        repository.save(order1);
        repository.save(order2);
        repository.save(order3);

        List<Order> result = repository.findAllByJastiperIdAndOrderStatus("jastiper-001", OrderStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals("order-001", result.getFirst().getOrderId());
    }

    @Test
    void testFindAllByJastiperIdAndOrderStatusReturnsEmptyWhenNoMatch() {
        repository.save(makeOrder("order-001", "titipers-001", "jastiper-001"));

        List<Order> result = repository.findAllByJastiperIdAndOrderStatus("jastiper-001", OrderStatus.COMPLETED);

        assertTrue(result.isEmpty());
    }
}

