package Controller;

import Model.*;
import View.UserMenu;
import View.ViewConsole;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AnimalController {
    Validator validator = new Validator();
    Nursery nursery = new Nursery();
    UserMenu mainMenu = new UserMenu();
    ViewConsole viewConsole = new ViewConsole();

    public static Animal createAnimal(String id, String name, String day_of_birth, String commands, String type) {
        Animal animal = switch (type) {
            case "Кошки" -> new Cats(id, name, day_of_birth, commands, type);
            case "Собаки" -> new Dogs(id, name, day_of_birth, commands, type);
            case "Хомяки" -> new Hamsters(id, name, day_of_birth, commands, type);
            case "Верблюды" -> new Camels(id, name, day_of_birth, commands, type);
            case "Ослы" -> new Donkeys(id, name, day_of_birth, commands, type);
            case "Лошади" -> new Horses(id, name, day_of_birth, commands, type);
            default -> null;
        };
        Counter.increase();
        return animal;
    }

    public List<Animal> getAllAnimals() throws IOException, SQLException {
        List<Animal> animals = nursery.getAll();
        return animals;
    }

    public Animal newAnimalData() throws Exception {
        String type = mainMenu.animalChoose(mainMenu.animalTypeChoice());
        String name = viewConsole.getName();
        String dob;
        while (!validator.dateFormatValidation( dob = viewConsole.getDayOfBirth())) {
        }
        String commands = viewConsole.getCommands();
        return AnimalController.createAnimal(null, name, dob, commands, type);
    }

    public Animal partOfNewAnimalData(Animal animal, String choice) throws Exception {
        switch (choice) {
            case "2":
                animal.setName(viewConsole.getName());
                break;
            case "3":
                String dob;
                while (!validator.dateFormatValidation(dob = viewConsole.getDayOfBirth())) ;
                animal.setDayOfBirth(dob);
                break;
            case "4":
                animal.setCommands(viewConsole.getCommands());
                break;
            case "5":
                animal.setType(mainMenu.animalChoose(mainMenu.animalTypeChoice()));
        }
        return animal;
    }

    public int getID() throws IOException, SQLException {
        int lastNum = 0;
        List<Animal> list = nursery.getAll();
        if (!list.isEmpty()) lastNum = Integer.parseInt(list.get(list.size() - 1).getID());
        return lastNum + 1;
    }
}
