package id.ac.ui.cs.advprog.jsonbackend.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.catalog.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAllProducts();

    List<Product> searchByName(String keyword);

    List<Product> findByJastiper(String jastiperId);

}