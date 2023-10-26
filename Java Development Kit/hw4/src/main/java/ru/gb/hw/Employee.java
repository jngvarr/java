package ru.gb.hw;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    protected String name;
    protected int workExperience;
    protected String phoneNumber;
    protected static int number;
    protected String id;

    public Employee(String name, int workExperience, String phoneNumber) {
        this.name = name;
        this.workExperience = workExperience;
        this.phoneNumber = phoneNumber;
        this.id = "#" + ++number;
    }

    public String getName() {
        return name;
    }

    public int getWorkExperience() {
        return workExperience;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getID() {
        return id;
    }
}
