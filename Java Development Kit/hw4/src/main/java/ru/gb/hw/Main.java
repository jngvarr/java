package ru.gb.hw;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        EmployeesList employeesList = new EmployeesList();
        employeesList.addEmployee(new Employee("Иванов", 10, "79998887766"));
        employeesList.addEmployee(new Employee("Петров", 20, "79998886688"));
        employeesList.addEmployee(new Employee("Сидоров", 25, "79997776655"));
        employeesList.addEmployee(new Employee("Матвеева", 5, "79998886666"));
        employeesList.addEmployee(new Employee("Кузнецова", 15, "79998886666"));


        System.out.println(employeesList.getEmployeeByExp(20));
        System.out.println(employeesList.getEmployeeByExp(11));

        System.out.println(employeesList.getEmployeeByID("#1"));
        System.out.println(employeesList.getEmployeeByID("#2"));
        System.out.println(employeesList.getEmployeeByID("#3"));
        System.out.println(employeesList.getEmployeeByID("#4"));
        System.out.println(employeesList.getEmployeeByID("#5"));
        System.out.println(employeesList.getEmployeeByID("#6"));

        System.out.println(employeesList.getEmployeesPhoneNumberByName("Иванов"));
        System.out.println(employeesList.getEmployeesPhoneNumberByName("Кузнецова"));
        System.out.println(employeesList.getEmployeesPhoneNumberByName("Кузнецов"));
    }
}
