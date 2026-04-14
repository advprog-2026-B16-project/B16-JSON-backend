package id.ac.ui.cs.advprog.jsonbackend.order.service;

import id.ac.ui.cs.advprog.jsonbackend.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.catalog.service.ProductService;
import id.ac.ui.cs.advprog.jsonbackend.catalog.repository.ProductRepository;
import id.ac.ui.cs.advprog.jsonbackend.order.dto.*;
import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import id.ac.ui.cs.advprog.jsonbackend.order.model.Order;
import id.ac.ui.cs.advprog.jsonbackend.order.repository.OrderRepository;
import id.ac.ui.cs.advprog.jsonbackend.order.state.OrderState;
import id.ac.ui.cs.advprog.jsonbackend.order.state.OrderStateFactory;
import id.ac.ui.cs.advprog.jsonbackend.wallet.model.Wallet;
import id.ac.ui.cs.advprog.jsonbackend.wallet.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final ProductService productService;;
    private final WalletService walletService;

    @Override
    @Transactional
    public OrderResponse checkout(CreateOrderRequest request){

        List<ProductDTO> productList = productService.findAllProducts();
        // TODO : Dari productServicenya ada cara buat ngambil produk based on ID
        ProductDTO product = null;
        for (ProductDTO p : productList) {
            if (p.getId().equals(request.getProductId())) {
                product = p;
                break;
            }
        }
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Stok tidak cukup!");
        }

        BigDecimal totalAmount = calculateTotal(product.getPrice(), request.getQuantity());

        // TODO : walletService untuk cek saldo dan deduct
//        walletService.payment();

        Order order = new Order(
                null,
                product.getId(),
                request.getTitipersId(),
                request.getJastiperId(),
                request.getQuantity(),
                request.getShippingAddress()
        );

        order.updateStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String orderId, String cancellationReason) throws Throwable {
        Order order = (Order) orderRepository.findById(UUID.fromString(orderId)).orElseThrow(() -> new RuntimeException("Order tidak ditemukan!"));

        order.cancel(cancellationReason);

        Product product = productRepository.getById(order.getProductId());
        BigDecimal refundAmount = calculateTotal(product.getPrice(), order.getQuantity());

        // TODO : walletService untuk refund
        order.updateStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse submitRating(UUID orderId, RatingRequest request) {
        Order order = (Order) orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order tidak ditemukan!"));
        order.submitRating(request.getJastiperRating(), request.getProductRating());
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    @Override
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrderByTitipersId(String titipersId) {
        return orderRepository.findByTitipersId(UUID.fromString(titipersId));
    }

    @Override
    public List<Order> getOrderByJastiperId(String jastiperId) {
        return orderRepository.findByJastiperId(UUID.fromString(jastiperId));
    }

    @Override
    public Optional<Order> getOrderByOrderIdAndStatus(UUID orderId, OrderStatus status) {
        return orderRepository.findByOrderIdAndOrderStatus(orderId, status);
    }

    @Override
    public List<Order> getOrderByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order tidak ditemukan!"));
    }

    private BigDecimal calculateTotal(double price, int quantity) {
        return BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(quantity));

    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<ProductDTO> productList = productService.findAllProducts();
        // TODO : Dari productServicenya ada cara buat ngambil produk based on ID
        ProductDTO product = null;
        for (ProductDTO p : productList) {
            if (p.getId().equals(order.getProductId())) {
                product = p;
                break;
            }
        }

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalAmount(calculateTotal(product.getPrice(), order.getQuantity()))
                .shippingAddress(order.getShippingAddress())
                .orderStatus((order.getOrderStatus()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .titipersId(order.getTitipersId())
                .jastiperId(order.getJastiperId())
                .jastiperRating(order.getJastiperRating())
                .productRating(order.getProductRating())
                .cancellationReason(order.getCancellationReason())
                .build();
    }
}