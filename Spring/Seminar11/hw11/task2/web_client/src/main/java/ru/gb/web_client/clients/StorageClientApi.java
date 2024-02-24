package ru.gb.web_client.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.gb.web_client.model.Order;
import ru.gb.web_client.model.Product;

import java.util.List;

@FeignClient(name = "storage")
public interface StorageClientApi {
    @GetMapping
    List<Product> getProducts();

    @PostMapping("/{id}/reserve")
    ResponseEntity<?> reserveProduct(@PathVariable Long id, @RequestBody Order order);

    @PostMapping("/{id}/reserve/rollback")
    void rollbackReserve(@PathVariable Long id, @RequestBody Order order);

    @PostMapping("/{id}")
    ResponseEntity<?> buy(@PathVariable Long id, @RequestBody Order order);
}
