package ru.jngvarr.staffmanagement.services;

import dao.people.Employee;
import exceptions.NeededObjectNotFound;
import exceptions.NotEnoughData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.jngvarr.staffmanagement.repositories.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffService {
    public final EmployeeRepository employeeRepository;

    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployee(Long id) {
        Optional<Employee> neededEmployee = employeeRepository.findById(id);
        if (neededEmployee.isPresent()) {
            return neededEmployee.get();
        } else throw new NeededObjectNotFound("Employee not found!");
    }

    public Employee addEmployee(Employee employee) {
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
            return newEmployee;
        } else throw new NeededObjectNotFound("Employee not found");
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}