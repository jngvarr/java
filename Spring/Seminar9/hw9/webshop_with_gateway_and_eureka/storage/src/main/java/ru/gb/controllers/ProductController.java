package ru.gb.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.model.Order;
import ru.gb.model.Product;
import ru.gb.services.ProductService;

import java.util.List;

/**
 * Контроллер работы с товарами.
 */
@RestController
@AllArgsConstructor
public class ProductController {
    /**
     * Сервис работы с товарами.
     */
    private final ProductService productService;

    /**
     * Получение всех товаров.
     * @return ответ со списком товаров.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(){
        return ResponseEntity.ok().body(productService.getAllProduct());
    }

    /**
     * Получение конкретного товара.
     * @param id идентификатор товара.
     * @return ответ с товаром.
     */
    @GetMapping("{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(productService.getProductById(id));
    }

    /**
     * Списание товара.
     * @param id идентификатор товара.
     * @param order объект заказа.
     * @return ответ с подтверждением успешного выполнения.
     */
    @PostMapping("{id}")
    public ResponseEntity<Void> reduceAmount(@PathVariable("id") Long id,
                                             @RequestBody Order order){
        productService.reduceAmount(id, order.getProductsAmount());
        return ResponseEntity.ok().body(null);
    }

    /**
     * Резервирование товара.
     * @param id идентификатор товара.
     * @param order объект заказа.
     * @return ответ с подтверждением успешного выполнения.
     */
    @PostMapping("{id}/reserve")
    public ResponseEntity<Void> reserveAmount(@PathVariable("id") Long id,
                                              @RequestBody Order order){
        System.out.println("amount = " + order.getProductsAmount());
        productService.reservedProduct(id, order.getProductsAmount());
        return ResponseEntity.ok().body(null);
    }

    /**
     * Откат резервации товара.
     * @param id идентификатор товара.
     * @param order объект заказа.
     * @return ответ с подтверждением успешного выполнения.
     */
    @PostMapping("{id}/reserve/rollback")
    public ResponseEntity<Void> rollbackReserveAmount(@PathVariable("id") Long id,
                                              @RequestBody Order order){
        System.out.println("Контроллер отката изменения");
        System.out.println("amount = " + order.getProductsAmount());
        productService.rollbackReservedProduct(id, order.getProductsAmount());
        return ResponseEntity.ok().body(null);
    }

}
