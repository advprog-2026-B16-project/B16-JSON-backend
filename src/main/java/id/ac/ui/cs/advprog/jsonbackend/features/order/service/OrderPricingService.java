package id.ac.ui.cs.advprog.jsonbackend.features.order.service;

import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderPricingService {

    private static final BigDecimal DEFAULT_UNIT_PRICE = BigDecimal.valueOf(10000L);

    private static final Map<String, BigDecimal> HARDCODED_PRODUCT_PRICES = Map.of(
            "prod-abc-123", BigDecimal.valueOf(125000L),
            "prod-xyz-456", BigDecimal.valueOf(250000L),
            "prod-mno-789", BigDecimal.valueOf(175000L)
    );

    public BigDecimal calculateTotal(Order order) {
        return resolveUnitPrice(order.getProductId()).multiply(BigDecimal.valueOf(order.getQuantity()));
    }

    private BigDecimal resolveUnitPrice(String productId) {
        return HARDCODED_PRODUCT_PRICES.getOrDefault(productId, DEFAULT_UNIT_PRICE);
    }
}
