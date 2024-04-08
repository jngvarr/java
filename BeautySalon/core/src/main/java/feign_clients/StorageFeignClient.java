package feign_clients;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "storage", configuration = FeignClientConfiguration.class)
public interface StorageFeignClient {
}