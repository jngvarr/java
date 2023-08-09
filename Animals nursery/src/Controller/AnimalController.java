package Controller;

import Exceptions.UnclosedResourceException;
import Exceptions.UncorrectedDataException;
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

    public Animals createAnimal(String id, String name, String day_of_birth, String commands, String type) throws Exception {
        Animals animal = null;
        switch (type) {
            case "Кошки":
                animal = new Cats(id, name, day_of_birth, commands, type);
                break;
            case "Собаки":
                animal = new Dogs(id, name, day_of_birth, commands, type);
                break;
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
        try (Counter counter = new Counter()) {
            assert animal != null;
            if (!animal.getName().isEmpty() && !animal.getDayOfBirth().isEmpty() && !animal.getCommands().isEmpty() && !animal.getType().isEmpty())
                counter.add();
            if (!counter.closed) throw new UnclosedResourceException("Ресурс не закрыт!");
        } catch (UnclosedResourceException e) {
            System.out.println(e.getMessage());
        }
        return animal;
    }

    public List<Animals> getAllAnimals() throws IOException {
        List<Animals> animals = nursery.getAll();
        return animals;
    }

    public String[] newAnimalData() throws Exception {
        String[] animalData = new String[4];
        animalData[3] = mainMenu.animalChoose(mainMenu.animalTypeChoice());
        animalData[0] = viewConsole.getName();
        while (!validator.dateFormatValidation(animalData[1] = viewConsole.getDay_of_birth())) {
        }
        animalData[2] = viewConsole.getCommands();
        return animalData;
    }

    public String[] partOfNewAnimalData(String[] part, String choice) throws Exception {
        String[] animalData = part;
        switch (choice) {
            case "2":
                animalData[0] = viewConsole.getName();
                break;
            case "3":
                while (!validator.dateFormatValidation(animalData[1] = viewConsole.getDay_of_birth())) ;
                break;
            case "4":
                animalData[2] = viewConsole.getCommands();
                break;
            case "5":
                animalData[3] = mainMenu.animalChoose(mainMenu.animalTypeChoice());
        }
        return animalData;
    }

    public int getID() throws IOException {
        int lastNum = 0;
        List<Animals> list = nursery.getAll();
        if (!list.isEmpty()) lastNum = Integer.parseInt(list.get(list.size() - 1).getID());
        return lastNum + 1;
    }
}
