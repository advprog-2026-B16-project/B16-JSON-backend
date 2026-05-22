package id.ac.ui.cs.advprog.jsonbackend.features.catalog.service;

import id.ac.ui.cs.advprog.jsonbackend.features.order.model.Order;

public interface ProductStockService {
    void reserveStock(Order order);

    void releaseReservedStock(Order order);
}
