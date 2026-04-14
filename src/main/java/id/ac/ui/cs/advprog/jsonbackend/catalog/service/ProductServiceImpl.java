package id.ac.ui.cs.advprog.jsonbackend.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.catalog.mapper.ProductMapper;
import id.ac.ui.cs.advprog.jsonbackend.catalog.repository.ProductRepository;
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
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> findByJastiper(String jastiperId) {
        return productRepository.findByJastiperId(jastiperId)
                .stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
}