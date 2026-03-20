package id.ac.ui.cs.advprog.jsonbackend.module.catalog.mapper;

import id.ac.ui.cs.advprog.jsonbackend.module.catalog.dto.ProductDTO;
import id.ac.ui.cs.advprog.jsonbackend.module.catalog.model.Product;

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

    public static Product toEntity(ProductDTO dto) {
        Product product = new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setOriginCountry(dto.getOriginCountry());
        product.setPurchaseDate(dto.getPurchaseDate());
        product.setJastiperId(dto.getJastiperId());

        return product;
    }
}