package id.ac.ui.cs.advprog.jsonbackend.features.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.CreateOrderRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.OrderResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.order.dto.RatingRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.features.order.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private static final UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID TITIPERS_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID JASTIPER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService)).build();
    }

    @Test
    void checkoutShouldReturnOrderResponse() throws Exception {
        CreateOrderRequest request = checkoutRequest();
        OrderResponse response = orderResponse(OrderStatus.PENDING);

        when(orderService.checkout(any(CreateOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()))
                .andExpect(jsonPath("$.productId").value("prod-abc-123"))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.titipersId").value(TITIPERS_ID.toString()))
                .andExpect(jsonPath("$.jastiperId").value(JASTIPER_ID.toString()));

        verify(orderService).checkout(any(CreateOrderRequest.class));
    }

    @Test
    void checkoutShouldReturnBadRequestForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOrdersShouldReturnList() throws Exception {
        when(orderService.getAllOrder()).thenReturn(List.of(orderEntity(OrderStatus.PAID)));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderStatus").value("PAID"))
                .andExpect(jsonPath("$[0].orderId").value(ORDER_ID.toString()));

        verify(orderService).getAllOrder();
    }

    @Test
    void getOrderByIdShouldReturnOrder() throws Exception {
        when(orderService.getOrderById(ORDER_ID)).thenReturn(orderEntity(OrderStatus.SHIPPED));

        mockMvc.perform(get("/api/orders/{orderId}", ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("SHIPPED"))
                .andExpect(jsonPath("$.orderId").value(ORDER_ID.toString()));

        verify(orderService).getOrderById(ORDER_ID);
    }

    @Test
    void getOrderByIdShouldReturnBadRequestForInvalidUuid() throws Exception {
        mockMvc.perform(get("/api/orders/{orderId}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrdersByTitipersIdShouldReturnList() throws Exception {
        when(orderService.getOrderByTitipersId(TITIPERS_ID.toString())).thenReturn(List.of(orderEntity(OrderStatus.PAID)));

        mockMvc.perform(get("/api/orders/titipers/{titipersId}", TITIPERS_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderStatus").value("PAID"));

        verify(orderService).getOrderByTitipersId(TITIPERS_ID.toString());
    }

    @Test
    void getOrdersByJastiperIdShouldReturnList() throws Exception {
        when(orderService.getOrderByJastiperId(JASTIPER_ID.toString())).thenReturn(List.of(orderEntity(OrderStatus.PAID)));

        mockMvc.perform(get("/api/orders/jastiper/{jastiperId}", JASTIPER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderStatus").value("PAID"));

        verify(orderService).getOrderByJastiperId(JASTIPER_ID.toString());
    }

    @Test
    void getOrdersByStatusShouldReturnList() throws Exception {
        when(orderService.getOrderByStatus(OrderStatus.PAID)).thenReturn(List.of(orderEntity(OrderStatus.PAID)));

        mockMvc.perform(get("/api/orders/status/{status}", OrderStatus.PAID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderStatus").value("PAID"));

        verify(orderService).getOrderByStatus(OrderStatus.PAID);
    }

    @Test
    void getOrderByOrderIdAndStatusShouldReturnNotFoundWhenMissing() throws Exception {
        when(orderService.getOrderByOrderIdAndStatus(ORDER_ID, OrderStatus.COMPLETED)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/{orderId}/status/{status}", ORDER_ID, OrderStatus.COMPLETED))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatusShouldReturnUpdatedOrder() throws Exception {
        when(orderService.updateOrderStatus(ORDER_ID, OrderStatus.SHIPPED)).thenReturn(orderResponse(OrderStatus.SHIPPED));

        mockMvc.perform(patch("/api/orders/{orderId}/status", ORDER_ID)
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("SHIPPED"));

        verify(orderService).updateOrderStatus(ORDER_ID, OrderStatus.SHIPPED);
    }

    @Test
    void updateOrderStatusShouldReturnBadRequestForInvalidStatus() throws Exception {
        mockMvc.perform(patch("/api/orders/{orderId}/status", ORDER_ID)
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelOrderShouldForwardReasonAndReturnResponse() throws Throwable {
        when(orderService.cancelOrder(eq(ORDER_ID.toString()), eq("Out of stock"))).thenReturn(orderResponse(OrderStatus.CANCELLED));

        mockMvc.perform(patch("/api/orders/{orderId}/cancel", ORDER_ID)
                        .param("cancellationReason", "Out of stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));

        verify(orderService).cancelOrder(ORDER_ID.toString(), "Out of stock");
    }

    @Test
    void submitRatingShouldReturnBadRequestForInvalidUuid() throws Exception {
        RatingRequest request = ratingRequest(5, 4);

        mockMvc.perform(post("/api/orders/{orderId}/rating", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private static CreateOrderRequest checkoutRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setProductId("prod-abc-123");
        request.setTitipersId(TITIPERS_ID);
        request.setJastiperId(JASTIPER_ID);
        request.setQuantity(2);
        request.setShippingAddress("Jl. Margonda Raya No. 1");
        return request;
    }

    private static RatingRequest ratingRequest(int jastiperRating, int productRating) {
        RatingRequest request = new RatingRequest();
        request.setJastiperRating(jastiperRating);
        request.setProductRating(productRating);
        return request;
    }

    private static Order orderEntity(OrderStatus status) {
        Order order = new Order(ORDER_ID, "prod-abc-123", TITIPERS_ID, JASTIPER_ID, 2, "Jl. Margonda Raya No. 1");
        order.setOrderStatus(status);
        order.setCreatedAt(LocalDateTime.of(2026, 3, 6, 14, 30));
        order.setUpdatedAt(LocalDateTime.of(2026, 3, 6, 14, 31));
        return order;
    }

    private static OrderResponse orderResponse(OrderStatus status) {
        return OrderResponse.builder()
                .orderId(ORDER_ID)
                .productId("prod-abc-123")
                .quantity(2)
                .totalAmount(BigDecimal.valueOf(250000L))
                .shippingAddress("Jl. Margonda Raya No. 1")
                .orderStatus(status)
                .createdAt(LocalDateTime.of(2026, 3, 6, 14, 30))
                .updatedAt(LocalDateTime.of(2026, 3, 6, 14, 31))
                .titipersId(TITIPERS_ID)
                .jastiperId(JASTIPER_ID)
                .jastiperRating(5)
                .productRating(4)
                .cancellationReason(status == OrderStatus.CANCELLED ? "Out of stock" : null)
                .build();
    }
}

