package Controller;

import java.util.List;

import Controller.*;

public class Validator {
    RegistryController controller = new RegistryController();

    public boolean signIsNotDeleted(int signNumber) {
        List<String[]> list = controller.readDataFromFile(controller.path + controller.fileName);

        if (list.get(signNumber)[1].substring(0, 1).equals("#")) return true;
        else {
            System.out.println("Действие невозможно для данной записи!!");
            return false;
        }
    }

    public boolean dateFormatValidation(String date) {
        if (date.matches("\\d{4}-\\d{2}-\\d{2}")) return true;
        else {
            System.out.print("Формат даты не верен.\n> ");
            return false;
        }
    }
}