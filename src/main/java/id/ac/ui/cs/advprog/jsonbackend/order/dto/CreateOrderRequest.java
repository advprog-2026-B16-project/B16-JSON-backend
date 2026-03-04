package id.ac.ui.cs.advprog.jsonbackend.order.dto;


import lombok.Getter;
import lombok.Setter;

/**
 * CreateOrderRequest adalah DTO (Data Transfer Object) untuk menerima data dari client saat checkout.
 *
 * Cara Kerja:
 * Client (HTTP POST /api/orders/checkout) mengirim JSON → Spring parse ke CreateOrderRequest
 *
 * Contoh request body:
 * {
 *   "productId": "prod-123",
 *   "titipersId": "user-456",
 *   "jastiperId": "jastiper-789",
 *   "quantity": 2,
 *   "shippingAddress": "Jl. Margonda No.1"
 * }
 *
 * Digunakan di:
 * OrderController.checkout(@RequestBody CreateOrderRequest request)
 * OrderService.checkout(CreateOrderRequest request)
 *
 * Data minimal yang wajib diisi:
 * - quantity     : jumlah barang yang dipesan
 * - shippingAddress : alamat pengiriman Titipers
 */


@Getter
@Setter
public class CreateOrderRequest {
    private String productId;
    private String titipersId;
    private String jastiperId;
    private int quantity;
    private String shippingAddress;
}
