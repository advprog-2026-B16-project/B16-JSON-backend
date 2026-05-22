package id.ac.ui.cs.advprog.jsonbackend.features.order.service;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.repository.ProductRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.CreateOrderRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.RatingRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.event.OrderCancelledEvent;
import id.ac.ui.cs.advprog.jsonbackend.features.order.event.OrderCreatedEvent;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private static final UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TITIPERS_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID JASTIPER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        OrderPricingService orderPricingService = new OrderPricingService(productRepository);
        orderService = new OrderServiceImpl(orderRepository, orderPricingService, eventPublisher);
    }

    @Test
    void checkoutShouldPersistOrderPublishEventAndReturnMappedResponse() {
        CreateOrderRequest request = checkoutRequest("prod-abc-123", 2);

        when(productRepository.findById("prod-abc-123")).thenReturn(Optional.of(product("prod-abc-123", 125000)));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId(ORDER_ID);
            return order;
        });

        OrderResponse response = orderService.checkout(request);

        assertEquals(ORDER_ID, response.getOrderId());
        assertEquals("prod-abc-123", response.getProductId());
        assertEquals(2, response.getQuantity());
        assertEquals(BigDecimal.valueOf(250000L), response.getTotalAmount());
        assertEquals("Jl. Margonda Raya No. 1", response.getShippingAddress());
        assertEquals(OrderStatus.PENDING, response.getOrderStatus());
        assertEquals(TITIPERS_ID, response.getTitipersId());
        assertEquals(JASTIPER_ID, response.getJastiperId());
        assertNotNull(response.getCreatedAt());

        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        OrderCreatedEvent event = eventCaptor.getValue();
        assertEquals(ORDER_ID, event.orderId());
        assertEquals(TITIPERS_ID.toString(), event.titipersId());
        assertEquals("prod-abc-123", event.productId());
        assertEquals(2, event.quantity());
        assertEquals(BigDecimal.valueOf(250000L), event.totalAmount());
    }

    @Test
    void checkoutShouldRejectUnknownProduct() {
        CreateOrderRequest request = checkoutRequest("prod-unknown", 3);

        when(productRepository.findById("prod-unknown")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.checkout(request));

        assertEquals("Product not found", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @ParameterizedTest
    @MethodSource("invalidCheckoutRequests")
    void checkoutShouldRejectInvalidRequests(CreateOrderRequest request, String expectedMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.checkout(request));

        assertEquals(expectedMessage, exception.getMessage());
        verifyNoInteractions(orderRepository, eventPublisher);
    }

    @Test
    void cancelOrderShouldUseProvidedReasonWhenAvailable() throws Throwable {
        Order order = existingOrder(OrderStatus.PAID);
        String reason = "Customer changed mind";

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productRepository.findById("prod-abc-123")).thenReturn(Optional.of(product("prod-abc-123", 125000)));
        when(orderRepository.save(order)).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.cancelOrder(ORDER_ID.toString(), reason);

        assertEquals(OrderStatus.CANCELLED, response.getOrderStatus());
        assertEquals(reason, response.getCancellationReason());
        assertNotNull(response.getUpdatedAt());

        ArgumentCaptor<OrderCancelledEvent> eventCaptor = ArgumentCaptor.forClass(OrderCancelledEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        OrderCancelledEvent event = eventCaptor.getValue();
        assertEquals(ORDER_ID, event.orderId());
        assertEquals(TITIPERS_ID.toString(), event.titipersId());
        assertEquals(BigDecimal.valueOf(250000L), event.totalRefundAmount());
        assertEquals(reason, event.reason());
    }

    @ParameterizedTest
    @MethodSource("cancelReasonCases")
    void cancelOrderShouldFallbackToDefaultReasonWhenReasonMissing(String inputReason, String expectedReason) throws Throwable {
        Order order = existingOrder(OrderStatus.PAID);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productRepository.findById("prod-abc-123")).thenReturn(Optional.of(product("prod-abc-123", 125000)));
        when(orderRepository.save(order)).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.cancelOrder(ORDER_ID.toString(), inputReason);

        assertEquals(OrderStatus.CANCELLED, response.getOrderStatus());
        assertEquals(expectedReason, response.getCancellationReason());
        verify(eventPublisher).publishEvent(any(OrderCancelledEvent.class));
    }

    @Test
    void cancelOrderShouldRejectInvalidOrderId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.cancelOrder("not-a-uuid", "reason"));

        assertNotNull(exception);
        verifyNoInteractions(orderRepository, eventPublisher);
    }

    @Test
    void cancelOrderShouldThrowWhenOrderIsMissing() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.cancelOrder(ORDER_ID.toString(), "reason"));

        assertEquals("Order tidak ditemukan!", exception.getMessage());
        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void submitRatingShouldPersistCompletedOrder() {
        Order order = existingOrder(OrderStatus.COMPLETED);
        RatingRequest request = ratingRequest(5, 4);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productRepository.findById("prod-abc-123")).thenReturn(Optional.of(product("prod-abc-123", 125000)));
        when(orderRepository.save(order)).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.submitRating(ORDER_ID, request);

        assertEquals(OrderStatus.COMPLETED, response.getOrderStatus());
        assertEquals(5, response.getJastiperRating());
        assertEquals(4, response.getProductRating());
        assertNotNull(response.getUpdatedAt());
        verify(orderRepository).save(order);
    }

    @Test
    void submitRatingShouldThrowWhenOrderIsNotCompleted() {
        Order order = existingOrder(OrderStatus.PAID);
        RatingRequest request = ratingRequest(5, 4);

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.submitRating(ORDER_ID, request));

        assertEquals("Rating can only be submitted for COMPLETED orders", exception.getMessage());
        verify(orderRepository).findById(ORDER_ID);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void submitRatingShouldThrowWhenOrderIsMissing() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.submitRating(ORDER_ID, ratingRequest(5, 4)));

        assertEquals("Order tidak ditemukan!", exception.getMessage());
    }

    @Test
    void getAllOrderShouldReturnRepositoryResults() {
        Order order = existingOrder(OrderStatus.PAID);
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> orders = orderService.getAllOrder();

        assertEquals(1, orders.size());
        assertEquals(order, orders.get(0));
    }

    @Test
    void getOrderByTitipersIdShouldDelegateToRepository() {
        Order order = existingOrder(OrderStatus.PAID);
        when(orderRepository.findByTitipersId(TITIPERS_ID)).thenReturn(List.of(order));

        List<Order> orders = orderService.getOrderByTitipersId(TITIPERS_ID.toString());

        assertEquals(1, orders.size());
        assertEquals(order, orders.get(0));
        verify(orderRepository).findByTitipersId(TITIPERS_ID);
    }

    @Test
    void getOrderByTitipersIdShouldRejectInvalidUuid() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.getOrderByTitipersId("invalid-uuid"));
    }

    @Test
    void getOrderByJastiperIdShouldDelegateToRepository() {
        Order order = existingOrder(OrderStatus.SHIPPED);
        when(orderRepository.findByJastiperId(JASTIPER_ID)).thenReturn(List.of(order));

        List<Order> orders = orderService.getOrderByJastiperId(JASTIPER_ID.toString());

        assertEquals(1, orders.size());
        assertEquals(order, orders.get(0));
        verify(orderRepository).findByJastiperId(JASTIPER_ID);
    }

    @Test
    void getOrderByJastiperIdShouldRejectInvalidUuid() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.getOrderByJastiperId("invalid-uuid"));
    }

    @Test
    void getOrderByOrderIdAndStatusShouldReturnOptionalResult() {
        Order order = existingOrder(OrderStatus.PAID);
        when(orderRepository.findByOrderIdAndOrderStatus(ORDER_ID, OrderStatus.PAID)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderByOrderIdAndStatus(ORDER_ID, OrderStatus.PAID);

        assertEquals(Optional.of(order), result);
    }

    @Test
    void getOrderByStatusShouldDelegateToRepository() {
        Order order = existingOrder(OrderStatus.CANCELLED);
        when(orderRepository.findByOrderStatus(OrderStatus.CANCELLED)).thenReturn(List.of(order));

        List<Order> result = orderService.getOrderByStatus(OrderStatus.CANCELLED);

        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
    }

    @Test
    void getOrderByIdShouldReturnOrder() {
        Order order = existingOrder(OrderStatus.SHIPPED);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(ORDER_ID);

        assertEquals(order, result);
    }

    @Test
    void getOrderByIdShouldThrowWhenMissing() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.getOrderById(ORDER_ID));

        assertEquals("Order tidak ditemukan!", exception.getMessage());
    }

    @Test
    void updateOrderStatusShouldPersistNewStatus() {
        Order order = existingOrder(OrderStatus.PAID);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(productRepository.findById("prod-abc-123")).thenReturn(Optional.of(product("prod-abc-123", 125000)));
        when(orderRepository.save(order)).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.updateOrderStatus(ORDER_ID, OrderStatus.SHIPPED);

        assertEquals(OrderStatus.SHIPPED, response.getOrderStatus());
        assertNotNull(response.getUpdatedAt());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatusShouldThrowWhenOrderIsMissing() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus(ORDER_ID, OrderStatus.SHIPPED));

        assertEquals("Order tidak ditemukan!", exception.getMessage());
    }

    private static Stream<Arguments> invalidCheckoutRequests() {
        UUID sharedUser = UUID.fromString("44444444-4444-4444-4444-444444444444");
        return Stream.of(
                Arguments.of(null, "Request checkout tidak boleh null"),
                Arguments.of(checkoutRequest(null, TITIPERS_ID, JASTIPER_ID, 1, "Jl. Margonda Raya No. 1"), "productId wajib diisi"),
                Arguments.of(checkoutRequest("prod-abc-123", null, JASTIPER_ID, 1, "Jl. Margonda Raya No. 1"), "titipersId wajib diisi"),
                Arguments.of(checkoutRequest("prod-abc-123", sharedUser, sharedUser, 1, "Jl. Margonda Raya No. 1"), "Jastiper tidak boleh membeli barang dirinya sendiri"),
                Arguments.of(checkoutRequest("prod-abc-123", TITIPERS_ID, JASTIPER_ID, 0, "Jl. Margonda Raya No. 1"), "quantity harus lebih dari 0"),
                Arguments.of(checkoutRequest("prod-abc-123", TITIPERS_ID, JASTIPER_ID, 1, "   "), "shippingAddress wajib diisi")
        );
    }

    private static Stream<Arguments> cancelReasonCases() {
        return Stream.of(
                Arguments.of(null, "No reason provided"),
                Arguments.of("", "No reason provided"),
                Arguments.of("   ", "No reason provided"),
                Arguments.of("Customer changed mind", "Customer changed mind")
        );
    }

    private static CreateOrderRequest checkoutRequest(String productId, int quantity) {
        return checkoutRequest(productId, TITIPERS_ID, JASTIPER_ID, quantity, "Jl. Margonda Raya No. 1");
    }

    private static CreateOrderRequest checkoutRequest(String productId,
                                                      UUID titipersId,
                                                      UUID jastiperId,
                                                      int quantity,
                                                      String shippingAddress) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setProductId(productId);
        request.setTitipersId(titipersId);
        request.setJastiperId(jastiperId);
        request.setQuantity(quantity);
        request.setShippingAddress(shippingAddress);
        return request;
    }

    private static RatingRequest ratingRequest(Integer jastiperRating, Integer productRating) {
        RatingRequest request = new RatingRequest();
        request.setJastiperRating(jastiperRating);
        request.setProductRating(productRating);
        return request;
    }

    private static Product product(String id, double price) {
        Product product = new Product();
        product.setId(id);
        product.setPrice(price);
        return product;
    }

    private static Order existingOrder(OrderStatus status) {
        Order order = new Order(ORDER_ID, "prod-abc-123", TITIPERS_ID, JASTIPER_ID, 2, "Jl. Margonda Raya No. 1");
        order.updateStatus(status);
        if (status == OrderStatus.PAID || status == OrderStatus.PURCHASED || status == OrderStatus.SHIPPED || status == OrderStatus.COMPLETED) {
            order.setUpdatedAt(null);
        }
        return order;
    }
}
