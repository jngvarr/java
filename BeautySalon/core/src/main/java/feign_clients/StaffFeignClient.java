package feign_clients;

import dao.people.Employee;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "staff", configuration = FeignClientConfiguration.class)
public interface StaffFeignClient {

    @GetMapping("/staff")
    List<Employee> getEmployees();
    @GetMapping("/staff/{id}")
    Employee getEmployee(@PathVariable Long id);


}
