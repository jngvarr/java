package ru.jngvarr.staffmanagement.repositories;

import dao.people.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
