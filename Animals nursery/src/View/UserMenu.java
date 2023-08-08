package View;

import Controller.*;
import Model.*;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class UserMenu {

    public void start() throws IOException, SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        Nursery nursery = new Nursery();
        MenuController menuController = new MenuController();
        boolean end = false;

        while (!end) {
            System.out.println(
                    "\n" + "Выберите действие:\n" +
                            "1 - Список всех животных.\n" +
                            "2 - Добавить новое животное.\n" +
                            "3 - Корректировка существующих данных.\n" +
                            "4 - Посмотреть список команд, которые умеет выполнять животное.\n" +
                            "5 - Дрессировка.\n" +
                            "6 - Удалить запись.\n" +
                            "0 - Выйти.");
            String choice = sc.next();
            switch (choice) {
                case "1":
                    menuController.getAll();
                    break;
                case "2":
                    menuController.addNewAnimal();
                    break;
                case "3":
                    //animalController.updateAnimalData();
                    break;
                case "4":
                    //animalController.animalsCommands();
                    break;
                case "5":
                    //animalController.training();
                    break;
                case "6":
                   // animalController.deleteAnimal();
                    break;
                case "0":
                    end = true;
            }
        }

    }

    public AnimalType animalTypeChoice() throws IOException, SQLException, ClassNotFoundException {
        AnimalType type = null;
        Scanner sc = new Scanner(System.in);
        System.out.println("Какое животное добавляем: \n" +
                "1 - Добавить домашнее животное.\n" +
                "2 - Добавить вьючное животное.\n" +
                "0 - Возврат в предыдущее меню. )");
        String choice;
        boolean end = false;
        AnimalType animalType;
        choice = sc.next();
        switch (choice) {
            case "1", "2":
                type = AnimalType.getType(choice);
                break;
            case "0":
                start();
        }
        return type;
    }

    public String animalChoose(AnimalType type) throws IOException, SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        String choice;
        switch (type) {
            case HOME -> {
                System.out.println("Какое животное добавить: \n" +
                        "1 - Добавить кошку.\n" +
                        "2 - Добавить собаку.\n" +
                        "3 - Добавить хомяка.\n" +
                        "0 - Возврат в предыдущее меню. ");
                choice = sc.next();
                switch (choice) {
                    case "1":
                        return "Кошки";
                    case "2":
                        return "Собаки";
                    case "3":
                        return "Хомяки";
                    case "0":
                        animalTypeChoice();
                }
            }
            case PACK -> {
                System.out.println("Какое животное добавить: \n" +
                        "1 - Добавить верблюда.\n" +
                        "2 - Добавить осла.\n" +
                        "3 - Добавить лошадь.\n" +
                        "0 - Возврат в предыдущее меню. ");
                choice = sc.next();
                switch (choice) {
                    case "1":
                        return "Вербдюды";
                    case "2":
                        return "Ослы";
                    case "3":
                        return "Лошади";
                    case "0":
                        animalTypeChoice();
                }
            }
        }
        return null;
    }
}
