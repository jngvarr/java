package ru.jngvarr.webclient.feign_clients;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "storage")
public interface StorageFeignClient {
}
