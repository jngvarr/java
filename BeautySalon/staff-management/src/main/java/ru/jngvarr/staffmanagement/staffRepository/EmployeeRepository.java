package ru.jngvarr.staffmanagement.staffRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jngvarr.beautysalon.model.people.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
