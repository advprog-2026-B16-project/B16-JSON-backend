package id.ac.ui.cs.advprog.jsonbackend.order.dto;

import id.ac.ui.cs.advprog.jsonbackend.order.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * UpdateOrderStatusRequest adalah DTO untuk menerima data perubahan status pesanan dari client.
 *
 * Cara Kerja:
 * Client (HTTP PUT /api/orders/{orderId}/status) mengirim JSON → Spring parse ke UpdateOrderStatusRequest
 *
 * Contoh request body:
 * {
 *   "nextStatus": "PURCHASED",
 *   "cancellationReason": null
 * }
 *
 * Alur transisi status yang valid:
 * PENDING → PAID → PURCHASED → SHIPPED → COMPLETED
 *                     ↓           ↓          ↓
 *                 CANCELLED   CANCELLED  (tidak bisa)
 *
 * Digunakan di:
 * OrderController.updateStatus(@PathVariable orderId, @RequestBody UpdateOrderStatusRequest request)
 * OrderService.updateStatus(String orderId, UpdateOrderStatusRequest request)
 *
 * Catatan:
 * - cancellationReason hanya diisi jika nextStatus = CANCELLED
 * - Validasi transisi status dilakukan via OrderStateFactory dan State Pattern
 */


@Getter
@Setter
public class UpdateOrderStatusRequest {
    private OrderStatus nextStatus;
    private String cancellationReason;
}