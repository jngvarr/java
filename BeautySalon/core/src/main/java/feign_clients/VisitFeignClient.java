package feign_clients;

import dao.Visit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@FeignClient(name = "visits", configuration = FeignClientConfiguration.class)
@FeignClient(name = "visits")
public interface VisitFeignClient {
    @GetMapping("/visits")
    List<Visit> getVisits();

    @GetMapping("/visits/{id}")
    Visit getVisit(@PathVariable Long id);

    @RequestMapping(value = "/visits/create-client", method = RequestMethod.POST)
    Visit addClient(@RequestBody Visit clientToAdd);





}
