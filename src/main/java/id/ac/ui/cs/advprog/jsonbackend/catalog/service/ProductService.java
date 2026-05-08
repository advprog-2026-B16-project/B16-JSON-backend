package id.ac.ui.cs.advprog.jsonbackend.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.catalog.dto.ProductRequest;

import java.util.List;

public interface ProductService {

    List<ProductDTO> findAllProducts();

    List<ProductDTO> searchByName(String name);

    List<ProductDTO> findByJastiper(String jastiperId);

    ProductDTO create(ProductRequest request);

    ProductDTO update(String id, ProductRequest request);

    void delete(String id);

    ProductDTO reduceStock(String id, int quantity);
}