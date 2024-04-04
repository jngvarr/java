package feign_clients;

import dao.Visit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "visits", configuration = FeignClientConfiguration.class)
public interface VisitFeignClient {
    @GetMapping("/visits")
    List<Visit> getVisits();

    @GetMapping("/visits/{id}")
    Visit getVisit(@PathVariable Long id);

    @RequestMapping(value = "/visits/create-visit", method = RequestMethod.POST)
    Visit create(@RequestBody Visit visitToAdd);

    @RequestMapping(value = "/visits/update/{id}", method = RequestMethod.PUT)
    Visit update(@RequestBody Visit visitToUpdate);

    @RequestMapping(value = "/visits/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id);
}
