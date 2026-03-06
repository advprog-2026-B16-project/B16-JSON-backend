package id.ac.ui.cs.advprog.jsonbackend.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.catalog.repository.FakeProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceImplTest {

    private FakeProductRepository repository;
    private ProductServiceImpl service;

    @BeforeEach
    void setup() {
        repository = new FakeProductRepository();
        service = new ProductServiceImpl(repository);
    }

    @Test
    void testFindAllProducts() {

        Product product = new Product(
                "KitKat Matcha",
                "Matcha flavor",
                50000,
                10,
                "Japan",
                LocalDate.now(),
                "jastiper1"
        );

        repository.addProduct(product);

        List<ProductDTO> result = service.findAllProducts();

        assertEquals(1, result.size());
        assertEquals("KitKat Matcha", result.get(0).getName());
    }

    @Test
    void testSearchProductByName() {

        repository.addProduct(new Product(
                "KitKat Strawberry",
                "Strawberry flavor",
                55000,
                5,
                "Japan",
                LocalDate.now(),
                "jastiper1"
        ));

        repository.addProduct(new Product(
                "Pocky",
                "Chocolate",
                30000,
                15,
                "Japan",
                LocalDate.now(),
                "jastiper2"
        ));

        List<ProductDTO> result = service.searchByName("kitkat");

        assertEquals(1, result.size());
        assertEquals("KitKat Strawberry", result.get(0).getName());
    }

    @Test
    void testFindProductsByJastiper() {

        repository.addProduct(new Product(
                "KitKat",
                "Matcha",
                50000,
                10,
                "Japan",
                LocalDate.now(),
                "jastiperA"
        ));

        repository.addProduct(new Product(
                "Pocky",
                "Chocolate",
                30000,
                10,
                "Japan",
                LocalDate.now(),
                "jastiperB"
        ));

        List<ProductDTO> result = service.findByJastiper("jastiperA");

        assertEquals(1, result.size());
        assertEquals("KitKat", result.get(0).getName());
    }
}