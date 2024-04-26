package ru.jngvarr.staffmanagement.services;

import dao.entities.people.Employee;
import exceptions.NeededObjectNotFound;
import exceptions.NotEnoughData;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.jngvarr.staffmanagement.repositories.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class StaffService {
    public final EmployeeRepository employeeRepository;

    public List<Employee> getEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        System.out.println(employees);
        return employees;
    }

    public List<Employee> getEmployeeByContact(String contact) {
        List<Employee> neededClients = employeeRepository.findAllByContact(contact);
        if (neededClients != null) return neededClients;
        else throw new IllegalArgumentException("Employee not found");
    }

    public Employee getEmployee(Long id) {
        Optional<Employee> neededEmployee = employeeRepository.findById(id);
        if (neededEmployee.isPresent()) {
            return neededEmployee.get();
        } else throw new NeededObjectNotFound("Employee not found!");
    }

    public Employee addEmployee(Employee employee) {
        log.debug("addEmployee{}", employee);
        if (employee.getFirstName() != null && employee.getFunction() != null) {
            return employeeRepository.save(employee);
        } else throw new NotEnoughData("Not enough employee data");
    }

    public Employee update(Employee employeeData, Long id) {
        Optional<Employee> oldEmployee = employeeRepository.findById(id);
        if (oldEmployee.isPresent()) {
            Employee newEmployee = oldEmployee.get();
            if (employeeData.getFirstName() != null) newEmployee.setFirstName(employeeData.getFirstName());
            if (employeeData.getLastName() != null) newEmployee.setLastName(employeeData.getLastName());
            if (employeeData.getFunction() != null) newEmployee.setFunction(employeeData.getFunction());
            if (employeeData.getContact() != null) newEmployee.setContact(employeeData.getContact());
            if (employeeData.getDob() != null) newEmployee.setDob(employeeData.getDob());
            log.debug("update {}", newEmployee);
            return employeeRepository.save(newEmployee);
        } else throw new NeededObjectNotFound("Employee not found");
    }

    public List<Employee> getClientByFullName(String name, String lastName) {
        return employeeRepository.findAllByFirstNameAndLastName(name, lastName);
    }

    public List<Employee> getClientByName(String name) {
        return employeeRepository.findAllByFirstName(name);
    }

    public List<Employee> getEmployeeByLastName(String lastName) {
        return employeeRepository.findAllByLastName(lastName);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}