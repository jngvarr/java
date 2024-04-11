package ru.jngvarr.webclient.controllers;

import dao.entities.people.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.jngvarr.webclient.services.SalonService;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
public class SalonEmployeeController {

    private final SalonService salonService;

    @GetMapping
    public String showAll(Model model) {
        model.addAttribute("employees", salonService.getEmployees());
        return "employees";
    }

    @GetMapping("/{id}")
    public String getEmployee(Model model, @PathVariable Long id) {
        model.addAttribute("employee", salonService.getEmployee(id));
        return "employyee";
    }

    @GetMapping("/create-view")
    public String toCreateEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee-create";
    }

    @PostMapping("/create-action")
    public String addEmployee(Model model, Employee employee) {
        salonService.addEmployee(employee);
        model.addAttribute("employees", salonService.getEmployees());
        return "employees";
    }

    @GetMapping("/update-view/{id}")
    public String updateEmployeeForm(Model model, @PathVariable long id) {
        Employee oldEmployee = salonService.getEmployee(id);
        model.addAttribute("employee", oldEmployee);
        return "employee-update";
    }

    @PostMapping ("/update-action")
    public String update(@ModelAttribute("employee") Employee employee) {
        salonService.updateEmployees(employee, employee.getId());
        return "redirect:/employees";
    }

    @GetMapping("/delete-action/{id}")
    public String delete(@PathVariable Long id) {
        salonService.deleteEmployee(id);
        return "redirect:/employees";
    }
}

