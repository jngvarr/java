package feign_clients;


import dao.Consumable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "storage", configuration = FeignClientConfiguration.class)
public interface StorageFeignClient {
    @GetMapping("/storage")
    List<Consumable> getConsumables();
}
