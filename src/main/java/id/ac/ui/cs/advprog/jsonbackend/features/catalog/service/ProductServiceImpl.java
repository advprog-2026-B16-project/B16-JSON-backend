package id.ac.ui.cs.advprog.jsonbackend.features.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto.ProductRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.mapper.ProductMapper;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.repository.ProductRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> findByJastiper(String jastiperId) {
        return productRepository.findByJastiperId(jastiperId)
                .stream()
                .map(ProductMapper::toDTO)
                .toList();
    }

    @Override
    public ProductDTO create(ProductRequest request) {

        Product product = ProductMapper.toEntity(request);
        return ProductMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO update(String id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductMapper.updateEntity(product, request);

        return ProductMapper.toDTO(productRepository.save(product));
    }

    @Override
    public void delete(String id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productRepository.delete(product);
    }
    @Override
    @Transactional
    public ProductDTO reduceStock(String id, int quantity) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock not enough");
        }

        product.setStock(product.getStock() - quantity);

        return ProductMapper.toDTO(productRepository.save(product));
    }
}
