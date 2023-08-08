package Controller;

import Model.Animals;
import View.UserMenu;

import java.io.IOException;
import java.sql.SQLException;

public class MenuController {
    AnimalController animalController = new AnimalController();
    UserMenu mainMenu = new UserMenu();

    public void getAll() throws SQLException, IOException, ClassNotFoundException {
        animalController.getAllAnimals();
    }

    public void addNewAnimal() throws SQLException, IOException, ClassNotFoundException {
        String type = mainMenu.animalChoose(mainMenu.animalTypeChoice());
        String[] newAnimal = animalController.newAnimalData();
        Animals addingAnimal = animalController.createAnimal(
                animalController.getID() + "", newAnimal[0], newAnimal[1], newAnimal[2], type);
    }
}
