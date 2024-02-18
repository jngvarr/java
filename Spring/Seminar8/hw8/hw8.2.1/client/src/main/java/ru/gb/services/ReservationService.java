package ru.gb.services;

import gb.hw82.aspect.LogAction;
import gb.hw82.aspect.MeasureTime;
import gb.hw82.model.Product;
import gb.hw82.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class ReservationService {


    private final ProductRepository productRepository;

    @Transactional
    @LogAction(description = "Reserving product on the warehouse")
    @MeasureTime
    public void reserveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() > 0) {
            product.setQuantity(product.getQuantity() - 1);
            productRepository.save(product);
        } else {
            throw new RuntimeException("Product out of stock");
        }
    }
}

