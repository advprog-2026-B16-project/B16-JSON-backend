package id.ac.ui.cs.advprog.jsonbackend.module.catalog.repository;

import id.ac.ui.cs.advprog.jsonbackend.module.catalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByJastiperId(String jastiperId);

}