package feign_clients;


import dao.entities.Consumable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "storage", configuration = FeignClientConfiguration.class)
public interface StorageFeignClient {
    @GetMapping("/storage")
    List<Consumable> getConsumables();

    @GetMapping("/storage/{id}")
    Consumable getConsumable(@PathVariable Long id);

    @GetMapping("/storage/byTitle/{title}")
    Consumable getConsumableByTitle(@PathVariable String title);

    @PostMapping("/storage/create")
    Consumable addConsumable(@RequestBody Consumable consumable);

    @PutMapping("/storage/update/{id}")
    Consumable updateConsumable(@RequestBody Consumable newData, @PathVariable Long id);

    @DeleteMapping("/storage/delete/{id}")
    void deleteConsumable(@PathVariable Long id);
}


