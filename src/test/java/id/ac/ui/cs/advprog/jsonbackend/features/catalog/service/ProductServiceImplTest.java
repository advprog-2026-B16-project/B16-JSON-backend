package id.ac.ui.cs.advprog.jsonbackend.features.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.ProductRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.repository.FakeProductRepository;
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

    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest();
        request.name = "Tokyo Banana";
        request.description = "Banana sponge cake";
        request.price = 45000;
        request.stock = 12;
        request.originCountry = "Japan";
        request.purchaseDate = LocalDate.now();
        request.jastiperId = "jastiper-create";

        ProductDTO result = service.create(request);

        assertNotNull(result.getId());
        assertEquals("Tokyo Banana", result.getName());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void testUpdateProduct() {
        Product product = new Product(
                "Pocky",
                "Chocolate",
                30000,
                15,
                "Japan",
                LocalDate.now(),
                "jastiper-update"
        );
        Product savedProduct = repository.save(product);

        ProductRequest request = new ProductRequest();
        request.name = "Pocky Almond";
        request.description = "Almond coating";
        request.price = 35000;
        request.stock = 20;
        request.originCountry = "Japan";
        request.purchaseDate = LocalDate.now().plusDays(1);
        request.jastiperId = "jastiper-new";

        ProductDTO result = service.update(savedProduct.getId(), request);

        assertEquals("Pocky Almond", result.getName());
        assertEquals(20, result.getStock());
        assertEquals("jastiper-update", result.getJastiperId());
    }

    @Test
    void testDeleteProduct() {
        Product savedProduct = repository.save(new Product(
                "KitKat",
                "Matcha",
                50000,
                10,
                "Japan",
                LocalDate.now(),
                "jastiper-delete"
        ));

        service.delete(savedProduct.getId());

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void testReduceStock() {
        Product savedProduct = repository.save(new Product(
                "Royce",
                "Chocolate",
                120000,
                8,
                "Japan",
                LocalDate.now(),
                "jastiper-stock"
        ));

        ProductDTO result = service.reduceStock(savedProduct.getId(), 3);

        assertEquals(5, result.getStock());
    }

    @Test
    void testReduceStockThrowsWhenInsufficient() {
        Product savedProduct = repository.save(new Product(
                "Royce",
                "Chocolate",
                120000,
                2,
                "Japan",
                LocalDate.now(),
                "jastiper-stock"
        ));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.reduceStock(savedProduct.getId(), 3));

        assertEquals("Stock not enough", exception.getMessage());
    }
}
