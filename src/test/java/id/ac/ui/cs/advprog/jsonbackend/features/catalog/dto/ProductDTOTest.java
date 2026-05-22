package id.ac.ui.cs.advprog.jsonbackend.features.catalog.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductDTOTest {

    @Test
    void constructorAndSettersShouldExposeAllFields() {
        LocalDate purchaseDate = LocalDate.now();
        ProductDTO dto = new ProductDTO("id", "name", "desc", 1000, 3, "JP", purchaseDate, "jastiper");

        assertEquals("id", dto.getId());
        assertEquals("name", dto.getName());
        assertEquals("desc", dto.getDescription());
        assertEquals(1000, dto.getPrice());
        assertEquals(3, dto.getStock());
        assertEquals("JP", dto.getOriginCountry());
        assertEquals(purchaseDate, dto.getPurchaseDate());
        assertEquals("jastiper", dto.getJastiperId());

        dto.setId("id2");
        dto.setName("name2");
        dto.setDescription("desc2");
        dto.setPrice(2000);
        dto.setStock(4);
        dto.setOriginCountry("ID");
        dto.setPurchaseDate(purchaseDate.plusDays(1));
        dto.setJastiperId("jastiper2");

        assertEquals("id2", dto.getId());
        assertEquals("name2", dto.getName());
        assertEquals("desc2", dto.getDescription());
        assertEquals(2000, dto.getPrice());
        assertEquals(4, dto.getStock());
        assertEquals("ID", dto.getOriginCountry());
        assertEquals(purchaseDate.plusDays(1), dto.getPurchaseDate());
        assertEquals("jastiper2", dto.getJastiperId());
    }
}
