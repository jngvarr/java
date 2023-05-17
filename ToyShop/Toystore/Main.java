package Toystore;

import java.util.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Model model = new Model();
        Menu menu = new Menu();
        List<Toy> toysList = new ArrayList<>();
        menu.welcome();
        menu.printMenu(menu.mainMenu);
        toysList = menu.subMenu(toysList);
        String path = System.getProperty("user.dir") + "\\toysList.csv";
        model.writeToyDataToFile(toysList, path);
        menu.raffleMenu();
    }
}