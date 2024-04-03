package ru.jngvarr.webclient.feign_clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "staff")
public interface StaffFGNClient {
}
