package Controller;

import java.util.List;

import Controller.*;

public class Validator {

    public boolean dateFormatValidation(String date) {
        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) return true;
        else {
            System.out.print("Формат даты не верен.\n> ");
            return false;
        }
    }
}