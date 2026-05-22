package id.ac.ui.cs.advprog.jsonbackend.features.catalog.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.ProductRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.StockRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductDTO> getAll() {
        return service.findAllProducts();
    }

    @PostMapping
    public ProductDTO create(@RequestBody ProductRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable String id, @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("/{id}/reduce")
    public ProductDTO reduceStock(@PathVariable String id, @RequestBody StockRequest request) {
        return service.reduceStock(id, request.quantity);
    }
}
