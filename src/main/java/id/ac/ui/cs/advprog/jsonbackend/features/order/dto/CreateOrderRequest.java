package id.ac.ui.cs.advprog.jsonbackend.features.order.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateOrderRequest {
    private String productId;
    private UUID titipersId;
    private UUID jastiperId;
    private int quantity;
    private String shippingAddress;
}
