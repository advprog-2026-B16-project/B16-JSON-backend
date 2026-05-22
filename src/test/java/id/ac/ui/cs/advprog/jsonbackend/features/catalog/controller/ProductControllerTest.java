package id.ac.ui.cs.advprog.jsonbackend.features.catalog.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.ProductRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.StockRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    private ProductService productService;
    private ProductController controller;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        controller = new ProductController(productService);
    }

    @Test
    void controllerShouldDelegateAllProductOperations() {
        ProductDTO dto = product();
        ProductRequest request = new ProductRequest();
        StockRequest stockRequest = new StockRequest();
        stockRequest.quantity = 2;

        when(productService.findAllProducts()).thenReturn(List.of(dto));
        when(productService.create(request)).thenReturn(dto);
        when(productService.update("prod-1", request)).thenReturn(dto);
        when(productService.reduceStock("prod-1", 2)).thenReturn(dto);

        assertEquals(List.of(dto), controller.getAll());
        assertEquals(dto, controller.create(request));
        assertEquals(dto, controller.update("prod-1", request));
        controller.delete("prod-1");
        assertEquals(dto, controller.reduceStock("prod-1", stockRequest));

        verify(productService).delete("prod-1");
    }

    private static ProductDTO product() {
        return new ProductDTO("prod-1", "Snack", "desc", 1000, 5, "JP", LocalDate.now(), "jastiper-1");
    }
}
