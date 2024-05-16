package ru.jngvarr.staffmanagement.controllers;

import dao.entities.people.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.staffmanagement.services.StaffService;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/staff")
@CrossOrigin(origins = "http://localhost:4200")
public class StaffController {
    private final StaffService staffService;

    @GetMapping
    public List<Employee> getEmployees() {
//        log.debug("getEmployees {}", staffService.getEmployees());
        return staffService.getEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return staffService.getEmployee(id);
    }

    @GetMapping("/by-phone/{phoneNumber}")
    public Employee getEmployeeByPhone(@PathVariable String phoneNumber) {
        return staffService.getEmployeeByPhone(phoneNumber);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public Employee createEmployee(@RequestBody Employee employee) {
        return staffService.addEmployee(employee);
    }

    @PutMapping("/update/{id}")
    public Employee updateEmployee(@RequestBody Employee newData, @PathVariable Long id) {
        return staffService.updateEmployee(newData, id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        staffService.delete(id);
    }

    @GetMapping("/by-name")
    public List<Employee> getEmployeesByName(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "lastName", required = false) String lastName) {
        log.debug("name={}, lastname={}", name, lastName);
        return (name.isEmpty() || lastName.isEmpty()) ?
                (name.isEmpty() ? staffService.getEmployeesByLastName(lastName) : staffService.getEmployeesByName(name)) :
                staffService.getEmployeesByFullName(name, lastName);
    }

    @GetMapping("/by-contact/{phoneNumber}")
    public List<Employee> getEmployeesByPhone(@PathVariable String phoneNumber) {
        log.debug("number={}", phoneNumber);
        return staffService.getEmployeeByContact(phoneNumber);
    }

    @GetMapping("/by-function/{function}")
    public List<Employee> getEmployeeByFunction(@PathVariable String function) {
        log.debug("number={}", function);
        return staffService.getEmployeeByFunction(function);
    }
}
