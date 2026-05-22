package id.ac.ui.cs.advprog.jsonbackend.features.order.service;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.repository.ProductRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderPricingService {

    private final ProductRepository productRepository;

    public BigDecimal calculateTotal(Order order) {
        return resolveUnitPrice(order.getProductId()).multiply(BigDecimal.valueOf(order.getQuantity()));
    }

    private BigDecimal resolveUnitPrice(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
        return unitPrice.stripTrailingZeros().scale() <= 0 ? unitPrice.setScale(0) : unitPrice;
    }
}
