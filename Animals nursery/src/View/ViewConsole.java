package View;

import Model.Animals;

import java.util.List;
import java.util.Scanner;

public class ViewConsole {
    Scanner sc = new Scanner(System.in);

    public void printAll(List<Animals> list) {
        if (list.isEmpty()) System.out.println("Список пуст.");
        else for (Animals animal : list) {
            System.out.println(animal);
        }
    }

    public String getName() {
        System.out.print("Введите имя животного: \n> ");
        return sc.nextLine();
    }
    public String getDay_of_birth() {
        System.out.print("Введите дату рождения животного(в формате гггг-мм-дд): \n> ");
        return sc.nextLine();
    }
    public String getCommands() {
        System.out.print("Введите через запятую команды, которые знает животное: \n> ");
        return sc.nextLine();
    }

}
