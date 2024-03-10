package ru.jngvarr.webclient.fgn_clients;

import org.springframework.cloud.openfeign.FeignClient;
import ru.jngvarr.servicemamagement.model.Servize;

import java.util.List;


@FeignClient(name = "services")
public interface ServiceFGNClient {

    public List<Servize> showAll();
}
