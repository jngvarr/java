package feign_clients;

import dao.Servize;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@FeignClient(name = "services", configuration = FeignClientConfiguration.class)
public interface ServiceFeignClient {
    @GetMapping("/services")
    public List<Servize> getServices();

    @RequestMapping(value = "/services/{id}", method = RequestMethod.GET)
    Servize getService(@PathVariable Long id);

    @RequestMapping(value = "/services/duration/{id}", method = RequestMethod.GET)
    int getServiceDuration(@PathVariable Long id);
}
