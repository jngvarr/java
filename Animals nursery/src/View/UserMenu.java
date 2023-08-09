package View;

import Controller.*;
import Model.*;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class UserMenu {

    public void start() throws IOException, SQLException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
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
                    menuController.updateAnimalData();
                    break;
                case "4":
                    menuController.getAnimalsCommands();
                    break;
                case "5":
                    menuController.trainAnimal();
                    break;
                case "6":
                    menuController.deleteAnimal();
                    break;
                case "0":
                    end = true;
            }
        }

    }

    public AnimalType animalTypeChoice() throws IOException, SQLException, ClassNotFoundException {
        AnimalType type = null;
        Scanner sc = new Scanner(System.in);
        System.out.println("Выберите тип животного: \n" +
                "1 - Домашнее животное.\n" +
                "2 - Вьючное животное.\n" +
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
                System.out.println("Выберите животное: \n" +
                        "1 - Кошка.\n" +
                        "2 - Собака.\n" +
                        "3 - Хомяк.\n" +
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
                System.out.println("Выберите животное: \n" +
                        "1 - Верблюд.\n" +
                        "2 - Осёл.\n" +
                        "3 - Лошадь.\n" +
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

    public String choseUpdate() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Выберите вариант изменения записи: \n" +
                "1 - Запись целиком.\n" +
                "2 - Имя животного.\n" +
                "3 - Дату рождения.\n" +
                "4 - Выполняемые команды.\n" +
                "2 - Вид животного.\n" +
                "0 - Возврат в предыдущее меню. )\n> ");

        return sc.next();
    }
}
