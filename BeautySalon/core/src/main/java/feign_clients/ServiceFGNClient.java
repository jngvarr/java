package feign_clients;

import dao.Servize;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;


@FeignClient(name = "services")
public interface ServiceFGNClient {

    public List<Servize> showAll();
}
