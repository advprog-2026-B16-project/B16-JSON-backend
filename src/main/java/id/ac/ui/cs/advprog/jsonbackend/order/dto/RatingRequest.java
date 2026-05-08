package id.ac.ui.cs.advprog.jsonbackend.order.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * RatingRequest adalah DTO untuk menerima data penilaian dari Titipers setelah pesanan COMPLETED.
 *
 * Cara Kerja:
 * Client (HTTP PUT /api/orders/{orderId}/rating) mengirim JSON → Spring parse ke RatingRequest
 *
 * Contoh request body:
 * {
 *   "jastiperRating": 5,
 *   "productRating": 4
 * }
 *
 * Digunakan di:
 * OrderController.submitRating(@PathVariable orderId, @RequestBody RatingRequest request)
 * OrderService.submitRating(String orderId, RatingRequest request)
 *
 * Aturan bisnis:
 * - Rating hanya bisa dikirim jika status pesanan = COMPLETED
 * - jastiperRating : penilaian kinerja jasa Jastiper
 * - productRating  : penilaian kualitas barang yang dibelikan
 *
 * TODO: Data rating ini akan dikirim ke Modul Profil untuk update reputasi Jastiper dan rating produk
 */


@Getter
@Setter
public class RatingRequest {
    private Integer jastiperRating;
    private Integer productRating;
}
