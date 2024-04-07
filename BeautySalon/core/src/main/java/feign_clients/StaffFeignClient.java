package feign_clients;

import dao.people.Client;
import dao.people.Employee;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "staff", configuration = FeignClientConfiguration.class)
public interface StaffFeignClient {

    @GetMapping("/staff")
    List<Employee> getEmployees();
    @GetMapping("/staff/{id}")
    Employee getEmployee(@PathVariable Long id);
    @RequestMapping(value = "/staff/create", method = RequestMethod.POST)
    Employee addEmployee(@RequestBody Employee employeeToAdd);

    @RequestMapping(value = "/staff/update/{id}", method = RequestMethod.PUT)
    Employee update(@RequestBody Employee newData, @PathVariable Long id);
    @RequestMapping(value = "/staff/delete/{id}", method = RequestMethod.DELETE)
    void deleteEmployee(@PathVariable Long id);

}
