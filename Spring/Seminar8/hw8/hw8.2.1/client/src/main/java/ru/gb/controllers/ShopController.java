package ru.gb.controllers;

import gb.hw82.model.Product;
import gb.hw82.services.PaymentService;
import gb.hw82.services.ReservationService;
import gb.hw82.services.ShopServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopServiceImpl shopService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    @GetMapping
    public List<Product> showAllProducts() {
        return shopService.getAllProducts();
    }

    @PostMapping("/reserve/{productId}")
    public ResponseEntity<String> reserveProduct(@PathVariable Long productId) {
        // резервирование товара на складе
        reservationService.reserveProduct(productId);

        return ResponseEntity.ok("Product reserved successfully");
    }

    @PostMapping("/pay/{productId}/{accountId}")
    public ResponseEntity<String> payForProduct(@PathVariable Long productId, @PathVariable Long accountId) {
        // оплата товара
        paymentService.processPayment(productId, accountId);

        return ResponseEntity.ok("Payment processed successfully");
    }
}