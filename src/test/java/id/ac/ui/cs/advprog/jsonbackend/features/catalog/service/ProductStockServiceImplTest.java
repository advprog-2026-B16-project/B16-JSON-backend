package id.ac.ui.cs.advprog.jsonbackend.features.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.repository.ProductRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductStockServiceImplTest {

    private ProductRepository productRepository;
    private ProductStockServiceImpl productStockService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productStockService = new ProductStockServiceImpl(productRepository);
    }

    @Test
    void reserveStockShouldDecreaseStockWithLockedProduct() {
        Product product = product(5);
        Order order = order(2);

        when(productRepository.findByIdForUpdate("prod-abc-123")).thenReturn(Optional.of(product));

        productStockService.reserveStock(order);

        assertEquals(3, product.getStock());
        verify(productRepository).save(product);
    }

    @Test
    void reserveStockShouldRejectInsufficientStock() {
        Product product = product(1);
        Order order = order(2);

        when(productRepository.findByIdForUpdate("prod-abc-123")).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productStockService.reserveStock(order));

        assertEquals("Stock not enough", exception.getMessage());
        verify(productRepository, never()).save(product);
    }

    @Test
    void releaseReservedStockShouldIncreaseStockWithLockedProduct() {
        Product product = product(3);
        Order order = order(2);

        when(productRepository.findByIdForUpdate("prod-abc-123")).thenReturn(Optional.of(product));

        productStockService.releaseReservedStock(order);

        assertEquals(5, product.getStock());
        verify(productRepository).save(product);
    }

    private static Product product(int stock) {
        return new Product("Snack", "desc", 125000, stock, "JP", LocalDate.now(), UUID.randomUUID().toString());
    }

    private static Order order(int quantity) {
        return new Order(
                UUID.randomUUID(),
                "prod-abc-123",
                UUID.randomUUID(),
                UUID.randomUUID(),
                quantity,
                "Jl. Margonda"
        );
    }
}
