package id.ac.ui.cs.advprog.jsonbackend.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Product> findByJastiper(String jastiperId) {
        return productRepository.findByJastiperId(jastiperId);
    }
}