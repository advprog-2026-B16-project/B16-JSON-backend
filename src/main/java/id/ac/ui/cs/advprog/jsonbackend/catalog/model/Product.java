package id.ac.ui.cs.advprog.jsonbackend.catalog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Column(length = 1000)
    private String description;

    private double price;

    private int stock;

    private String originCountry;

    private LocalDate purchaseDate;

    private String jastiperId;

    public Product() {}

    public Product(String name, String description, double price, int stock,
                   String originCountry, LocalDate purchaseDate, String jastiperId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.originCountry = originCountry;
        this.purchaseDate = purchaseDate;
        this.jastiperId = jastiperId;
    }
}
