package feign_clients;

import dao.entities.Servize;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "services", configuration = FeignClientConfiguration.class)
public interface ServiceFeignClient {
    @GetMapping("/services")
    public List<Servize> getServices();

    @RequestMapping(value = "/services/{id}", method = RequestMethod.GET)
    Servize getService(@PathVariable Long id);

    @RequestMapping(value = "/services/duration/{id}", method = RequestMethod.GET)
    int getServiceDuration(@PathVariable Long id);

    @PostMapping("/services/create")
    void addService(Servize service);

    @RequestMapping(value = "/services/update/{id}", method = RequestMethod.PUT)
    Servize updateService(@RequestBody Servize newData, @PathVariable Long id);

    @DeleteMapping("/services/delete/{id}")
    void deleteService(@PathVariable Long id);
}
