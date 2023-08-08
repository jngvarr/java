package Controller;

import Model.Animals;
import View.UserMenu;
import View.ViewConsole;

import java.io.IOException;
import java.sql.SQLException;

public class MenuController {
    AnimalController animalController = new AnimalController();
    Nursery nursery = new Nursery();
    ViewConsole viewConsole = new ViewConsole();

    public void getAll() throws SQLException, IOException, ClassNotFoundException {
        viewConsole.printAll(animalController.getAllAnimals());
    }

    public void addNewAnimal() throws SQLException, IOException, ClassNotFoundException {
        String[] newAnimalData = animalController.newAnimalData();
        Animals addingAnimal = animalController.createAnimal(
                animalController.getID() + "", newAnimalData[0], newAnimalData[1], newAnimalData[2], newAnimalData[3]);
        nursery.addAnimal(addingAnimal);
        System.out.println("Новое животное добавлено.");
    }
    public void updateAnimalData() throws SQLException, IOException, ClassNotFoundException {
        System.out.println("Обновление данных: ");
        String updateID = viewConsole.getID();
        String[] newAnimalData = animalController.newAnimalData();
        nursery.updateData(newAnimalData, updateID);
        System.out.println("Данные обновлены.");
    }
    public void getAnimalsCommands() throws SQLException, IOException, ClassNotFoundException {
        System.out.println("Получить список команд: ");
        String comID = viewConsole.getID();
        String commands = nursery.getCommands(comID);
        viewConsole.printCommands(commands);
    }
    public void deleteAnimal() throws SQLException, IOException, ClassNotFoundException {
        System.out.println("Удаление животного: ");
        String delID = viewConsole.getID();
        nursery.deleteAnimal(delID);
        System.out.println("Запись удалена.");
    }
    public void trainAnimal() throws SQLException, IOException, ClassNotFoundException {
        System.out.println("Тренировка животного: ");
        String trainID = viewConsole.getID();
        String commands = nursery.getCommands(trainID);
        nursery.trainAnimal(commands.trim() + ", " + viewConsole.getNewCommands(), trainID);
        System.out.println("Команды разучены.");
    }
}
