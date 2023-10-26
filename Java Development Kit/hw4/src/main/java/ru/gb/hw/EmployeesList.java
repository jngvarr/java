package ru.gb.hw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeesList {
    private List<Employee> employeeList = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public String getEmployeeByExp(int exp) {
        Map<Integer, String> mapByExp = new HashMap<>();
        if (!employeeList.isEmpty()) {
            for (Employee employee : employeeList) {
                mapByExp.put(employee.getWorkExperience(), employee.getName());
            }
        }
        if (!mapByExp.containsKey(exp)) return "Сотрудника со стажем " + exp + " нет в штате.";
        return "Cотрудник со стажем " + exp + " это " + mapByExp.get(exp);
    }

    public String getEmployeesPhoneNumberByName(String name) {
        Map<String, String> mapByExp = new HashMap<>();
        if (!employeeList.isEmpty()) {
            for (Employee employee : employeeList) {
                mapByExp.put(employee.getName(), employee.getPhoneNumber());
            }
        }
        if (!mapByExp.containsKey(name)) return "Сотрудника по фамилии " + name + " нет в штате.";
        return "Сотрудник " + name + " имеет следующий телефонный номер: " + mapByExp.get(name);
    }

    public String getEmployeeByID(String id) {
        if (!employeeList.isEmpty()) {
            for (Employee employee : employeeList) {
                if (employee.getID().equals(id)) {
                    return "Табельный номер " + id + " у сотрудника по фамилии " + employee.getName();
                }
            }
        }
        return "Сотрудника с табельным номером " + id + " нет в штате.";
    }


}

