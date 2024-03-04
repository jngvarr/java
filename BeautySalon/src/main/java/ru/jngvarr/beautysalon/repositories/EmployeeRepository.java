package ru.jngvarr.beautysalon.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jngvarr.beautysalon.model.people.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
