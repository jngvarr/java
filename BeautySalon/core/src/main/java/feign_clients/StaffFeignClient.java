package feign_clients;

import dao.entities.people.Employee;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "staff", configuration = JwtFeignConfig.class)
public interface StaffFeignClient {

    @GetMapping("/staff")
    List<Employee> getEmployees();

    @GetMapping("/staff/{id}")
    Employee getEmployee(@PathVariable Long id);

    @GetMapping("/staff/by-phone/{phoneNumber}")
    Employee getEmployeeByPhone(@PathVariable String phoneNumber);

    @RequestMapping(value = "/staff/create", method = RequestMethod.POST)
    Employee addEmployee(@RequestBody Employee employeeToAdd);

    @RequestMapping(value = "/staff/update/{id}", method = RequestMethod.PUT)
    Employee update(@RequestBody Employee newData, @PathVariable Long id);

    @RequestMapping(value = "/staff/delete/{id}", method = RequestMethod.DELETE)
    void deleteEmployee(@PathVariable Long id);

}
