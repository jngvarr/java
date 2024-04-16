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

    @RequestMapping(value = "/storage/create", method = RequestMethod.POST)
    Consumable addConsumable(@RequestBody Consumable consumable);

    @RequestMapping(value = "/storage/update/{id}", method = RequestMethod.PUT)
    Consumable updateConsumable(@RequestBody Consumable newData, @PathVariable Long id);

    @RequestMapping(value = "/storage/delete/{id}", method = RequestMethod.DELETE)
    void deleteConsumable(@PathVariable Long id);
}


