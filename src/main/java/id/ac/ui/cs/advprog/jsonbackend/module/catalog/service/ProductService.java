package id.ac.ui.cs.advprog.jsonbackend.module.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.module.catalog.dto.ProductDTO;

import java.util.List;

public interface ProductService {

    List<ProductDTO> findAllProducts();

    List<ProductDTO> searchByName(String keyword);

    List<ProductDTO> findByJastiper(String jastiperId);

}