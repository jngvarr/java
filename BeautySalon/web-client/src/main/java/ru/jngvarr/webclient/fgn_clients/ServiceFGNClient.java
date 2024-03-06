package ru.jngvarr.webclient.fgn_clients;

import org.springframework.cloud.openfeign.FeignClient;



@FeignClient(name = "services")
public interface ServiceFGNClient {

//    public List<Servize> showAll();
}
