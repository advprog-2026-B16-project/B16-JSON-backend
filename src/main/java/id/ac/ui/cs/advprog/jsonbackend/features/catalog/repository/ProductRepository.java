package id.ac.ui.cs.advprog.jsonbackend.features.catalog.repository;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByJastiperId(String jastiperId);

    long countByStockLessThanEqual(int stock);

    @Query("select coalesce(sum(p.stock), 0) from Product p")
    Long sumStock();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") String id);

}
