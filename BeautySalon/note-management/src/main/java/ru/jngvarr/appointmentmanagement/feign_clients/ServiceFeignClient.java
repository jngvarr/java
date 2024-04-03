package ru.jngvarr.appointmentmanagement.feign_clients;

import dao.Servize;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


@FeignClient(name = "services")
public interface ServiceFeignClient {
    @RequestMapping(value = "/services/{id}", method = RequestMethod.GET)
    Servize getService(@PathVariable Long id);

    @RequestMapping(value = "/services/duration/{id}", method = RequestMethod.GET)
    int getServiceDuration(@PathVariable Long id);

}
