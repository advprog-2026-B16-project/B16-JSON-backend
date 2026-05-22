package id.ac.ui.cs.advprog.jsonbackend.features.order.service;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.repository.ProductRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JastiperOrderServiceImplTest {

    private static final UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TITIPERS_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID JASTIPER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID OTHER_JASTIPER_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    private JastiperOrderServiceImpl jastiperOrderService;

    @BeforeEach
    void setUp() {
        OrderPricingService orderPricingService = new OrderPricingService(productRepository);
        jastiperOrderService = new JastiperOrderServiceImpl(orderRepository, orderPricingService);
    }

    @Test
    void getMyOrdersShouldReturnAuthenticatedJastiperOrders() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Order order = order(OrderStatus.PURCHASED, JASTIPER_ID);

        when(orderRepository.findByJastiperId(JASTIPER_ID)).thenReturn(List.of(order));

        List<Order> result = jastiperOrderService.getMyOrders(jastiper);

        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
        verify(orderRepository).findByJastiperId(JASTIPER_ID);
    }

    @Test
    void markAsShippedShouldUpdateOwnedPurchasedOrder() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Order order = order(OrderStatus.PURCHASED, JASTIPER_ID);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById("prod-abc-123")).thenReturn(Optional.of(product(125000)));

        OrderResponse response = jastiperOrderService.markAsShipped(jastiper, ORDER_ID);

        assertEquals(OrderStatus.SHIPPED, response.getOrderStatus());
        assertEquals(BigDecimal.valueOf(250000L), response.getTotalAmount());
        verify(orderRepository).save(order);
    }

    @Test
    void markAsCompletedShouldUpdateOwnedShippedOrder() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Order order = order(OrderStatus.SHIPPED, JASTIPER_ID);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById("prod-abc-123")).thenReturn(Optional.of(product(125000)));

        OrderResponse response = jastiperOrderService.markAsCompleted(jastiper, ORDER_ID);

        assertEquals(OrderStatus.COMPLETED, response.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void markAsShippedShouldRejectOrderOwnedByAnotherJastiper() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Order order = order(OrderStatus.PURCHASED, OTHER_JASTIPER_ID);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> jastiperOrderService.markAsShipped(jastiper, ORDER_ID));

        assertEquals("Jastiper hanya boleh mengubah status order miliknya sendiri", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void markAsShippedShouldRejectInvalidTransition() {
        User jastiper = user(JASTIPER_ID, UserRole.JASTIPER);
        Order order = order(OrderStatus.PAID, JASTIPER_ID);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> jastiperOrderService.markAsShipped(jastiper, ORDER_ID));

        assertEquals("PAID can only transition to PURCHASED or CANCELLED", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldRejectNonJastiperUser() {
        User titiper = user(TITIPERS_ID, UserRole.TITIPER);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> jastiperOrderService.getMyOrders(titiper));

        assertEquals("Only jastiper can access this order workflow", exception.getMessage());
        verify(orderRepository, never()).findByJastiperId(any());
    }

    private static Order order(OrderStatus status, UUID jastiperId) {
        Order order = new Order(ORDER_ID, "prod-abc-123", TITIPERS_ID, jastiperId, 2, "Jl. Margonda Raya No. 1");
        order.setOrderStatus(status);
        return order;
    }

    private static User user(UUID id, UserRole role) {
        return User.builder()
                .id(id)
                .username("user-" + id)
                .email(id + "@example.com")
                .password("secret")
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private static Product product(double price) {
        Product product = new Product();
        product.setId("prod-abc-123");
        product.setPrice(price);
        return product;
    }
}
