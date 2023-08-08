package Controller;

import Model.*;
import View.UserMenu;
import View.ViewConsole;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AnimalController {
    RegistryController registryController = new RegistryController();
    Validator validator = new Validator();
    Nursery nursery = new Nursery();
    UserMenu mainMenu = new UserMenu();
    ViewConsole viewConsole = new ViewConsole();

    public Animals createAnimal(String id, String name, String day_of_birth, String commands, String type) throws IOException {
        Animals animal = null;
        {
        }
        ;
        switch (type) {
            case "Кошки":
                animal = new Cats(id, name, day_of_birth, commands, type);
                break;
            case "Собаки":
                animal = new Dogs(id, name, day_of_birth, commands, type);
            case "Хомяки":
                animal = new Hamsters(id, name, day_of_birth, commands, type);
                break;
            case "Верблюды":
                animal = new Camels(id, name, day_of_birth, commands, type);
                break;
            case "Ослы":
                animal = new Donkeys(id, name, day_of_birth, commands, type);
                break;
            case "Лошади":
                animal = new Horses(id, name, day_of_birth, commands, type);
        }
        new Counter().add();
        return animal;
    }

    public void updateAnimalData() throws SQLException, IOException, ClassNotFoundException {
        int noteNum = Integer.parseInt(viewConsole.getID());
            String[] newData = newAnimalData();
            System.out.println("Данные были изменены.");

    }

    public void deleteAnimal() throws IOException {
        Scanner sc = new Scanner(System.in);
        try {
            getAllAnimals();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Введите номер записи для удаления.");
        int deleteNum = Integer.parseInt(sc.next());
        if (validator.signIsNotDeleted(deleteNum)) {
            String[] deleted = new String[]{"This", "animal", "was", "deleted."};
            registryController.deleteAnimalData(deleted, deleteNum);
            System.out.println("Животное удалено.");
        }
    }

    public List<Animals> getAllAnimals() throws SQLException, ClassNotFoundException, IOException {
        List<Animals> animals = nursery.getAll();
        return animals;
    }

    public void animalsCommands() throws SQLException, ClassNotFoundException, IOException {
        Scanner sc = new Scanner(System.in);
        getAllAnimals();
        System.out.println("Введите номер записи для просмотра команд.");
        int commandsNum = Integer.parseInt(sc.next());
        if (validator.signIsNotDeleted(commandsNum)) registryController.commandsList(commandsNum);
    }

    public void training() throws IOException {
        Scanner sc = new Scanner(System.in);
        try {
            getAllAnimals();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Кого тренируем? Введите номер записи.");
        int trainingNum = Integer.parseInt(sc.next());
        if (validator.signIsNotDeleted(trainingNum)) {
            System.out.println("Введите команду для тренировки");
            String newCommand = sc.next();
            registryController.training(trainingNum, newCommand);
            System.out.println("Команда разучена!");
        }
    }

    public String[] newAnimalData() throws SQLException, IOException, ClassNotFoundException {
        String[] animalData = new String[4];
        animalData[3] = mainMenu.animalChoose(mainMenu.animalTypeChoice());
        animalData[0] = viewConsole.getName();
        while (!validator.dateFormatValidation(animalData[1] = viewConsole.getDay_of_birth())) {
        }
        animalData[2] = viewConsole.getCommands();
        return animalData;
    }

    public int getID() throws SQLException, IOException, ClassNotFoundException {
        int lastNum = 0;
        List<Animals> list = nursery.getAll();
        if (!list.isEmpty()) lastNum = Integer.parseInt(list.get(list.size() - 1).getID());
        return lastNum + 1;
    }

}
