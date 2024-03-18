package ru.jngvarr.webclient.fgn_clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "staff")
public interface StaffFGNClient {
}
