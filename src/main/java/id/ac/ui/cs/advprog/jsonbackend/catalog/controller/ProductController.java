package id.ac.ui.cs.advprog.jsonbackend.catalog.controller;

import id.ac.ui.cs.advprog.jsonbackend.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.catalog.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchByName(keyword);
    }

    @GetMapping("/jastiper/{jastiperId}")
    public List<Product> getByJastiper(@PathVariable String jastiperId) {
        return productService.findByJastiper(jastiperId);
    }
}