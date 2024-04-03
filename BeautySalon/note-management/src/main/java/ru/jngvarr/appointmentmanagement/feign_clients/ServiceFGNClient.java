package ru.jngvarr.webclient.feign_clients;

import org.springframework.cloud.openfeign.FeignClient;
import dao.Servize;

import java.util.List;


@FeignClient(name = "services")
public interface ServiceFGNClient {

    public List<Servize> showAll();
}
