package id.ac.ui.cs.advprog.jsonbackend.catalog.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

    private String id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String originCountry;
    private LocalDate purchaseDate;
    private String jastiperId;

    public ProductDTO() {}

    public ProductDTO(String id, String name, String description,
                      double price, int stock,
                      String originCountry, LocalDate purchaseDate,
                      String jastiperId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.originCountry = originCountry;
        this.purchaseDate = purchaseDate;
        this.jastiperId = jastiperId;
    }
}