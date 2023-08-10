package Controller;

import Model.Animal;
import View.UserMenu;
import View.ViewConsole;

import java.io.IOException;
import java.sql.SQLException;

public class MenuController {
    AnimalController animalController = new AnimalController();
    Nursery nursery = new Nursery();
    ViewConsole viewConsole = new ViewConsole();
    UserMenu userMenu = new UserMenu();

    public void getAll() throws IOException, SQLException {
        viewConsole.printAll(animalController.getAllAnimals());
    }

    public void addNewAnimal() throws SQLException, IOException, ClassNotFoundException {
        nursery.addAnimal(animalController.newAnimalData());
        System.out.println("Новое животное добавлено.");
    }

    public void updateAnimalData() throws SQLException, IOException, ClassNotFoundException {
        Animal newAnimalData;
        System.out.println("Обновление данных: ");
        String updateID = viewConsole.getID();
        Animal animal = nursery.getAnimal(updateID);
        System.out.println("\nВносим изменения: " + animal + "\n");
        String updateChoice = userMenu.choseUpdate();
        if (updateChoice.equals("1")) newAnimalData = animalController.newAnimalData();
        else {
            newAnimalData = animalController.partOfNewAnimalData(animal, updateChoice);
        }
        nursery.updateData(newAnimalData, updateID);
        System.out.println("Данные обновлены.");
    }

    public void getAnimalsCommands() throws SQLException {
        System.out.println("Получить список команд: ");
        String comID = viewConsole.getID();
        String commands = nursery.getCommands(comID);
        viewConsole.printCommands(commands);
    }

    public void deleteAnimal() throws SQLException {
        System.out.println("Удаление животного: ");
        String delID = viewConsole.getID();
        nursery.deleteAnimal(delID);
        System.out.println("Запись удалена.");
    }

    public void trainAnimal() throws SQLException {
        System.out.println("Тренировка животного: ");
        String trainID = viewConsole.getID();
        String commands = nursery.getCommands(trainID);
        nursery.trainAnimal(commands.trim() + ", " + viewConsole.getNewCommands(), trainID);
        System.out.println("Команды разучены.");
    }
}
