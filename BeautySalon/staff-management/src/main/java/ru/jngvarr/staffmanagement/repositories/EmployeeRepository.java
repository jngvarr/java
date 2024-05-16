package ru.jngvarr.staffmanagement.repositories;

import dao.entities.people.Client;
import dao.entities.people.Employee;
import dao.entities.people.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByFirstNameAndLastName(String name, String lastName);

    List<Employee> findAllByFirstName(String name);

    List<Employee> findAllByLastName(String lastName);

    List<Employee> findAllByContact(String contact);

    List<Employee> findAllByFunction(Function function);

    Employee getEmployeeByContact(String phoneNumber);
}
