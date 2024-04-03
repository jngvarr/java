package ru.jngvarr.appointmentmanagement.feign_clients;

import dao.people.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "clients")
public interface ClientFeignClient {

    @GetMapping("/clients/{id}")
    Client getClient(@PathVariable Long id);

}
