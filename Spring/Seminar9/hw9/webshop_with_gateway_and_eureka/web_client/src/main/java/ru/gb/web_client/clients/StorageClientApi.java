package ru.gb.web_client.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = storage)
public interface StorageClientApi {

}
