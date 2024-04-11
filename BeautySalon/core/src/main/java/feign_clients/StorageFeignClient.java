package feign_clients;


import dao.entities.Consumable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "storage", configuration = FeignClientConfiguration.class)
public interface StorageFeignClient {
    @GetMapping("/storage")
    List<Consumable> getConsumables();

    @GetMapping("/storage/{id}")
    Consumable getConsumable(@PathVariable Long id);
}
