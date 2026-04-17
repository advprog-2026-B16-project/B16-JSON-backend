package id.ac.ui.cs.advprog.jsonbackend.catalog.controller;

import id.ac.ui.cs.advprog.jsonbackend.catalog.dto.ProductDTO;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // penting untuk FE
public class ProductController {

    @GetMapping
    public List<ProductDTO> getProducts() {

        return List.of(
                new ProductDTO(
                        "1",
                        "KitKat Matcha",
                        "Japanese Matcha Chocolate",
                        50000,
                        10,
                        "Japan",
                        LocalDate.of(2025,5,1),
                        "jastiper1"
                ),
                new ProductDTO(
                        "2",
                        "Pocky Strawberry",
                        "Strawberry biscuit sticks",
                        30000,
                        20,
                        "Japan",
                        LocalDate.of(2025,5,2),
                        "jastiper2"
                )
        );
    }
}