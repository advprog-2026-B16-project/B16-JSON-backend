package id.ac.ui.cs.advprog.jsonbackend.catalog.mapper;

import id.ac.ui.cs.advprog.jsonbackend.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.catalog.dto.ProductRequest;
import id.ac.ui.cs.advprog.jsonbackend.catalog.model.Product;

public class ProductMapper {

    public static ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getOriginCountry(),
                product.getPurchaseDate(),
                product.getJastiperId()
        );
    }

    public static Product toEntity(ProductRequest request) {

        return new Product(
                request.name,
                request.description,
                request.price,
                request.stock,
                request.originCountry,
                request.purchaseDate,
                request.jastiperId
        );
    }

    public static void updateEntity(Product product, ProductRequest request) {

        product.setName(request.name);
        product.setDescription(request.description);
        product.setPrice(request.price);
        product.setStock(request.stock);
        product.setOriginCountry(request.originCountry);
        product.setPurchaseDate(request.purchaseDate);
    }
}