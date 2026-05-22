package id.ac.ui.cs.advprog.jsonbackend.features.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.features.catalog.model.Product;
import id.ac.ui.cs.advprog.jsonbackend.features.catalog.repository.ProductRepository;
import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductStockServiceImpl implements ProductStockService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void reserveStock(Order order) {
        Product product = productRepository.findByIdForUpdate(order.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < order.getQuantity()) {
            throw new RuntimeException("Stock not enough");
        }

        product.setStock(product.getStock() - order.getQuantity());
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void releaseReservedStock(Order order) {
        Product product = productRepository.findByIdForUpdate(order.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStock(product.getStock() + order.getQuantity());
        productRepository.save(product);
    }
}
