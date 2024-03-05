package ru.jngvarr.webclient.fgn_clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "clients")
public interface ClientFGNClient {

}
