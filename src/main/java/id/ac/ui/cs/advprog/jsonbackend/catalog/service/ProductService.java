package id.ac.ui.cs.advprog.jsonbackend.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.catalog.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    List<ProductDTO> findAllProducts();

    List<ProductDTO> searchByName(String keyword);

    List<ProductDTO> findByJastiper(String jastiperId);

}